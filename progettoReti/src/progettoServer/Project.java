package progettoServer;

import java.util.ArrayList;
import java.util.List;

public class Project {
	
	private String name;
	private String creator;
	private List<String> members;
	private List<Card> cards;
	private List<String> cardsList;
	private String ip;
	
	//Classe relativa al progetto, contiene tutti i metodi e le informazioni necessarie
	public Project(String name, String creator) {
		this.name = name;
		this.creator = creator;
		this.members = new ArrayList<String>();
		this.members.add(creator);
		this.cards = new ArrayList<Card>();
		this.cardsList = new ArrayList<String>();
	}
	//Vari metodi set/get il cui nome e' esaustivo per la loro funzionalita'
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public void setCardsList(List<String> cardsList) {
		this.cardsList = cardsList;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public String getCreator() {
		return this.creator;
	}
	
	public List<String> getMembers() {
		return this.members;
	}
	
	public String addMember(String member) {
		if(members.isEmpty()) {
			members.add(member);
			return "success";
		}
		if(members.contains(member)) return "alreadyMember";
		
		if(!members.contains(member)) {
			members.add(member);
			return "success";
		}
		return "failure";
	}
	
	public boolean isMember(String user) {
		if(members.isEmpty() | !members.contains(user)) return false;
		return true;
	}
	
	//Devo verificare l'unicita' del nome della card
	public String addCard(Card card) {
		if(cards.isEmpty()) {
			cards.add(card);
			cardsList.add(card.getName());
			return "success";
		} else {
			boolean isPresent = false;
			String cardToAddName = card.getName();
			for(Card key : cards) {
				String cardName = key.getName();
				if(cardName.equals(cardToAddName)) {
					isPresent = true;
				}
			}
			if(isPresent) {
				return "nameAlreadyUsed";
			} else {
				cards.add(card);
				cardsList.add(card.getName());
				return "success";
			}
		}
	}
	
	public List<String> getCardsList() {
		return this.cardsList;
	}
	
	public List<Card> getCards() {
		return this.cards;
	}
	
	public Card getCardByName(String cardName) {
		if(cardsList.isEmpty()) {
			return null; //la card non esiste
		}
		if(!cardsList.contains(cardName)) {
			return null;
		}
		
		for(Card card : cards) {
			if (card.getName().equals(cardName)) return card;
		}
		
		return null;
	}
	
	public String getIp() {
		return this.ip;
	}

}
