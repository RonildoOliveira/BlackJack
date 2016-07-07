package ufc.rc.bj.net;

import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import ufc.rc.bj.obj.Carta;

public class Client
{
	private Socket socket              = null;
	private DataInputStream  console   = null;
	private ObjectOutputStream streamOut = null;
	private ObjectInputStream streamIn = null;
	
	private List<Carta> mao;

	public Client(String serverName, int serverPort) throws ClassNotFoundException{

		System.out.println("Establishing connection. Please wait ...");

		try{
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			start();
		}catch(UnknownHostException uhe){
			System.out.println("Host unknown: " + uhe.getMessage());
		}catch(IOException ioe){
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
		
		mao = new ArrayList<Carta>();
		String line = "";

		while (!line.equals(".bye")){
			
			try{  
				line = JOptionPane.showInputDialog("Mensagem: ");
				streamOut.writeObject(line);
				streamOut.flush();
			}catch(IOException ioe){  
				System.out.println("Sending error: " + ioe.getMessage());
			}

			try {
				//JOptionPane.showMessageDialog(null, streamIn.readUTF());
				//acidionar na mao
				mao.add(new Carta((String) streamIn.readObject(), 5));
				JOptionPane.showMessageDialog(null, mao.size());
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		/*********************/
		System.out.println(calculaPontos());
	}
	
	public void start() throws IOException
	{  
		console   = new DataInputStream(System.in);
		streamOut = new ObjectOutputStream(socket.getOutputStream());
		streamIn = new ObjectInputStream(socket.getInputStream());
	}
	
	public void stop(){
		try {
			
			if (console   != null)  console.close();
			if (streamOut != null)  streamOut.close();
			if (streamIn != null)  streamIn.close();
			if (socket    != null)  socket.close();
		}
		catch(IOException ioe){
			System.out.println("Error closing ...");
		}
	}
	
	public int calculaPontos(){
		int soma = 0;
		
		for (Carta carta : mao) {
			soma+=carta.getValor();
		}
		
		return soma;
	}
	
	public static void main(String args[]) throws ClassNotFoundException
	{  
		@SuppressWarnings("unused")
		Client client = new Client("localhost", 9090);
	}
	
}
