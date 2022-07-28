package progettoServer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//Classe che rappresenta le funzionalita' dell'interfaccia grafica relativa alla page della card
//Quando una card viene aperta, viene mostrata la finestra generata da questa classe
public class CardPage implements ActionListener {
	
	private String userID;
	private String password;
	private String projectName;
	private String cardName;
	private String state;
	private String ipStart;
	private SocketChannel client;
	private UserServerInterface serverObject;
	private NotifyEventImpl callbackObj;
	private final int port;
	private final int BUFFER_DIMENSION;
	
	//Campi interfaccia grafica
	JFrame frame = new JFrame();
	JButton getCardHistoryButton = new JButton("Add member"); //bottone per card history
	JButton moveCardButton = new JButton();	//bottone per spostare una card
	JButton undoButton = new JButton(); //bottone per tornare indietro
	JTextArea textDescription = new JTextArea(); //area per la descrizione della card
	JLabel nameLabel = new JLabel(); //label per il nome della card
	JLabel fromLabel = new JLabel(); //label per lo stato della card
	JLabel toLabel = new JLabel(); //label per la scritta "To"
	UpdateComboBox updateComboBox = new UpdateComboBox(); //oggetto per aggiornare il contenuto del comboBox
	UpdateListBox updateListBox = new UpdateListBox(); //oggetto per aggiornare la lista contenuta nel pannello per gli spostamenti della card
	JComboBox<String> comboBoxTo; 
	JList<String> listOnlineUsers; // lista per gli utenti online per non perdere le callback che arrivano
	JList<String> listRegisteredUsers; //lista per gli utenti registrati per non perdere le callback che arrivano
	JList<String> listCardHistory = new JList<String>(); //lista per gli spostamenti della card
	JPanel cardHistoryPanel = new JPanel(new BorderLayout());  //pannelli 
	JPanel descriptionPanel = new JPanel(new BorderLayout());
	

	public CardPage(String userID, String password, String projectName, SocketChannel client, UserServerInterface serverObject, int port, int bufferSize, String cardName, String resultLog, NotifyEventImpl callbackObj,JList<String> listRegisteredUsers, JList<String> listOnlineUsers, String ipStart) {
		//Tutti i paramentri necessari per tornare alle pagine precedenti correttamente, mantenendo lo stato del sistema client (callback, riferimenti agli oggetti, ecc..)
		this.userID = userID;
		this.password = password;
		this.client = client;
		this.projectName = projectName;
		this.serverObject = serverObject;
		this.port = port;
		this.ipStart = ipStart;
		this.BUFFER_DIMENSION = bufferSize;
		this.callbackObj = callbackObj; //server per mantenere i risultati delle callback 
		this.cardName = cardName;
		this.listOnlineUsers = listOnlineUsers;
		this.listRegisteredUsers = listRegisteredUsers;
		
		//Divido le stringhe che il server mi ha restituito ottenendone lo stato della card e la descrizione
		StringTokenizer st = new StringTokenizer(resultLog,"*");
		String state = st.nextToken();
		String description = st.nextToken();
		this.state = state;
		
		getCardHistoryButton.setBounds(393,30,302,25);
		getCardHistoryButton.setText("Get History");
		getCardHistoryButton.setFocusable(false);
		getCardHistoryButton.addActionListener(this);
		
		moveCardButton.setBounds(573,257,122,25);
		moveCardButton.setFocusable(false);
		moveCardButton.setText("Move");
		moveCardButton.addActionListener(this);
		
		ImageIcon undoIcon = new ImageIcon("./src/progettoServer/Imm/undo.png");
		Image imgUndo = undoIcon.getImage() ;  
		Image newimgUndo = imgUndo.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH );  
		undoIcon = new ImageIcon(newimgUndo);
		
		undoButton.setBounds(30,290,30,30);
		undoButton.setFocusable(false);
		undoButton.setIcon(undoIcon);
		undoButton.addActionListener(this);
		
