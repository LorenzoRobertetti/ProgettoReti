package progettoServer;

import java.nio.ByteBuffer;

//Classe degli oggetti usati come attachment per gestire le connessioni tramite NIO
//Contiene tutte le informazioni necessarie per svolgere le funzionalita' richieste

public class AttachmentInfo {
	
	private ByteBuffer[] bfs; //bfs usato per leggere e scrivere
	private String answer;	//la risposta che e' stata elaborata dal gestore delle richieste
	private String user;	//l'username associato
	
	//Setta il buffer
	public void setBfs(ByteBuffer[] bfs) {
		this.bfs = bfs;
	}
	//Setta la risposta
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	//Setta l'username
	public void setUser(String user) {
		this.user = user;
	}
	// Rispettivi metodi get
	public ByteBuffer[] getBfs() {
		return this.bfs;
	}
	
	public String getUser() {
		return this.user;
	}
	
	public String getAnswer() {
		return this.answer;
	}

}
