package progettoServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
//Classe che implementa la schermata di un progetto
	public class ProjectPage implements ActionListener{
	
		private String userID;
		private String password;
		private String projectName;
		private SocketChannel client;
		private String ipStart;
		private UserServerInterface serverObject;
		private final int port;
		private final int BUFFER_DIMENSION;
		private NotifyEventImpl callbackObj;
		
		//Campi interfaccia grafica
		JFrame frame = new JFrame();
		JButton addMemberButton = new JButton("Add member");
		JButton showMemberButton = new JButton("Show members");
		JButton showCardsButton = new JButton("Show cards");
		JButton addCardButton = new JButton("Add card");
		JButton openCardButton = new JButton("Open card");
		JButton refreshUsersButton = new JButton();
		JButton refreshCardsButton = new JButton();
		JButton undoButton = new JButton();
		JButton chatButton = new JButton();
		JButton logoutButton = new JButton("Logout");
		JButton deleteButton = new JButton("Delete");
		JTextField addCardField = new JTextField();
		JTextField addCardDescriptionField = new JTextField();
		JLabel messageNameLabel = new JLabel();
		JLabel messageDescrLabel = new JLabel();
		JLabel nameLabel = new JLabel();
		UpdateComboBox updateComboBox = new UpdateComboBox();
		UpdateListBox updateListBox = new UpdateListBox();
		JComboBox<String> comboBoxAddMember;
		JComboBox<String> comboBoxOpenCard;
		JList<String> listOnlineUsers;
		JList<String> listRegisteredUsers;
		JList<String> showMemberListUsers;
		JList<String> showCardsListUsers;
		JPanel showMemberPanel = new JPanel(new BorderLayout());
		JPanel showCardPanel = new JPanel(new BorderLayout());

	public ProjectPage(String userID, String password, String projectName, SocketChannel client, UserServerInterface serverObject, int port, int bufferSize, NotifyEventImpl callbackObj, JList<String> listRegisteredUsers, JList<String> listOnlineUsers, String ipStart) {
		
		this.userID = userID;
		this.password = password;
		this.projectName = projectName;
		this.client = client;
		this.serverObject = serverObject;
		this.port = port;
		this.ipStart = ipStart;
		this.BUFFER_DIMENSION = bufferSize;
		this.callbackObj = callbackObj;
		this.listOnlineUsers = listOnlineUsers;
		this.listRegisteredUsers = listRegisteredUsers;
		
		//Setting interfaccia grafica
		showMemberButton.setBounds(72,60,160,25);
		showMemberButton.setFocusable(false);
		showMemberButton.addActionListener(this);
		
		addMemberButton.setBounds(60,465,205,25);
		addMemberButton.setFocusable(false);
		addMemberButton.addActionListener(this);
		
		showCardsButton.setBounds(307,60,160,25);
		showCardsButton.setFocusable(false);
		showCardsButton.addActionListener(this);
		
		addCardButton.setBounds(60,525,205,25);
		addCardButton.setFocusable(false);
		addCardButton.addActionListener(this);
		
		openCardButton.setBounds(60,585,200,25);
		openCardButton.setFocusable(false);
		openCardButton.addActionListener(this);
		
		logoutButton.setBounds(225,655,100,25);
		logoutButton.setFocusable(false);
		logoutButton.addActionListener(this);
		
		deleteButton.setBounds(225,700,100,25);
		deleteButton.setFocusable(false);
		deleteButton.addActionListener(this);
		
		addCardField.setBounds(285,525,100,25);
		addCardDescriptionField.setBounds(390,525,100,25);
		
		showMemberListUsers = new JList<String>();
		showCardsListUsers = new JList<String>();
		
		showMemberPanel.setBounds(60, 100, 205, 330);
		showMemberPanel.add(new JScrollPane(showMemberListUsers));
		
		showCardPanel.setBounds(285, 100, 205, 330);
		showCardPanel.add(new JScrollPane(showCardsListUsers));
		
		comboBoxAddMember = new JComboBox<String>();
		comboBoxAddMember.addActionListener(this);
		comboBoxAddMember.setBounds(285, 465, 160, 25);
		
		comboBoxOpenCard = new JComboBox<String>();
		comboBoxOpenCard.addActionListener(this);
		comboBoxOpenCard.setBounds(285, 585, 160, 25);
		
		ImageIcon refreshIcon = new ImageIcon("./src/progettoServer/Imm/refresh.png");
		Image imgRefresh = refreshIcon.getImage() ;  
		Image newRefresh = imgRefresh.getScaledInstance( 15, 15,  java.awt.Image.SCALE_SMOOTH );  
		refreshIcon = new ImageIcon(newRefresh);
		
		refreshUsersButton.setBounds(465,465,25,25);
		refreshUsersButton.setFocusable(false);
		refreshUsersButton.addActionListener(this);
		refreshUsersButton.setIcon(refreshIcon);
		
		
		refreshCardsButton.setBounds(465,585,25,25);
		refreshCardsButton.setFocusable(false);
		refreshCardsButton.addActionListener(this);
		refreshCardsButton.setIcon(refreshIcon);
		
		ImageIcon undoIcon = new ImageIcon("./src/progettoServer/Imm/undo.png");
		Image imgUndo = undoIcon.getImage() ;  
		Image newimgUndo = imgUndo.getScaledInstance( 35, 35,  java.awt.Image.SCALE_SMOOTH );  
		undoIcon = new ImageIcon(newimgUndo);
		
		undoButton.setBounds(60,645,45,45);
		undoButton.setFocusable(false);
		undoButton.addActionListener(this);
		undoButton.setIcon(undoIcon);
		
		ImageIcon chatIcon = new ImageIcon("./src/progettoServer/Imm/chat.png");
		Image imgChat = chatIcon.getImage() ;  
		Image newimgChat = imgChat.getScaledInstance( 35, 35,  java.awt.Image.SCALE_SMOOTH );  
		chatIcon = new ImageIcon(newimgChat);
		
		chatButton.setBounds(445,645,45,45);
		chatButton.setFocusable(false);
		chatButton.addActionListener(this);
		chatButton.setIcon(chatIcon);
		
		messageNameLabel.setBounds(285,510,250,20);
		messageNameLabel.setFont(new Font(null,Font.ITALIC,10));
		messageDescrLabel.setBounds(390,510,250,20);
		messageDescrLabel.setFont(new Font(null,Font.ITALIC,10));
		nameLabel.setBounds(60, 15, 775, 20);
		nameLabel.setFont(new Font(null,Font.ITALIC,20));
		nameLabel.setText("Progetto: " + projectName);
		
		frame.add(comboBoxAddMember);
		frame.add(comboBoxOpenCard);
		frame.add(showMemberButton);
		frame.add(addMemberButton);
		frame.add(showCardsButton);
		frame.add(addCardButton);
		frame.add(openCardButton);
		frame.add(refreshUsersButton);
		frame.add(refreshCardsButton);
		frame.add(chatButton);
		frame.add(undoButton);
		frame.add(deleteButton);
		frame.add(logoutButton);
		frame.add(addCardField);
		frame.add(addCardDescriptionField);
		frame.add(messageNameLabel);
		frame.add(messageDescrLabel);
		frame.add(nameLabel);
		frame.add(showMemberPanel);
		frame.add(showCardPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(550,800);
		frame.setLayout(null);
		frame.setVisible(true);
	}
	//Metodo che gestisce le interazioni con l'utente
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		if(e.getSource()==showMemberButton) {
			try {
				String msgToServer = "showMember" + "*" + projectName;
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
	            updateListBox.updateListBox(showMemberListUsers, resultLog);
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la richiesta di show member");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==addMemberButton) {
			try {
				String method = "addMember";
				String memberToAdd = (String) comboBoxAddMember.getSelectedItem();
				String msgToServer = method + "*" + projectName + "*" + memberToAdd;
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
				System.out.println("Errore durante la richiesta di addmember");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==showCardsButton) {
			String msgToServer = "showCards" + "*" + projectName;
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
	            updateListBox.updateListBox(showCardsListUsers, resultLog);
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la richiesta delle cards");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==addCardButton) {
			//String userID = this.userID;
			String cardName = addCardField.getText();
			String cardDescription = addCardDescriptionField.getText();
			
			if(cardName.equals("")) {
				messageNameLabel.setForeground(Color.red);
				messageNameLabel.setText("Text field is empty");
			} else {
				if(cardDescription.equals("")) {
					messageDescrLabel.setForeground(Color.red);
					messageDescrLabel.setText("Text field is empty");
				} else {
					if(cardName.contains("*")) {
						messageNameLabel.setForeground(Color.red);
						messageNameLabel.setText("Can't use '*'");
					} else {
						if(cardDescription.contains("*")) {
							messageDescrLabel.setForeground(Color.red);
							messageDescrLabel.setText("Can't use '*'");
						} else {
							String msgToServer = "addCard" + "*" + projectName + "*" + cardName + "*" + cardDescription;
							
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
								System.out.println("Errore l'aggiunta della card");
								exSocketLogin.printStackTrace();
							}
						}
					}
				}
			}
			
		}
		
		if(e.getSource()==openCardButton) {
			String userID = this.userID;
			String cardName = (String) comboBoxOpenCard.getSelectedItem();
			String msgToServer = "openCard"+ "*" + projectName + "*" + cardName;
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
	            if(!resultLog.equals("Deleted") && !resultLog.equals("cardDoesntExist")) {
	            	frame.dispose();
	    	    	@SuppressWarnings("unused")
	    			CardPage chatPage = new CardPage(userID, password, projectName, client, serverObject, port, BUFFER_DIMENSION, cardName, resultLog, callbackObj, listRegisteredUsers, listOnlineUsers, ipStart);
	            }
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante l'apertura della card");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==refreshUsersButton) {
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
	            updateComboBox.updateComboBox(comboBoxAddMember, resultLog);
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante il refresh get user");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==refreshCardsButton) {
			try {
				String msgToServer = "showCards" + "*" + projectName;
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
	            updateComboBox.updateComboBox(comboBoxOpenCard, resultLog);
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la il refresh cards");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==undoButton) {
			frame.dispose();
	    	@SuppressWarnings("unused")
			WelcomePage welcomePage = new WelcomePage(userID, password, client, serverObject, port, BUFFER_DIMENSION, false, callbackObj, listRegisteredUsers, listOnlineUsers, ipStart);
		}
		
		if(e.getSource()==chatButton) {
			try {
				String msgToServer = "openChat" + "*" + userID + "*" + projectName;
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
	            if(!resultLog.equals("Deleted")) {
	            	if(!resultLog.equals("failure")) {
		        		frame.dispose();
		    	    	@SuppressWarnings("unused")
		    			ChatPage chatPage = new ChatPage(userID, password, client, serverObject, port, BUFFER_DIMENSION, resultLog, callbackObj, listRegisteredUsers, listOnlineUsers, projectName, ipStart);	
		        	}
	            }
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante apertura chat");
				exSocketLogin.printStackTrace();
			}
		
		}
		
		if(e.getSource()==deleteButton) {
			try {
				String msgToServer = "deleteProject" + "*" + projectName;
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
            	if(!resultLog.equals("failure")) {
	        		frame.dispose();
	    	    	@SuppressWarnings("unused")
	    			WelcomePage welcomePage = new WelcomePage(userID, password, client, serverObject, port, BUFFER_DIMENSION, false, callbackObj, listRegisteredUsers, listOnlineUsers, ipStart);
	        	}
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante eliminazione progetto");
				exSocketLogin.printStackTrace();
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
	            serverObject.unregisterForCallback(userID);
				frame.dispose();
				@SuppressWarnings("unused")
				LoginPage loginPage = new LoginPage(ipStart, port);
				
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante il logout");
				exSocketLogin.printStackTrace();
			}
		}
	}

}