		comboBoxTo = new JComboBox<String>();
		comboBoxTo.addActionListener(this);
		comboBoxTo.setBounds(393, 257, 160, 25);
		//Dipendenze per la lista che appare nel combobox 
		switch(this.state) 
        { 	
			case "To Do": 
            	String[] set0 = {"In progress"};
            	comboBoxTo.setModel(new DefaultComboBoxModel<String>(set0));
            	break; 
            case "In progress": 
                String[] set1 = {"To be revised", "Done"};
                comboBoxTo.setModel(new DefaultComboBoxModel<String>(set1));
                break; 
            case "To be revised": 
            	String[] set2 = {"In progress", "Done"};
                comboBoxTo.setModel(new DefaultComboBoxModel<String>(set2));
                break; 
            case "Done": 
                comboBoxTo.setEnabled(false);
                moveCardButton.setEnabled(false);
                break; 
            default: 
                System.out.println("no match"); 
        } 
		//Continua il setting dell'interfaccia
		cardHistoryPanel.setBounds(393, 70, 302, 172);
		cardHistoryPanel.add(new JScrollPane(listCardHistory));
		cardHistoryPanel.setBackground(Color.GRAY);
		
		textDescription.setText(description);
		textDescription.setEditable(false);
		textDescription.setLineWrap(true);
		textDescription.setWrapStyleWord(true);
		
		descriptionPanel.setBounds(30, 70, 302, 172);
		descriptionPanel.add(new JScrollPane(textDescription));
		descriptionPanel.setBackground(Color.GRAY);
		
		nameLabel.setBounds(30,30,302,25);
		nameLabel.setFont(new Font(null,Font.ITALIC,25));
		nameLabel.setText("Card " + cardName);
		
		fromLabel.setBounds(30,257,302,25);
		fromLabel.setFont(new Font(null,Font.ITALIC,25));
		fromLabel.setText("From: " + state);
		
		toLabel.setBounds(342,257,40,25);
		toLabel.setFont(new Font(null,Font.ITALIC,25));
		toLabel.setText("To");
		
		frame.add(nameLabel);
		frame.add(undoButton);
		frame.add(getCardHistoryButton);
		frame.add(descriptionPanel);
		frame.add(cardHistoryPanel);
		frame.add(fromLabel);
		frame.add(toLabel);
		frame.add(comboBoxTo);
		frame.add(moveCardButton);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(725,400);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setResizable(false);
		
	}
	
	//Tutte le azioni che avvengono dopo il click del mouse su una risorsa visibile dell'interfaccia grafica
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==undoButton) { //Torno alla schermata di progetto
			frame.dispose();
			@SuppressWarnings("unused")
			ProjectPage projectPage = new ProjectPage(userID, password, projectName, client, serverObject, port, BUFFER_DIMENSION, callbackObj, listRegisteredUsers, listOnlineUsers, ipStart);
		}
		
		if(e.getSource()==getCardHistoryButton) { //Chiedo al server la history della card
			//String from = this.state;
			String to = (String) comboBoxTo.getSelectedItem();
			String msgToServer = "getCardHistory"+ "*" + projectName + "*" + cardName + "*" + to;
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
	            updateListBox.updateUnsortListBox(listCardHistory, resultLog);
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante richiesta della card history");
				exSocketLogin.printStackTrace();
			}
		}
		
		if(e.getSource()==moveCardButton) { //Chiedo al server di spostare una card allo stato "to" selezionato nel combobox
			String to = (String) comboBoxTo.getSelectedItem();
			String msgToServer = "moveCard"+ "*" + projectName + "*" + cardName + "*" + to;
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
	            	this.state = to;
	            	fromLabel.setText("From: " + state);
	                switch(to) //se lo spostamento ha avuto successo, aggiorno il combobox per le opzioni di spostamento 
	                { 
	                    case "In progress": 
	                        String[] set1 = {"To be revised", "Done"};
	                        comboBoxTo.setModel(new DefaultComboBoxModel<String>(set1));
	                        break; 
	                    case "To be revised": 
	                    	String[] set2 = {"In progress", "Done"};
	                        comboBoxTo.setModel(new DefaultComboBoxModel<String>(set2));
	                        break; 
	                    case "Done": 
	                        comboBoxTo.setEnabled(false);
	                        moveCardButton.setEnabled(false);
	                        break; 
	                    default: 
	                        System.out.println("no match"); 
	                } 
	            }
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la richiesta dello spostamento della card");
				exSocketLogin.printStackTrace();
			}
		}
		
	}

}
