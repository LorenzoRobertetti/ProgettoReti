package progettoServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
//Interfaccia oggetto remoto lato client, per le callback
public interface NotifyEventInterface extends Remote{
	
	public void notifyEvent(int color, List<String> list) throws RemoteException;
	
}
