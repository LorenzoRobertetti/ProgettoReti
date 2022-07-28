package progettoServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//Classe che contiene la maggior parte delle strutture dati del programma
public class Data {
	
	private String path = "./src/progettoServer/Store/";
	
	private List<Project> projects;
	private List<String> projectsList;
	private List<String> onlineUsers;
	private Set<String> ipSet;
	

	public Data() { 
		this.projects = new ArrayList<Project>();
		this.projectsList = new ArrayList<String>();
		this.onlineUsers = new ArrayList<String>();
		this.ipSet = new HashSet<String>();
	}
	
	public List<Project> getProjects() {
		return this.projects;
	}
	//Metodo per creare un nuovo progetto 
	public String createProject(String projectName, String creator) {
		try {
			if(projectsList.isEmpty()) { 
				MulticastAddress ip = new MulticastAddress();
				String newIp = ip.getInetAddress();
				ipSet.add(newIp);
				Project newProject = new Project(projectName, creator);
				newProject.setIP(newIp.toString());
				projects.add(newProject);
				projectsList.add(projectName);
				return "success";
			} else {
				if(projectsList.contains(projectName)) {
					return "projectAlreadyExist";
				} else {
					MulticastAddress ip;
					String newIp;
					do {
						ip = new MulticastAddress();
						newIp = ip.getInetAddress();
					} while(ipSet.add(newIp));
					
					Project newProject = new Project(projectName, creator);
					newProject.setIP(newIp.toString());
					projects.add(newProject);
					projectsList.add(projectName);
					return "success";
				}
			}
		} catch (UnknownHostException e) {
			return "failure";
		}
		
	}
	
	public List<String> getProjectsList() {
		return this.projectsList;
	}
	
	//Metodo per ottenere un progetto dal suo nome
	public Project getProjectByName(String projectName) {
		
		if(projectsList.isEmpty() | !projectsList.contains(projectName)) return null;
		
		for(Project project : projects) {
			if(project.getName().equals(projectName)) return project;
		}
		
		return null;
	}
	//Metodo per aggiungere un membro alla lista degli utenti online
	public String addOnlineUser(String user) {
		if(onlineUsers.contains(user)) {
			return "userAlreadyOnline";
		} else {
			onlineUsers.add(user);
			return "success";
		}
	}
	//Metodo per ottenere la lista degli online
	public List<String> getOnlineUsers() {
		return this.onlineUsers;
	}
	//Metodo per pulire la directory, serve per salvare tutti i dati prima della chiusura
	public void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	//metodo per fare la store di tutti i dati 
	public void store() {
		
		File deleteDir = new File(path);
		
		File[] files = deleteDir.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
		
		if(projects.isEmpty()) {
			File file = new File(path);
			file.mkdir();
			return;
		}
		
		for(Project project : projects) {
			String projectName = project.getName();
			File file = new File(path + projectName);
			if (file.mkdirs()) {
	            this.storeProject(path + projectName, project);
	        } else {
	            System.out.println("Failed to create directory!");
	        }
		}

	}
	//Metodo per fare la store di un singolo progetto, chiamata dalla store precedente
	public void storeProject(String path, Project project) {
		
		try {
		
		FileOutputStream f = new FileOutputStream(new File(path + "/" + project.getName() + ".txt"));
        ObjectOutputStream o = new ObjectOutputStream(f);
        
        
        ProjectData projectData = new ProjectData();
        projectData.setName(project.getName());
        projectData.setCreator(project.getCreator());
        projectData.setCardsList(project.getCardsList());
        projectData.setIp(project.getIp());
        projectData.setMembers(project.getMembers());

        // Write project data in a file
        o.writeObject(projectData);
        
        //Write cards 
        if(!project.getCards().isEmpty()) {
        	for (Card card : project.getCards()) {
        		String name = card.getName();
        		FileOutputStream c = new FileOutputStream(new File(path + "/" + name + ".txt"));
                ObjectOutputStream o1 = new ObjectOutputStream(c);
                o1.writeObject(card);
                //vedi se bisogna cuiderli subito, probabil,ente no
                c.close();
                o1.close();
        	}
        }

        o.close();
        f.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	//Metodo per fare la load
	public void load() {
		File rootDir = new File(path);
        File[] rootFiles = rootDir.listFiles();
      
        if(rootFiles != null) { //Esiste almeno un progetto
        	for(File file : rootFiles) {
        		if(file.isDirectory()) {
        			this.loadProject(file);
        		}
        	}
        }
        
	}
	//Metodo per caricare un singolo progetto 
	public void loadProject(File directoryProject) {
		
		try {
			String projectName = directoryProject.getName();
			
			FileInputStream fi = new FileInputStream(new File(path + "/" + projectName + "/" + projectName + ".txt"));
	        ObjectInputStream oi = new ObjectInputStream(fi);

	        // Read object
	        ProjectData projectData = (ProjectData) oi.readObject();
	        //costruisco la lista delle card
	        List<String> cardsList = projectData.getCardsList();
	        List<Card> cards = new ArrayList<Card>(); //intanto la faccio vuota
	        
	        if(!cardsList.isEmpty()) {
	        	for(String cardName : cardsList) {
	        		FileInputStream fi2 = new FileInputStream(new File(path + "/" + projectName + "/" + cardName + ".txt"));
	        		System.out.println(path + "/" + projectName + "/" + cardName + ".txt");
	    	        ObjectInputStream oi2 = new ObjectInputStream(fi2);
	    	        
	    	        Card card = (Card) oi2.readObject();
	    	        cards.add(card);
	    	        
	    	        fi2.close();
	    	        oi2.close();
	        	}
	        }
	        
	        oi.close();
	        fi.close();
	        
	        //Creo il progetto completo con tutti i dati
	        String name = projectData.getName();
	        String creator = projectData.getCreator();
	        String ip = projectData.getIp();
	        List<String> members = projectData.getMembers();
	        
	        Project newProject = new Project(name, creator);
	        
	        newProject.setCards(cards);
	        newProject.setCardsList(cardsList);
	        newProject.setMembers(members);
	        newProject.setIP(ip);
	        
	        this.projects.add(newProject);
	        this.projectsList.add(name);
	        this.ipSet.add(ip);
	        
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
