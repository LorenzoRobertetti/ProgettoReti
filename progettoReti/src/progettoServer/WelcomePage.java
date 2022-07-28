package progettoServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
//Classe che implementa la pagina di welcome 
public class WelcomePage implements ActionListener {
	
	private String userID;
	private String password;
	private SocketChannel client;
	private UserServerInterface serverObject;
	private Object syncObject;
	private NotifyEventImpl callbackObj;
	private int port;
	private String ipStart;
	private final int BUFFER_DIMENSION;
	
	//Campi interfaccia grafica
	JFrame frame = new JFrame();
	JButton getUserListButton = new JButton("Get User List");
	JButton getUserOnlineButton = new JButton("Get user online list");
	JButton getProjectsListButton = new JButton();
	JButton createProjectButton = new JButton("Create project");
	JButton openProjectButton = new JButton("Open project");
	JButton logoutButton = new JButton("Logout");
	JButton blueBellButton = new JButton();
	JButton greenBellButton = new JButton();
	JTextField createProjectField = new JTextField();
	JLabel messageLabel = new JLabel();
	JLabel nameLabel = new JLabel();
	UpdateComboBox updateComboBox = new UpdateComboBox();
	UpdateListBox updateListBox = new UpdateListBox();
	JComboBox<String> comboBoxProjects;
	JList<String> listRegisteredUsers = new JList<String>();
	JList<String> listOnlineUsers = new JList<String>();
	List<String> newListRegisterUsers;
	List<String> newListOnlineUsers;
	JPanel registeredUsersPanel = new JPanel(new BorderLayout());
	JPanel onlineUsersPanel = new JPanel(new BorderLayout());
	

