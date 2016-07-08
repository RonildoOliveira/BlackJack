package ufc.rc.bj.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ufc.rc.bj.conf.Config;
import ufc.rc.bj.obj.Carta;

public class Server{
	private Socket socket = null;
	private ServerSocket socketServer = null;
	private ObjectInputStream oInStream = null;
	private ObjectOutputStream oOutStreamOut = null;

	private List<Carta> baralho;

	public Server(int porta) throws ClassNotFoundException{
		try {
			System.out.println("[" + porta + "]");
			socketServer = new ServerSocket(porta);  
			System.out.println("Iniciando Servidor: " + socketServer);
			System.out.println("Esperando por cliente ..."); 
			socket = socketServer.accept();
			System.out.println("Conexão estabelecida: " + socket);

			//Abre fluxos de entrada e saída
			abrir();

			baralho = new ArrayList<Carta>();
			iniciaBaralho();

			//recebe a parada da conexao
			boolean parado = false; 
			String comando = "";

			boolean cartasServer = true;

			while (!parado || baralho.isEmpty()){

				if(cartasServer){
					try {
						//Envia soma das cartas do servidor
						oOutStreamOut.writeObject(iniciarMaoServer());
						oOutStreamOut.flush();
					} catch (Exception e) {

					}
					cartasServer = false;
				}

				try {
					//recebe
					comando = (String) oInStream.readObject();
					parado = comando.equals(Config.PARAR);
				}catch(IOException ioe){
					parado = true;
				}

				try {
					//Sorteia
					int rm = new Random().nextInt()%baralho.size();
					if(rm < 0)
						rm*=-1;

					oOutStreamOut.writeObject(baralho.get(rm));
					oOutStreamOut.flush();
					baralho.remove(rm);
				} catch (Exception e) {

				}

			}

			//fecha fluxos de entrada e saida
			fechar();
		}
		catch(IOException e){
			System.out.println(e); 
		}
	}

	private int iniciarMaoServer(){

		int soma = 0;

		while (soma <= 16){

			int rm = new Random().nextInt()%baralho.size();
			if(rm < 0)
				rm*=-1;

			soma+=baralho.get(rm).getValor();
			baralho.remove(rm);
		}

		return soma;
	}

	private void iniciaBaralho() {

		String [] naipes = {"c","h","s","d"};

		for (int i = 0; i < naipes.length; i++) {
			for (int j = 2; j <= 10; j++) {
				baralho.add(new Carta(j+""+naipes[i], j));
			}
		}

		for (int i = 0; i < naipes.length; i++) {
			baralho.add(new Carta("K"+naipes[i], 10));
			baralho.add(new Carta("Q"+naipes[i], 10));
			baralho.add(new Carta("J"+naipes[i], 10));
			baralho.add(new Carta("A"+naipes[i], 10));
		}
	}

	public void abrir() throws IOException
	{  
		oInStream = new ObjectInputStream(socket.getInputStream());
		oOutStreamOut = new ObjectOutputStream(socket.getOutputStream());
	}

	public void fechar() throws IOException
	{  
		if (socket != null)    socket.close();
		if (oInStream != null)  oInStream.close();
		if (oOutStreamOut != null) oOutStreamOut.close();
	}

	public static void main(String args[]) throws ClassNotFoundException{

		@SuppressWarnings("unused")
		Server server = new Server(Config.PORTA);
	}
}

