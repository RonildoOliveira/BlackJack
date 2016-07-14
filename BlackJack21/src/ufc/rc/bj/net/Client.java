package ufc.rc.bj.net;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ufc.rc.bj.conf.Config;
import ufc.rc.bj.obj.Carta;

public class Client implements Runnable {
	
	private Socket socket;
	private ObjectOutputStream oOutStream;
	private ObjectInputStream oInSreamIn;
	
	private List<Carta> mao;
	
	private JFrame frame;
	
	private Thread thread;

	private Pintor pintor;
	String line = "";
	
	private Font fonte;
	
	int jOption; // N Y C
	int qtdAs = 0; //Algoritmo
	
	//habilita uma unica leitura da soma das cartas do servidor
	boolean recebeSoma = true;
	int somaServer;
	
	public Client(String ipServer, int portaServer) throws ClassNotFoundException{
		
		System.out.println("Conectando ...");

		try{
			socket = new Socket(ipServer, portaServer);
			System.out.println("Conectado a " + socket);
			
			iniciarCliente();
			
		}catch(UnknownHostException e){
			System.out.println("Host não conhecido: " + e.getMessage());
		}catch(IOException e){
			System.out.println("Exceção de Entrada e Saída: " + e.getMessage());
		}
		
		mao = new ArrayList<Carta>();
		
		pintor = new Pintor();
		pintor.setPreferredSize(
				new Dimension(Config.LARGURA, Config.ALTURA));
		
		fonte = new Font("Verdana", Font.BOLD, 20);
		
		frame = new JFrame();
		frame.setTitle(Config.NOME);
		frame.setContentPane(pintor);
		frame.setSize(Config.LARGURA, Config.ALTURA);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
			
		frame.setVisible(true);
				
		thread = new Thread(this, Config.NOME);
		thread.start();
		
	}
		
	public void iniciarCliente() throws IOException {  
		oOutStream = new ObjectOutputStream(socket.getOutputStream());
		oInSreamIn = new ObjectInputStream(socket.getInputStream());
	}
	
	public void finalizarCliente(){
		try {
			if (oOutStream != null)	oOutStream.close();
			if (oInSreamIn != null) oInSreamIn.close();
			if (socket     != null) socket.close();
		}
		catch(IOException e){
			System.out.println("Erro de Entrada e Saída: "+e.getMessage());
		}
	}
	
	public int calculaPontos(){
		int soma = 0;
		
		for (Carta carta : mao) {
			if(mao.size() > 1 && qtdAs > 0 
			   && carta.getNome().contains("A")){
				carta.setValor(1);
			}
			soma+=carta.getValor();
		}
		return soma;
	}
	
	public int somaParcial(){
		int soma = 0;
		
		for (Carta carta : mao) {
			if(!carta.getNome().contains("A")){
				soma+=carta.getValor();
			}
		}
		
		if(soma <= 10 && qtdAs == 1){
			for (Carta carta : mao) {
				if(carta.getNome().contains("A")){
					carta.setValor(11);
				}
			}
		}
		
		return soma;
	}
	
	@Override
	public void run() {
		while (!line.equals(Config.PARAR) && mao.size() < 12){
			jOption = JOptionPane.showConfirmDialog(null, "Pedir Carta ?");
			
			if(recebeSoma){
				try {
					somaServer = (int)oInSreamIn.readObject();
				}catch(IOException ioe){
					System.out.println(ioe.getMessage());
				}catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				recebeSoma = false;
			}
			
			
			if(jOption == 0){ // YES
				try{  
					oOutStream.writeObject(line);
					oOutStream.flush();
				}catch(IOException e){  
					System.err.println("Erro de Entrada e Saída: " + e.getMessage());
				}
			}
			
			if(jOption == 1){ // NO = 1
				mostrarResultado();
				try {
					oOutStream.writeObject(Config.PARAR);
					oOutStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			
			if(jOption == 2) // Cancel = 2
				continue;
			
			try {
				//acidionar na mao
				Carta c = (Carta) oInSreamIn.readObject();
				
				//util no algoritmo de calcular
				if(c.getNome().contains("A")){
					qtdAs++;
				}
				
				mao.add(new Carta(c.getNome(),
							      c.getValor(),
							      ImageIO.read(getClass().getResourceAsStream("/"+c.getNome()+".png"))));
				pintor.repaint();
				
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	} 
	
	private void mostrarResultado(){

			if(calculaPontos() == 21 && somaServer != 21){
				JOptionPane.showMessageDialog(null, "VOCE VENCEU! "
						+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
			}
			
			if(calculaPontos() != 21 && somaServer == 21){
				JOptionPane.showMessageDialog(null, "VOCE PERDEU! "
						+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
			}
			
			if(calculaPontos() < 21){
				if(somaServer > 21){
					JOptionPane.showMessageDialog(null, "VOCE VENCEU! "
							+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
				}
				
				if(21 - calculaPontos() < 21 - somaServer){
					JOptionPane.showMessageDialog(null, "VOCE VENCEU! "
							+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
				}else{
					JOptionPane.showMessageDialog(null, "VOCE PERDEU! "
							+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
				}
			}
			
			if(calculaPontos() > 21){
				if(somaServer < 21){
					JOptionPane.showMessageDialog(null, "VOCE PERDEU! "
							+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
				}
				if(somaServer > 21){
					JOptionPane.showMessageDialog(null, "EMPATE! " 
							+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
				}
			}
			
			if(somaServer == calculaPontos() && 
			   somaServer < 21 && calculaPontos() < 21){
				JOptionPane.showMessageDialog(null, "EMPATE! " 
						+ "\nSRV " + somaServer+ " x CLI "+calculaPontos());
			}

	}
	
	private void render(Graphics g) {
		
		imprimirTexto("PONTOS: "+calculaPontos(), g, Color.BLACK, fonte);		
		
		//Imprime organizado 6 x 2 (Muito feio)
		for (int i = 0; i < mao.size(); i++) {
			if(i < 6){
				g.drawImage(mao.get(i).getImagem(), i + (30*i) +  94, 80, null);
			}
			else{
				g.drawImage(mao.get(i).getImagem(), (i-7) + (30*(i-7)) +  146, 160, null);
			}
		}
	}
	
	void imprimirTexto(String mensagem, Graphics g, Color color, Font font){
		g.setColor(color);
		g.setFont(font);
		Graphics2D g2 = (Graphics2D) g;
			int tamanho = g2.getFontMetrics().stringWidth(mensagem);
			g.drawString(mensagem, 
					Config.LARGURA/2 - tamanho/2,
					Config.ALTURA - tamanho / 2);
	}

	private class Pintor extends JPanel {
		private static final long serialVersionUID = 1L;

		public Pintor() {
			setFocusable(true);
			requestFocus();
			setBackground(Color.WHITE);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			render(g);
		}
		
	}

	public static void main(String[] args) {
		try {
			@SuppressWarnings("unused")
			Client client = new Client(Config.IP, Config.PORTA);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
