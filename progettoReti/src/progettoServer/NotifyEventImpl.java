package progettoServer;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class NotifyEventImpl extends RemoteObject implements NotifyEventInterface{
	//Oggetto remoto lato client
	private static final long serialVersionUID = 1L;
	private boolean greenChanged;
	private boolean blueChanged;
	private JButton blueBell;
	private JButton greenBell;
	private List<String> newListRegisterUsers;
	private List<String> newListOnlineUsers;
	private Object syncObject;
	
	public NotifyEventImpl (JButton blueBell, JButton greenBell, Object syncObject,List<String> newListRegisterUsers, List<String> newListOnlineUsers) {
		this.blueBell = blueBell;
		this.greenBell = greenBell;
		this.syncObject = syncObject;
		this.newListOnlineUsers = newListRegisterUsers;
		this.newListOnlineUsers = newListOnlineUsers;
		this.greenChanged = false;
		this.blueChanged = false;
	}
	//Metodo invocato dal server per effettuare la notifica
	public void notifyEvent(int color, List<String> list) throws RemoteException {
		if(color == 0) {
			synchronized (syncObject){
				this.newListRegisterUsers = list;
				this.blueChanged = true;
				SwingUtilities.invokeLater(new TaskCallback(blueBell));
			}
		} else {
			synchronized (syncObject){
				this.newListOnlineUsers = list;
				this.greenChanged = true;
				SwingUtilities.invokeLater(new TaskCallback(greenBell));
			}
		}
	}
	//Variabile che serve per memorizzare la notifica quando sono in altre pagine diverse dalla welcome
	public void setGreenChanged(boolean state) {
		synchronized(syncObject) {
			this.greenChanged = state;
		}
	}
	
	public void setBlueChanged(boolean state) {
		synchronized(syncObject) {
			this.blueChanged = state;
		}
	}
	//Ottengo il valore delle variabili se voglio capire se c'e' stata una notifica 
	public boolean getGreenChanged() {
		synchronized(syncObject) {
			return this.greenChanged;
		}
	}
	
	public boolean getBlueChanged() {
		synchronized(syncObject) {
			return this.blueChanged;
		}
	}
	//Metodi per ottenere le liste aggiornate 
	public List<String> getNewListRegisterUsers() {
		return this.newListRegisterUsers;
	}
	
	public List<String> getNewListOnlineUsers() {
		return this.newListOnlineUsers;
	}

}