	public WelcomePage(String userID, String password, SocketChannel client, UserServerInterface serverObject, int port, int bufferSize, boolean newPage, NotifyEventImpl callbackObj, JList<String> listRegisteredUsersT, JList<String> listOnlineUsersT, String ipS) {
		
		this.userID = userID;
		this.password = password;
		this.client = client;
		this.serverObject = serverObject; //variabile per la sincronizzazione
		this.port = port;
		this.BUFFER_DIMENSION = bufferSize;
		this.ipStart = ipS;
		this.syncObject = new Object();
		if(newPage) { //Se la pagina e' nuova, richiedo le liste degli utenti e mi registro alle callback
			this.listRegisteredUsers = new JList<String>();
			this.listOnlineUsers =  new JList<String>();
			this.callbackObj = new NotifyEventImpl(blueBellButton, greenBellButton, syncObject, newListRegisterUsers, newListOnlineUsers);
		} else { //altrimenti riprendo tutti i riferimenti
			this.callbackObj = callbackObj;
			this.listRegisteredUsers = listRegisteredUsersT;
			this.listOnlineUsers = listOnlineUsersT;
		}
		
		//Setting interfaccia grafica
		getUserListButton.setBounds(60,60,160,25);
		getUserListButton.setFocusable(false);
		getUserListButton.addActionListener(this);
		
		getUserOnlineButton.setBounds(285,60,160,25);
		getUserOnlineButton.setFocusable(false);
		getUserOnlineButton.addActionListener(this);
		
		ImageIcon refreshIcon = new ImageIcon("./src/progettoServer/Imm/refresh.png");
		Image imgRefresh = refreshIcon.getImage() ;  
		Image newRefresh = imgRefresh.getScaledInstance( 15, 15,  java.awt.Image.SCALE_SMOOTH );  
		refreshIcon = new ImageIcon(newRefresh);
		
		getProjectsListButton.setBounds(465,579,25,25);
		getProjectsListButton.setFocusable(false);
		getProjectsListButton.addActionListener(this);
		getProjectsListButton.setIcon(refreshIcon);
		
		createProjectButton.setBounds(60,492,205,25);
		createProjectButton.setFocusable(false);
		createProjectButton.addActionListener(this);
		
		openProjectButton.setBounds(60,579,205,25);
		openProjectButton.setFocusable(false);
		openProjectButton.addActionListener(this);
		
		logoutButton.setBounds(225,666,100,25);
		logoutButton.setFocusable(false);
		logoutButton.addActionListener(this);
		
		ImageIcon blueBellIcon = new ImageIcon("./src/progettoServer/Imm/blueBell.png");
		Image blueBell = blueBellIcon.getImage() ;  
		Image newBlueBell = blueBell.getScaledInstance( 15, 15,  java.awt.Image.SCALE_SMOOTH );  
		blueBellIcon = new ImageIcon(newBlueBell);
		
		blueBellButton.setBounds(240,60,25,25);
		blueBellButton.setFocusable(false);
		blueBellButton.addActionListener(this);
		blueBellButton.setIcon(blueBellIcon);
		blueBellButton.setEnabled(false);
		
		ImageIcon greenBellIcon = new ImageIcon("./src/progettoServer/Imm/greenBell.png");
		Image greenBell = greenBellIcon.getImage() ;  
		Image newGreenBell = greenBell.getScaledInstance( 15, 15,  java.awt.Image.SCALE_SMOOTH );  
		greenBellIcon = new ImageIcon(newGreenBell);
		
		greenBellButton.setBounds(465,60,25,25);
		greenBellButton.setFocusable(false);
		greenBellButton.addActionListener(this);
		greenBellButton.setIcon(greenBellIcon);
		greenBellButton.setEnabled(false);
		
		createProjectField.setBounds(285,492,205,25);
		
		comboBoxProjects = new JComboBox<String>();
		comboBoxProjects.addActionListener(this);
		comboBoxProjects.setBounds(285, 579, 160, 25);
		
		newListRegisterUsers = new ArrayList<String>();
		newListOnlineUsers = new ArrayList<String>();
		
		registeredUsersPanel.setBounds(60, 100, 205, 330);
		registeredUsersPanel.add(new JScrollPane(listRegisteredUsers));
		
		
		onlineUsersPanel.setBounds(285, 100, 205, 330);
		onlineUsersPanel.add(new JScrollPane(listOnlineUsers));
		
		messageLabel.setBounds(285,477,205,20);
		messageLabel.setFont(new Font(null,Font.ITALIC,10));
		
		nameLabel.setBounds(60, 15, 775, 20);
		nameLabel.setFont(new Font(null,Font.ITALIC,20));
		nameLabel.setText("Welcome: " + userID);
		
		frame.add(comboBoxProjects);
		frame.add(getUserListButton);
		frame.add(getUserOnlineButton);
		frame.add(getProjectsListButton);
		frame.add(createProjectButton);
		frame.add(openProjectButton);
		frame.add(logoutButton);
		frame.add(blueBellButton);
		frame.add(greenBellButton);
		frame.add(createProjectField);
		frame.add(messageLabel);
		frame.add(nameLabel);
		frame.add(registeredUsersPanel);
		frame.add(onlineUsersPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(550,800);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setResizable(false);
		
		if(newPage) {
			this.startRoutine();
		} else {
			if(callbackObj.getBlueChanged()) {
				blueBellButton.setEnabled(true);
			}
			if(callbackObj.getGreenChanged()) {
				greenBellButton.setEnabled(true);
			}
		}
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		if(e.getSource()==getUserListButton) {
			try {
				String msgToServer = "getUserList";
	            // la prima parte del messaggio contiene la lunghezza del messaggio
	            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
	            length.putInt(msgToServer.length());
	            length.flip();
	            client.write(length);
	            length.clear();
	
	            // la seconda parte del messaggio contiene il messaggio da inviare
	            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());
	
	            client.write(readBuffer);
	            readBuffer.clear();
	
	            ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
	            client.read(reply);
	            reply.flip();
	            String resultLog = new String(reply.array()).trim();
	            System.out.printf("Client: il server ha inviato %s\n", resultLog);
	            reply.clear();
	            synchronized(syncObject) { //aggiorno la lista
	            	updateListBox.updateListBox(listRegisteredUsers, resultLog);
	            }

			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante il get user list");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==getUserOnlineButton) {
			try {
				String msgToServer = "getUserOnline";
	            // la prima parte del messaggio contiene la lunghezza del messaggio
	            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
	            length.putInt(msgToServer.length());
	            length.flip();
	            client.write(length);
	            length.clear();
	
	            // la seconda parte del messaggio contiene il messaggio da inviare
	            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());
	
	            client.write(readBuffer);
	            readBuffer.clear();
	
	            ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
	            client.read(reply);
	            reply.flip();
	            String resultLog = new String(reply.array()).trim();
	            System.out.printf("Client: il server ha inviato %s\n", resultLog);
	            reply.clear();
	            synchronized(syncObject) { //aggiorno la lista
	            	updateListBox.updateListBox(listOnlineUsers, resultLog);
	            }
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la get user online");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==getProjectsListButton) { //Rappresentato graficamente dal bottone refresh
			String msgToServer = "getProjectsList" + "*" + userID;
			try {
	            // la prima parte del messaggio contiene la lunghezza del messaggio
	            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
	            length.putInt(msgToServer.length());
	            length.flip();
	            client.write(length);
	            length.clear();
	
	            // la seconda parte del messaggio contiene il messaggio da inviare
	            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());
	
	            client.write(readBuffer);
	            readBuffer.clear();
	
	            ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
	            client.read(reply);
	            reply.flip();
	            String resultLog = new String(reply.array()).trim();
	            System.out.printf("Client: il server ha inviato %s\n", resultLog);
	            reply.clear();
	            updateComboBox.updateComboBox(comboBoxProjects, resultLog);
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la get project");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==createProjectButton) {
			String userID = this.userID;
			String projectName = createProjectField.getText();
			if(projectName.equals("")) {
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Text field is empty");
			} else {
				if(projectName.contains("*")) {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Can't use '*'");
				} else {
					String msgToServer = "createProject" + "*" + userID + "*" + projectName;
					
					try {
			            // la prima parte del messaggio contiene la lunghezza del messaggio
			            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
			            length.putInt(msgToServer.length());
			            length.flip();
			            client.write(length);
			            length.clear();
			
			            // la seconda parte del messaggio contiene il messaggio da inviare
			            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());
			
			            client.write(readBuffer);
			            readBuffer.clear();
			
			            ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
			            client.read(reply);
			            reply.flip();
			            String resultLog = new String(reply.array()).trim();
			            System.out.printf("Client: il server ha inviato %s\n", resultLog);
			            reply.clear();
					} catch (Exception exSocketLogin) {
						System.out.println("Errore durante la create project");
						exSocketLogin.printStackTrace();
					}
				}
			}
			
		}
		
		if(e.getSource()==openProjectButton) {
			String userID = this.userID;
			String projectName = (String) comboBoxProjects.getSelectedItem(); //prendo la stringa selezionata nel combobox
			String msgToServer = "openProject" + "*" + userID + "*" + projectName;
			try {
	            // la prima parte del messaggio contiene la lunghezza del messaggio
	            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
	            length.putInt(msgToServer.length());
	            length.flip();
	            client.write(length);
	            length.clear();
	
	            // la seconda parte del messaggio contiene il messaggio da inviare
	            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());
	
	            client.write(readBuffer);
	            readBuffer.clear();
	
	            ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
	            client.read(reply);
	            reply.flip();
	            String resultLog = new String(reply.array()).trim();
	            System.out.printf("Client: il server ha inviato %s\n", resultLog);
	            reply.clear();
	            if(resultLog.equals("success")) {
	            	frame.dispose();
	    			@SuppressWarnings("unused")
	    			ProjectPage projectPage = new ProjectPage(userID, password, projectName, client, serverObject, port, BUFFER_DIMENSION, callbackObj, listRegisteredUsers, listOnlineUsers, ipStart);
	            }
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la open ");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==blueBellButton) { //campanella blu
			synchronized(syncObject) {
				newListRegisterUsers = callbackObj.getNewListRegisterUsers();
				updateListBox.updateDeepListBox(listRegisteredUsers, null, newListRegisterUsers, "blue");
				callbackObj.setBlueChanged(false);
				blueBellButton.setEnabled(false);
			}
		}
		
		if(e.getSource()==greenBellButton) { //campanella verde
			synchronized(syncObject) { //devo prendere la lock
				newListOnlineUsers = callbackObj.getNewListOnlineUsers();
				List<String> middleList = newListOnlineUsers;
				
				ListModel<String> oldListModel = listOnlineUsers.getModel();
				List<String> oldList = new ArrayList<String>();
				
				
				for(int i = 0; i < oldListModel.getSize(); i++){
					oldList.add(oldListModel.getElementAt(i));
			    }
				Set<String> stringSet = new LinkedHashSet<String>(oldList);
				stringSet.addAll(newListOnlineUsers);
				newListOnlineUsers = new ArrayList<String>(stringSet);
				
				updateListBox.updateDeepListBox(listOnlineUsers, middleList, newListOnlineUsers, "green");
				callbackObj.setGreenChanged(false);
				greenBellButton.setEnabled(false);
			}
		}
		
		if(e.getSource()==logoutButton) {
			String msgToServer = "logout" + "*" + this.userID;
			try {
	            // la prima parte del messaggio contiene la lunghezza del messaggio
	            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
	            length.putInt(msgToServer.length());
	            length.flip();
	            client.write(length);
	            length.clear();
	            
	            // la seconda parte del messaggio contiene il messaggio da inviare
	            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());
	            
	            client.write(readBuffer);
	            readBuffer.clear();
	            serverObject.unregisterForCallback(userID); //Mi levo dalle callback
	            frame.dispose();
				@SuppressWarnings("unused")
				LoginPage loginPage = new LoginPage(ipStart, port);
	            
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante logout");
				exSocketLogin.printStackTrace();
			}
		}
	}
	
	public void startRoutine() { //Routine di avvio se la pagina e' nuova, richiedo le liste di getOnline e get Registered
		try {
			String msgToServer = "getUserList";
            // la prima parte del messaggio contiene la lunghezza del messaggio
            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
            length.putInt(msgToServer.length());
            length.flip();
            client.write(length);
            length.clear();

            // la seconda parte del messaggio contiene il messaggio da inviare
            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());

            client.write(readBuffer);
            readBuffer.clear();

            ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
            client.read(reply);
            reply.flip();
            String resultLog = new String(reply.array()).trim();
            System.out.printf("Client: il server ha inviato %s\n", resultLog);
            reply.clear();
            synchronized(syncObject) {
            	updateListBox.updateListBox(listRegisteredUsers, resultLog);
            }
		} catch (Exception exSocketLogin) {
			System.out.println("Errore durante get user");
			exSocketLogin.printStackTrace();
		}
		
		try {
			String msgToServer = "getUserOnline";
            // la prima parte del messaggio contiene la lunghezza del messaggio
            ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
            length.putInt(msgToServer.length());
            length.flip();
            client.write(length);
            length.clear();

            // la seconda parte del messaggio contiene il messaggio da inviare
            ByteBuffer readBuffer = ByteBuffer.wrap(msgToServer.getBytes());

            client.write(readBuffer);
            readBuffer.clear();

            ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
            client.read(reply);
            reply.flip();
            String resultLog = new String(reply.array()).trim();
            System.out.printf("Client: il server ha inviato %s\n", resultLog);
            reply.clear();
            synchronized(syncObject) {
            	updateListBox.updateListBox(listOnlineUsers, resultLog);
            }
		} catch (Exception exSocketLogin) {
			System.out.println("Errore durante get user online");
			exSocketLogin.printStackTrace();
		}
		
		//Mi registro per le callback
		try {
			NotifyEventInterface stub = (NotifyEventInterface) UnicastRemoteObject.exportObject(callbackObj,0);
			serverObject.registerForCallback(stub, userID);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
	}

}
