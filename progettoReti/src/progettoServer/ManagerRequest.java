package progettoServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

//Classe che elabora tutte le richieste da parte del client 
public class ManagerRequest {
	
	private int portMulticast = 30000;
	//Strutture utilizzate per mantenere le informazioni del sistema
	private ConcurrentHashMap<String, String> userRegisterMap; 
	private Data data; //riferimento al database del server 
	private UserServerImpl userServerImpl;
	
	public ManagerRequest(ConcurrentHashMap<String, String> userRegisterMap, Data data,  UserServerImpl userServerImpl) {
		this.userRegisterMap = userRegisterMap;
		this.data = data;
		this.userServerImpl = userServerImpl;
	}
	//Metodo che elabora la richiesta e fornisce la risposta da inviare
	public String ElaborateRequest(String request, AttachmentInfo attachmentInfo) {
		
		StringTokenizer st = new StringTokenizer(request,"*");
		String method = "";
		
		if(st.hasMoreTokens()) {
			method = st.nextToken();
		} else {
			return "failure";
		}
		
		if(method.equals("login")) {
			String user = st.nextToken();
			String passw = st.nextToken();
			
			if(!userRegisterMap.containsKey(user)) { //Se l'userId non esiste
				return "idDoesntExist";
			}
			String realPassw = userRegisterMap.get(user);
			if(realPassw.equals(passw)) {
				List<String> onlineUsers = data.getOnlineUsers();
				if(!onlineUsers.isEmpty() && onlineUsers.contains(user)) { //gia online
					return "alreadyOnline";
				}
				onlineUsers.add(user);
				try {
					userServerImpl.doCallbacks(1, onlineUsers);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				attachmentInfo.setUser(user);
				return "success";
			} else {
				return "incorrectpassword"; //password errata
			}
		}
		
		if(method.equals("getUserList")) { //richiesta per ottenere la lista degli utenti
			String hashToString = "";
			
			if(userRegisterMap.isEmpty()) {
				hashToString = "vuota";
			} else {
				Set<String> hashSet = userRegisterMap.keySet();
				for(String name : hashSet) {
					hashToString = hashToString + name + "*";
				}
				hashToString = hashToString.substring(0, hashToString.length() - 1);
			}
			return hashToString;
		}
		
		if(method.equals("getUserOnline")) { //metodo per ottenere la lista degli utenti online
			String listToString = "";
			List<String> list = data.getOnlineUsers();
			
			if(list.isEmpty()) {
				listToString = "vuota";
			} else {
				for(String name : list) {
					listToString = listToString + name + "*";
				}
				listToString = listToString.substring(0, listToString.length() - 1);
			}
			return listToString;
		}
		
		if(method.equals("getProjectsList")) { //metodo per ottenere la lista dei progetti 
			String user = st.nextToken();
			String listToString = "";
			List<Project> list = data.getProjects();
			
			if(list.isEmpty()) {
				return "vuota";
			} else {
				for(Project project : list) {
					if(project.isMember(user)) {
						listToString = listToString + project.getName() + "*";
					}
				}
				if(listToString.equals("vuota") || listToString.equals("")) {
					return "vuota";
				}
			}
			return listToString.substring(0, listToString.length() - 1);
		}
		
		if(method.equals("createProject")) { //richiesta di creazione del proggetto 
			String user = st.nextToken();
			String projectName = st.nextToken();
			
			return data.createProject(projectName, user);
		}
		
		if(method.equals("openProject")) {
			String user = st.nextToken();
			String projectName = st.nextToken();
			
			Project projectSelected = data.getProjectByName(projectName);
			
			if(projectSelected == null) return "notExist";
			
			if(projectSelected.isMember(user)) {
				return "success";
			} else {
				return "notMember";
			}
			
		}
		
		//DENTRO IL PROGETTO (richieste che vengono fatte da dentro il progetto)
		
		if(method.equals("showMember")) {//Devo stare attento al fatto che il progetto puo' essere stato eliminato
			
			String projectName = st.nextToken();
			String listToString = "";
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "Deleted"; //Progetto eliminato
			
			List<String> list = project.getMembers();
			
			if(list.isEmpty()) {
				listToString = "vuota";
			} else {
				for(String name : list) {
					listToString = listToString + name + "*";
				}
				listToString = listToString.substring(0, listToString.length() - 1);
			}
			
			return listToString;
			
		}
		
		if(method.equals("addMember")) {
			String projectName = st.nextToken();
			String memberToAdd = st.nextToken();
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "Deleted"; //Progetto eliminato
			
			return project.addMember(memberToAdd);
		}
		
		if(method.equals("showCards")) {//Devo stare attento al fatto che il progetto puo' essere stato eliminato
			
			String projectName = st.nextToken();
			String listToString = "";
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "Deleted"; //Progetto eliminato
			
			List<String> list = project.getCardsList();
			
			if(list.isEmpty()) {
				listToString = "vuota";
			} else {
				for(String name : list) {
					listToString = listToString + name + "*";
				}
				listToString = listToString.substring(0, listToString.length() - 1);
			}
			
			return listToString;
			
		}
		
		if(method.equals("deleteProject")) {//Devo stare attento al fatto che il progetto puo' essere stato eliminato
			
			String projectName = st.nextToken();
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "failure"; //Progetto eliminato
			
			List<Card> cards = project.getCards();
			
			if(cards.isEmpty()) {
				data.getProjects().remove(project);
				data.getProjectsList().remove(projectName);
				return "success";
			} else {
				for(Card card : cards) {
					if(!card.getState().equals("Done")){
						return "failure";
					}
				}
			}
			
			data.getProjects().remove(project);
			data.getProjectsList().remove(projectName);
			return "success";
			
		}
		
		if(method.equals("addCard")) {//Devo stare attento al fatto che il progetto puo' essere stato eliminato
			
			String projectName = st.nextToken();
			String cardName = st.nextToken();
			String cardDescription = st.nextToken();
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "Deleted"; //Progetto eliminato
			
			Card newCard = new Card(cardName, cardDescription);
			
			return project.addCard(newCard);
			
		}
		
		if(method.equals("openCard")) {//Devo stare attento al fatto che il progetto puo' essere stato eliminato
			
			String projectName = st.nextToken();
			String cardName = st.nextToken();
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "Deleted"; //Progetto eliminato
			
			Card card = project.getCardByName(cardName);
			
			if(card == null) return "cardDoesntExist";
			
			return card.getState() + "*" + card.getDescription();
			
		}
		
		if(method.equals("moveCard")) {//Devo stare attento al fatto che il progetto puo' essere stato eliminato
			
			String projectName = st.nextToken();
			String cardName = st.nextToken();
			String to = st.nextToken();
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "Deleted"; //Progetto eliminato
			
			Card card = project.getCardByName(cardName);
			
			if(card == null) return "cardDoesntExist";
			
			String from = card.getState();
			card.addStateToHistory(from + "->" + to);
			card.setState(to);
			//Mando il messaggio di notifica
			DatagramSocket ms = null;
			try {
				ms = new DatagramSocket();
				byte[] data;
				String s = "Server:" + " " + cardName + "moved from " + from + " to " + to;
				data = s.getBytes();
				DatagramPacket dp = new DatagramPacket(data, data.length, InetAddress.getByName(project.getIp()), portMulticast);
				ms.send(dp);
				ms.close();
			} catch(IOException ex) {
				System.out.println(ex);
			}
			
			return "success";
			
		}
		
		
		if(method.equals("getCardHistory")) {//Devo stare attento al fatto che il progetto puo' essere stato eliminato
			
			String projectName = st.nextToken();
			String cardName = st.nextToken();
			String listToString = "";
			
			Project project = data.getProjectByName(projectName);
			
			if(project == null) return "Deleted"; //Progetto eliminato
			
			Card card = project.getCardByName(cardName);
			
			if(card == null) return "cardDoesntExist";
			
			List<String> list = card.getHistory();
			
			if(list.isEmpty()) {
				listToString = "vuota";
			} else {
				for(String name : list) {
					listToString = listToString + name + "*";
				}
				listToString = listToString.substring(0, listToString.length() - 1);
			}
			
			return listToString;
			
		}
		
		
		//Metodo per aprire la chat
		
		if(method.equals("openChat")) {
			String user = st.nextToken();
			String projectName = st.nextToken();
			
			Project projectSelected = data.getProjectByName(projectName);
			
			if(projectSelected == null) return "Deleted";
			
			if(!projectSelected.isMember(user)) {
				return "notMember";
			} else {
				return projectSelected.getIp();
			}
			
		}
		
		//Metodo per il logout
		if(method.equals("logout")) {
			String user = st.nextToken();
			List<String> onlineUsers = data.getOnlineUsers();
			if(onlineUsers != null && onlineUsers.contains(user)) {
				onlineUsers.remove(user);
				try {
					userServerImpl.doCallbacks(1, onlineUsers);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			return "exit";
		}
			return "failure";
		}

}
