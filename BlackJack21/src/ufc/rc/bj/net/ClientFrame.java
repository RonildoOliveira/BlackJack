package ufc.rc.bj.net;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ufc.rc.bj.conf.Config;
import ufc.rc.bj.obj.Carta;

public class ClientFrame implements Runnable, ActionListener {
	
	private Socket socket              = null;
	private ObjectOutputStream streamOut = null;
	private ObjectInputStream streamIn = null;
	
	private List<Carta> mao;
	
	private JFrame frame;
	
	private JButton botaoParar;
	private JButton botaoPedir;
	
	private Thread thread;

	private Painter painter;
	String line = "";
	
	private Font fonte;
	
	boolean pedido = false;
	int jOption;
	int qtdAs = 0;
	
	public ClientFrame(String serverName, int serverPort) throws ClassNotFoundException{
		
		System.out.println("Conectando ...");

		try{
			socket = new Socket(serverName, serverPort);
			System.out.println("Conectado a " + socket);
			start();
		}catch(UnknownHostException e){
			System.out.println("Host não conhecido: " + e.getMessage());
		}catch(IOException e){
			System.out.println("Exceção de Entrada e Saída: " + e.getMessage());
		}
		
		mao = new ArrayList<Carta>();
		
		painter = new Painter();
		painter.setPreferredSize(
				new Dimension(Config.LARGURA, Config.ALTURA));
		
		fonte = new Font("Verdana", Font.BOLD, 20);
		
		frame = new JFrame();
		frame.setTitle(Config.NOME);
		frame.setContentPane(painter);
		frame.setSize(Config.LARGURA, Config.ALTURA);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
			
		frame.setVisible(true);
		
		botaoParar = new JButton("PARAR");
		botaoPedir = new JButton("PEDIR");
		
//		frame.add(botaoParar);
//		frame.add(botaoPedir);
		
		thread = new Thread(this, Config.NOME);
		thread.start();
		
	}
		
	public void start() throws IOException
	{  
		streamOut = new ObjectOutputStream(socket.getOutputStream());
		streamIn = new ObjectInputStream(socket.getInputStream());
	}
	
	public void stop(){
		try {
			if (streamOut != null)  streamOut.close();
			if (streamIn != null)  streamIn.close();
			if (socket    != null)  socket.close();
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
	
	public void run() {
		while (!line.equals(Config.PARAR)){
			jOption = JOptionPane.showConfirmDialog(null, "Pedir Carta ?");
			
			if(jOption == 0){ // YES
				try{  
					//line = JOptionPane.showInputDialog("Mensagem: ");
					streamOut.writeObject(line);
					streamOut.flush();
				}catch(IOException e){  
					System.out.println("Erro de Entrada e Saída: " + e.getMessage());
				}
			}
			
			if(jOption == 1) // NO = 1 
				break;
			
			if(jOption == 2) // Cancel = 2
				continue;
			
			try {
				//acidionar na mao
				Carta c = (Carta) streamIn.readObject();
				
				//util no algoritmo de calcular
				if(c.getNome().contains("A")){
					qtdAs++;
				}
				
				mao.add(new Carta(c.getNome(),
							c.getValor(),
							ImageIO.read(getClass().getResourceAsStream("/"+c.getNome()+".png"))));
				painter.repaint();
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			pedido = false;
		}

	}

	private void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(fonte);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int larguraString = g2.getFontMetrics().stringWidth("PONTOS: "+calculaPontos()+"");

		g.drawString("PONTOS: "+calculaPontos()+"", 
				Config.LARGURA/2 - larguraString/2,
				Config.ALTURA - larguraString / 2);
		
		//Imprime organizado 6 x 2 (Muito feio)
		for (int i = 0; i < mao.size(); i++) {
			if(i < 6){
				g.drawImage(mao.get(i).getImagem(), i + (52*i) +  94, 80, null);
			}
			else{
				g.drawImage(mao.get(i).getImagem(), (i-7) + (52*(i-7)) +  146, 160, null);
			}
		}

	}

	private class Painter extends JPanel {
		private static final long serialVersionUID = 1L;

		public Painter() {
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
			ClientFrame client = new ClientFrame("localhost", 9090);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		botaoPedir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		
		botaoParar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
	}
}
