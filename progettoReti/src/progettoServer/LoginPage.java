package progettoServer;

import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
//Classe per l'interfaccia di login
public class LoginPage implements ActionListener{
	//Variabili connessione
	private final int BUFFER_DIMENSION = 1024;
	
	private int port;
	private String ip;
	
	JFrame frame = new JFrame();
	JButton loginButton = new JButton("Login");
	JButton registerButton = new JButton("Register");
	JTextField userIDField = new JTextField();
	JPasswordField userPasswordField = new JPasswordField();
	JLabel userIDLabel = new JLabel("userID:");
	JLabel userPasswordLabel = new JLabel("password:");
	JLabel messageLabel = new JLabel();
	
	LoginPage(String ip, int port){
		this.ip = ip;
		this.port = port;
		//Setting interfaccia
		userIDLabel.setBounds(50,100,75,25);
		userPasswordLabel.setBounds(50,150,75,25);
		
		messageLabel.setBounds(125,250,250,35);
		messageLabel.setFont(new Font(null,Font.ITALIC,10));
		
		userIDField.setBounds(125,100,200,25);
		userPasswordField.setBounds(125,150,200,25);
		
		loginButton.setBounds(125,200,100,25);
		loginButton.setFocusable(false);
		loginButton.addActionListener(this);
		
		registerButton.setBounds(225,200,100,25);
		registerButton.setFocusable(false);
		registerButton.addActionListener(this);
		
		frame.add(userIDLabel);
		frame.add(userPasswordLabel);
		frame.add(messageLabel);
		frame.add(userIDField);
		frame.add(userPasswordField);
		frame.add(loginButton);
		frame.add(registerButton);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420,420);
		frame.setLayout(null);
		frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==registerButton) {
			try {
				//Prelevo userId e passw dall'area testuale
		    	String userID = userIDField.getText();
				String password = String.valueOf(userPasswordField.getPassword());
				if(userID.equals("") | password.equals("")) {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Text is empty");
				} else {
					if(userID.contains("*") | password.contains("*") | userID.contains(" ") | password.contains(" ") | userID.equals("ignoto")) {
						messageLabel.setForeground(Color.red);
						messageLabel.setText("Can't use * or space");
					}  else {
						//Quando viene cliccato il tasto di registrazione, prendo l'oggetto remoto
						UserServerInterface serverObject;
				    	Remote remoteObject = null;
				    	Registry r = LocateRegistry.getRegistry(this.ip, 1920);
				    	remoteObject = r.lookup("SERVEROBJ");
				    	serverObject = (UserServerInterface) remoteObject;
						//Invoco il metodo remoto 
						String resultReg = serverObject.UserRegister(userID, password);
						if(resultReg.equals("UsernameAlreadyUsed")) {
							messageLabel.setForeground(Color.red);
							messageLabel.setText("Username already exist");
						} 
						if(resultReg.equals("UsernameCorrectlyRegistered")) {
							messageLabel.setForeground(Color.green);
							messageLabel.setText("Username correctly registered");
						}
					}
				}
			} catch(Exception exObjectRemote) {
				System.out.println("Errore prelevazione oggetto remoto");
				exObjectRemote.printStackTrace();
			}
		}
		
		if(e.getSource()==loginButton) {
			try {
				
				String userID = userIDField.getText();
				String password = String.valueOf(userPasswordField.getPassword());
				
				String msgToServer = "login" + "*" + userID + "*" + password;
				
				if(userID.equals("") | password.equals("")) {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Text is empty");
				} else {
					if(userID.contains("*") | password.contains("*") | userID.contains(" ") | password.contains(" ")) {
						messageLabel.setForeground(Color.red);
						messageLabel.setText("Can't use * or space");
					} else {
						//Mi connetto al socket 
						SocketChannel client = SocketChannel.open(new InetSocketAddress(InetAddress.getByName(ip), port));
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
			            
			            //Elaboro risposta
			            if(resultLog.equals("success")) {
			            	try {
			    				//Prelevo oggetto per le callback
			    				UserServerInterface serverObject;
			    		    	Remote remoteObject = null;
			    		    	Registry r = LocateRegistry.getRegistry(ip, 1920);
			    		    	remoteObject = r.lookup("SERVEROBJ");
			    		    	serverObject = (UserServerInterface) remoteObject;
			    		    	frame.dispose();
			    		    	@SuppressWarnings("unused")
								WelcomePage welcomePage = new WelcomePage(userID, password, client, serverObject, port, BUFFER_DIMENSION, true, null, null, null, ip);
			    		    	return;
			    			} catch(Exception exObjectRemote) {
			    				System.out.println("Errore prelevazione oggetto remoto");
			    				exObjectRemote.printStackTrace();
			    			}
			            } else {
			            	if(resultLog.equals("idDoesntExist")) {
			            		messageLabel.setForeground(Color.red);
								messageLabel.setText("Username does not exit");
								client.close();
			            	}
			            	if(resultLog.equals("incorrectpassword")) {
			            		messageLabel.setForeground(Color.red);
								messageLabel.setText("Incorrect password");
								client.close();
			            	}
			            	if(resultLog.equals("alreadyOnline")) {
			            		messageLabel.setForeground(Color.red);
								messageLabel.setText("Already Online");
								client.close();
			            	}
			            }
					}
				}
				
			} catch (Exception exSocketLogin) {
				System.out.println("Errore durante la connessione login");
				exSocketLogin.printStackTrace();
			}
		}
	}	
}
