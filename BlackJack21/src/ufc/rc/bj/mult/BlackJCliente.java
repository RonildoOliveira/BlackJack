package ufc.rc.bj.mult;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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


public class BlackJCliente extends Thread {

	static List<String> mao;
	
	List<Carta> minhaMao;
	
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	
	String nomeUsuario = getNome();
	
	JFrame frame = new JFrame(nomeUsuario);

	private Font fonte;

	JButton btPedir = new JButton("PEDIR");
	JButton btParar = new JButton("PARAR");

	Pintor pintor;
	int pontosServidor;
	
	int quantidadeAses = 0; //Algoritmo
	
	public BlackJCliente() {
		
		minhaMao = new ArrayList<Carta>();
		mao = new ArrayList<String>();

		fonte = new Font("Verdana", Font.BOLD, 20);

		// Layout GUI
		pintor = new Pintor();
		pintor.setPreferredSize(
				new Dimension(Config.LARGURA, Config.ALTURA));

		frame.setContentPane(pintor);
		frame.setSize(Config.LARGURA, Config.ALTURA);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame.getContentPane().add(btPedir);
		frame.getContentPane().add(btParar);
		
		frame.pack();

		//action pedir
		btPedir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				out.println(Config.PEDIR);
				out.flush();
				pintor.repaint();
			}
		});

		//action parar
		btParar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				out.println(Config.PARAR);
				out.flush();
				
				//decidePartida();
				
				btPedir.setEnabled(false);
			}
		});
		
		pintor.repaint();
	}

	//pega o nome do jogador
	public String getNome() {
		return JOptionPane.showInputDialog(
				frame,
				"Nome:",
				"Qual o seu nome?",
				JOptionPane.PLAIN_MESSAGE);
	}

	//inicia 
	@Override
	public void run() {
		
		try {
			inicializaSockets();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Pega duas cartas
		try {
			String linha = in.readLine();
			mao.add(linha.substring(5));
			minhaMao.add(gerarCarta(linha.substring(5)));
			linha = in.readLine();
			mao.add(linha.substring(6));
			minhaMao.add(gerarCarta(linha.substring(6)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		pintor.repaint();
		
		// Processamento de mensagens
		while (true) {

			String line = null;
			
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//servidor envia uma carta
			if (line.contains("BEGIN")) {
				mao.add(line.substring(6));
				
				//cria o objeto carta na lista
				minhaMao.add(gerarCarta(line.substring(6)));
				
				if(line.substring(6).contains("A")){
					quantidadeAses++;
				}
			}
			//parar partida 
			else if (line.contains("NUB")) {
				pontosServidor = Integer.parseInt(line.substring(4));
				
				decidePartida();
				
				return;
			}
			
			if(calculaPontos() > 21 || minhaMao.size() > 12){
				btPedir.setEnabled(false);
			}
			
			System.out.println(line);
			
			//aguente o torado
			pintor.repaint();
		}
		
		
	}

	private void inicializaSockets() throws UnknownHostException, IOException {
		socket = new Socket(Config.IP, Config.PORTA);
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
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
			try {
				render(g);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void render(Graphics g) throws IOException {

		//Imprime organizado 6 x 2
		g.drawImage(ImageIO.read(getClass().getResourceAsStream("/back.png")),
				0, 0, null);
		
		imprimirTexto("PONTOS: "+calculaPontos(), g, Color.WHITE, fonte);		

		for (int i = 0; i < mao.size(); i++) {
			if(i < 6){
				g.drawImage(
						ImageIO.read(getClass().getResourceAsStream("/"+mao.get(i)+".png")),
						i + (30*i) +  150, 100, null);
			}
			else{
				g.drawImage(
						ImageIO.read(getClass().getResourceAsStream("/"+mao.get(i)+".png")),
						(i-7) + (30*(i-7)) +  180, 180, null);
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
					Config.ALTURA - tamanho/6);
	}


	Carta gerarCarta(String carta){
		Carta c = null;
		//carta 10
		if(carta.charAt(0)=='T'){
			c = new Carta(carta, 10);
		}
		else if(carta.charAt(0)=='J'){
			c = new Carta(carta, 10);
		}
		else if(carta.charAt(0)=='Q'){
			c = new Carta(carta, 10);
		}
		else if(carta.charAt(0)=='K'){
			c = new Carta(carta, 10);
		}
		else if(carta.charAt(0)=='A'){
			c = new Carta(carta, 11);
		}
		else{
			c = new Carta(carta, Integer.parseInt(carta.charAt(0)+""));
		}
		
		return c;
	}
	
	public int calculaPontos(){
		
		int soma = 0;
		
		for (Carta carta : minhaMao) {
			if(minhaMao.size() > 1 && quantidadeAses > 0 
			   && carta.getNome().contains("A")){
				carta.setValor(1);
			}
			soma+=carta.getValor();
		}
		return soma;
	}
	
	public void decidePartida(){
		
		String resultado = null;
		
		if(calculaPontos() == 21){
			if(pontosServidor == 21){
				resultado = Config.EMP;
			}
			else if(pontosServidor == calculaPontos()){
				resultado = Config.EMP;
			}
			else if(pontosServidor > 21){
				resultado = Config.GAN;
			}
			else{
				resultado = Config.PER;
			}
		}
		
		else if(calculaPontos() > 21){
			if(pontosServidor == 21){
				resultado = Config.PER;
			}
			else if(pontosServidor == calculaPontos()){
				resultado = Config.EMP;
			}
			else if(pontosServidor > 21){
				resultado = Config.EMP;
			}
			else{
				resultado = Config.PER;
			}
		}
		
		//calcula pontos < 21
		else{
			if(pontosServidor == 21){
				resultado = Config.PER;
			}
			else if(pontosServidor == calculaPontos()){
				resultado = Config.EMP;
			}
			else if(pontosServidor > 21){
				resultado = Config.GAN;
			}
			else{
				//ganha o mais proximo de 21
				if(calculaPontos() > pontosServidor){
					resultado = Config.GAN;
				}else{
					resultado = Config.PER;
				}
			}
		}
		
		JOptionPane.showMessageDialog(null, resultado +
				"\nCLI: "+calculaPontos()+" SRV: "+pontosServidor);
	}

	public static void main(String[] args) throws Exception {
		BlackJCliente client = new BlackJCliente();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
	
}