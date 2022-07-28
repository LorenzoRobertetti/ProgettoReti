package progettoServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ChatPage implements ActionListener {
	
	private String userID; //user id, server per tornare alla pagina di progetto 
	private String password; 
	private SocketChannel client; 
	private UserServerInterface serverObject;
	private NotifyEventImpl callbackObj;
	private int port;
	private String ipStart;
	private final int BUFFER_DIMENSION;
	private String ip;
	private InetAddress ipAddress;
	private int portMulticast;
	private TaskReader reader;
	private String projectName;
	private DatagramSocket ms = null;
	
	//Campi interfaccia grafica
	JFrame frame = new JFrame();
	JButton logoutButton = new JButton("Logout");
	JButton blueBellButton = new JButton();
	JButton greenBellButton = new JButton();
	JButton addMemberButton = new JButton("Add member");
	JButton refreshUsersButton = new JButton();
	JButton sendMessageButton = new JButton();
	JTextField messageField = new JTextField();
	JButton undoButton = new JButton();
	JLabel messageLabel = new JLabel();
	UpdateComboBox updateComboBox = new UpdateComboBox();
	UpdateListBox updateListBox = new UpdateListBox();
	JComboBox<String> comboBoxAddMember;
	JList<String> listOnlineUsers;
	JList<String> listRegisteredUsers;
	JList<String> listMessages =  new JList<String>();
	List<String> newListRegisterUsers;
	List<String> newListOnlineUsers;
	JPanel dataMessagesPanel = new JPanel(new BorderLayout());
	Thread myThread;
	

	public ChatPage(String userID, String password, SocketChannel client, UserServerInterface serverObject, int port, int bufferSize, String resultLog, NotifyEventImpl callbackObj,JList<String> listRegisteredUsers, JList<String> listOnlineUsers, String project, String ipStart) {
		//Tutti i paramentri necessari per tornare alle pagine precedenti correttamente, mantenendo lo stato del sistema client (callback, riferimenti agli oggetti, ecc..)

		this.userID = userID;
		this.password = password;
		this.client = client;
		this.serverObject = serverObject;
		this.port = port;
		this.ipStart = ipStart;
		this.BUFFER_DIMENSION = bufferSize;
		this.callbackObj = callbackObj;
		this.ip = resultLog;
		System.out.println(ip);
		try {
			this.ipAddress = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("Errore setting indirizzo multicast");
			e.printStackTrace();
		}
		this.portMulticast = 30000;
		try {
			this.ms = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.reader = new TaskReader(ip, portMulticast, listMessages, new ArrayList<String>());
		this.listOnlineUsers = listOnlineUsers;
		this.listRegisteredUsers = listRegisteredUsers;
		this.projectName = project;
		//Setting interfaccia grafica
		comboBoxAddMember = new JComboBox<String>();
		comboBoxAddMember.addActionListener(this);
		comboBoxAddMember.setBounds(495, 30, 160, 25);
		
		addMemberButton.setBounds(320,30,160,25);
		addMemberButton.setFocusable(false);
		addMemberButton.addActionListener(this);
		
		ImageIcon refreshIcon = new ImageIcon("./src/progettoServer/Imm/refresh.png");
		Image imgRefresh = refreshIcon.getImage() ;  
		Image newRefresh = imgRefresh.getScaledInstance( 15, 15,  java.awt.Image.SCALE_SMOOTH );  
		refreshIcon = new ImageIcon(newRefresh);
		
		refreshUsersButton.setBounds(670,30,25,25);
		refreshUsersButton.setFocusable(false);
		refreshUsersButton.addActionListener(this);
		refreshUsersButton.setIcon(refreshIcon);
		
		ImageIcon sendIcon = new ImageIcon("./src/progettoServer/Imm/send.png");
		Image imgSend = sendIcon.getImage() ;  
		Image newSend = imgSend.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH );
		sendIcon = new ImageIcon(newSend);
		
		sendMessageButton.setBounds(410,395,25,25);
		sendMessageButton.setFocusable(false);
		sendMessageButton.addActionListener(this);
		sendMessageButton.setIcon(sendIcon);
		
		messageField.setBounds(30,395,362,25);

		newListRegisterUsers = new ArrayList<String>();
		newListOnlineUsers = new ArrayList<String>();
		
		dataMessagesPanel.setBounds(30, 85, 665, 280);
		dataMessagesPanel.add(new JScrollPane(listMessages));
		dataMessagesPanel.setBackground(Color.GRAY);
		
		messageLabel.setBounds(30,395,250,35);
		messageLabel.setFont(new Font(null,Font.ITALIC,25));
		
		ImageIcon undoIcon = new ImageIcon("./src/progettoServer/Imm/undo.png");
		Image imgUndo = undoIcon.getImage() ;  
		Image newimgUndo = imgUndo.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH );  
		undoIcon = new ImageIcon(newimgUndo);
		
		undoButton.setBounds(650,395,25,25);
		undoButton.setFocusable(false);
		undoButton.addActionListener(this);
		undoButton.setIcon(undoIcon);
		
		
		frame.add(comboBoxAddMember);
		frame.add(addMemberButton);
		frame.add(undoButton);
		frame.add(refreshUsersButton);
		frame.add(sendMessageButton);
		frame.add(dataMessagesPanel);
		frame.add(messageField);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(725,500);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setResizable(false);
		//Starting del thread per la lettura della chat
		//task con il metodo run utilizzato dal thread dedicato alla lettura della chat
		this.startRead();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==addMemberButton) { //Richiesta per aggiungere un membro al progetto
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
				System.out.println("Errore durante la connessione login");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==refreshUsersButton) { //richiesta della lista degli utenti registrati
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
				System.out.println("Errore durante la connessione login");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==sendMessageButton) { //tasto per mandare un messaggio nella chat
			
			try {
				byte[] data;
				String s = userID + ":" + " " + messageField.getText();
				data = s.getBytes();
				if(data.length < 1024) {
					DatagramPacket dp = new DatagramPacket(data, data.length, ipAddress, portMulticast);
					ms.send(dp);
				}
				
			} catch(IOException ex) {
				System.out.println("Errore spedizione del messaggio");
				System.out.println(ex);
			}
		}
		
		if(e.getSource()==undoButton) { //Torno indietro alla schermata di progetto
			this.stopRead(); //Metodo per far terminare il thread lettore 
			frame.dispose();
	    	@SuppressWarnings("unused")
	    	ProjectPage projectPage = new ProjectPage(userID, password, projectName, client, serverObject, port, BUFFER_DIMENSION, callbackObj, listRegisteredUsers, listOnlineUsers, ipStart);
		}
	}
	//Metodo per startare il thread lettore della chat
	public void startRead() {
		Thread thread = new Thread(reader);
		thread.start();
		this.myThread = thread;
	}
	//metodo per terminarlo, poi lo aspetto prima di tornare alla pagina di progetto 
	public void stopRead() {
		reader.setOpen();
		reader.stop();
		try {
			myThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}