package ufc.rc.bj.obj;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Carta implements Serializable {

	private static final long serialVersionUID = 2272647637273616127L;
	
	private String nome;
	private BufferedImage imagem;
	private int valor;
	
	public Carta(String nome, int valor) {
		this.nome = nome;
		this.valor = valor;
	}
	
	public Carta(String nome, int valor, BufferedImage imagem) {
		this.nome = nome;
		this.valor = valor;
		this.imagem = imagem;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public BufferedImage getImagem() {
		return imagem;
	}

	public void setImagem(BufferedImage imagem) {
		this.imagem = imagem;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}
	
}


