package progettoServer;
import java.rmi.*;
//Interfaccia dell'oggeto remoto esportato dal server
public interface UserServerInterface extends Remote{
	//Registrazione utente
	public String UserRegister(String user, String password) throws RemoteException;
	
	public void registerForCallback(NotifyEventInterface ClientInterface, String user) throws RemoteException;
	
	public void unregisterForCallback(String user) throws RemoteException;
	
}
