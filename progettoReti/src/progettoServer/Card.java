package progettoServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Classe che implementa concretamente la struttura astratta della card
//Gli oggetti sono le card di un progetto
//Tutte le variabili di istanza contengono le informazioni da associare ad una card

public class Card implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name; //nome della card
	private String state;//stato della card
	private String description; //descrizione della card
	private List<String> history; //storia degli spostamenti tra i vari stati della card
	

	public Card(String name, String description) {
		this.name = name;
		this.description = description;
		this.state = "To Do";
		this.history = new ArrayList<String> ();
	}
	//Serie di metodi per settare/ottenere informazioni dalla card, i nomi sono esaustivi per descrivere la funzionalita'
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void addStateToHistory(String state) {
		this.history.add(state);
	}
	
	public List<String> getHistory() {
		return this.history;
	}

}
