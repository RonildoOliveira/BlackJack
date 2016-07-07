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
	private Socket          socket   = null;
	private ServerSocket    server   = null;
	private ObjectInputStream streamIn =  null;
	private ObjectOutputStream streamOut =  null;

	private List<Carta> baralho;
		
	public Server(int port) throws ClassNotFoundException{
		try {
			System.out.println("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);  
			System.out.println("Server started: " + server);
			System.out.println("Waiting for a client ..."); 
			socket = server.accept();
			System.out.println("Client accepted: " + socket);
			open();
			
			baralho = new ArrayList<Carta>();
			
			iniciaBaralho();
			
			boolean done = false;
			String line = "";
			
			while (!done || baralho.isEmpty()){

				try {
					//recebe
					line = (String) streamIn.readObject();
					System.out.println(line);
					done = line.equals(Config.PARAR);
				}catch(IOException ioe){
					done = true;
				}
				
					try {
						//Sorteia
						int rm = new Random().nextInt()%baralho.size();
						if(rm < 0)
							rm*=-1;
												
						streamOut.writeObject(baralho.get(rm));
						streamOut.flush();
						baralho.remove(rm);
					} catch (Exception e) {

					}

			}
			close();
		}
		catch(IOException ioe){
			System.out.println(ioe); 
		}
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

	public void open() throws IOException
	{  
		streamIn = new ObjectInputStream(socket.getInputStream());
		streamOut = new ObjectOutputStream(socket.getOutputStream());
	}

	public void close() throws IOException
	{  
		if (socket != null)    socket.close();
		if (streamIn != null)  streamIn.close();
		if (streamOut != null) streamOut.close();
	}

	public static void main(String args[]) throws ClassNotFoundException{
		
		@SuppressWarnings("unused")
		Server server = new Server(9090);
	}
}

