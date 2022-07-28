package progettoServer;

import java.io.Serializable;
import java.util.List;

public class ProjectData implements Serializable{

	//Informazioni del progetto per effettuare lo store 
	private static final long serialVersionUID = 1L;
	private String name;
	private String creator;
	private List<String> members;
	private List<String> cardsList;
	private String ip;

	public ProjectData() {
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	public void setCardsList(List<String> cardsList) {
		this.cardsList = cardsList;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getCreator() {
		return this.creator;
	}
	
	public List<String> getMembers() {
		return this.members;
	}
	
	public List<String> getCardsList() {
		return this.cardsList;
	}
	
	public String getIp() {
		return this.ip;
	}

}
