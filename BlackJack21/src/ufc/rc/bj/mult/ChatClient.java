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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Painter;

import ufc.rc.bj.conf.Config;
import ufc.rc.bj.obj.Carta;

/**
 * A simple Swing-based client for the chat server.  Graphically
 * it is a frame with a text field for entering messages and a
 * textarea to see the whole dialog.
 *
 * The client follows the Chat Protocol which is as follows.
 * When the server sends "SUBMITNAME" the client replies with the
 * desired screen name.  The server will keep sending "SUBMITNAME"
 * requests as long as the client submits screen names that are
 * already in use.  When the server sends a line beginning
 * with "NAMEACCEPTED" the client is now allowed to start
 * sending the server arbitrary strings to be broadcast to all
 * chatters connected to the server.  When the server sends a
 * line beginning with "MESSAGE " then all characters following
 * this string should be displayed in its message area.
 */
public class ChatClient extends Thread {

	static List<String> mao;
	
	List<Carta> minhaMao;

	BufferedReader in;
	PrintWriter out;
	String nomeUser = getNome();
	JFrame frame = new JFrame(nomeUser);
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);

	private Font fonte;

	JButton btPedir = new JButton("Pedir");
	JButton btParar = new JButton("Parar");

	Pintor pintor;

	/**
	 * Constructs the client by laying out the GUI and registering a
	 * listener with the textfield so that pressing Return in the
	 * listener sends the textfield contents to the server.  Note
	 * however that the textfield is initially NOT editable, and
	 * only becomes editable AFTER the client receives the NAMEACCEPTED
	 * message from the server.
	 */
	public ChatClient() {
		
		minhaMao = new ArrayList<Carta>();
		mao = new ArrayList<String>();

		fonte = new Font("Verdana", Font.BOLD, 20);

		// Layout GUI
		pintor = new Pintor();
		pintor.setPreferredSize(
				new Dimension(Config.LARGURA, Config.ALTURA));

		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.setContentPane(pintor);
		frame.setSize(Config.LARGURA, Config.ALTURA);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame.getContentPane().add(btPedir);
		frame.getContentPane().add(btParar);
		
		frame.getContentPane().add(new JScrollPane(messageArea));

		frame.pack();

		btPedir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				out.println(Config.PEDIR);
				pintor.repaint();
			}
		});

		btParar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				out.println(Config.PARAR+"_somaMao_"+nomeUser);
//				JOptionPane.showMessageDialog(null, "Parei");
				btPedir.setEnabled(false);
			}
		});
		
		pintor.repaint();
	}

	/**
	 * Prompt for and return the address of the server.
	 */
	private String getServerAddress() {
		return JOptionPane.showInputDialog(
				frame,
				"Enter IP Address of the Server:",
				"Welcome to the Chatter",
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Prompt for and return the desired screen name.
	 */

	public String getNome() {
		return JOptionPane.showInputDialog(
				frame,
				"Nome:",
				"Qual o seu nome?",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Connects to the server then enters the processing loop.
	 */
	@Override
	public void run() {
		
		pintor.repaint();
		
		try {
			inicializaSockets();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Process all messages from server, according to the protocol.
		while (true) {

			String line = null;
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (line.startsWith("SUBMITNAME")) {
				out.println(nomeUser);
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			} else if (line.startsWith("MESSAGE")) {
				mao.add(line.substring(8));
				messageArea.append(line.substring(8) + "\n");
				System.out.println(line.substring(8));
			}
			
			System.out.println(line);
		}
		
		
	}

	private void inicializaSockets() throws UnknownHostException, IOException {
		// Make connection and initialize streams
		//String serverAddress = getServerAddress();
		String serverAddress = Config.IP;
		Socket socket = new Socket(serverAddress, Config.PORTA);
		in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
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

		void imprimirTexto(String mensagem, Graphics g, Color color, Font font){
			g.setColor(color);
			g.setFont(font);
			Graphics2D g2 = (Graphics2D) g;
			int tamanho = g2.getFontMetrics().stringWidth(mensagem);
			g.drawString(mensagem, 
					Config.LARGURA/2 - tamanho/2,
					Config.ALTURA - tamanho / 2);
		}
	}

	private void render(Graphics g) throws IOException {

		//Imprime organizado 6 x 2 (Muito feio)
		System.out.println("Render");
		//Imprime organizado 6 x 2 (Muito feio)
		
		imprimirTexto("PONTOS: ", g, Color.BLACK, fonte);		

		for (int i = 0; i < mao.size(); i++) {
			if(i < 6){
				g.drawImage(
						ImageIO.read(getClass().getResourceAsStream("/"+mao.get(i)+".png")),
						i + (30*i) +  150, 200, null);
			}
			else{
				g.drawImage(
						ImageIO.read(getClass().getResourceAsStream("/"+mao.get(i)+".png")),
						(i-7) + (30*(i-7)) +  180, 280, null);
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


	/**
	 * Runs the client as an application with a closeable frame.
	 */
	public static void main(String[] args) throws Exception {
		ChatClient client = new ChatClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
}