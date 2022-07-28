package progettoServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserServerImpl extends RemoteServer implements UserServerInterface{
	
	private String path = "./src/progettoServer/Store/";

	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String,String> userRegisterMap;
	private List<String> users;
	private List<NotifyEventInterface> clients;
	private Object syncObj;
	private ConcurrentHashMap<String,NotifyEventInterface> userReference;
	
	public UserServerImpl() {
		super();
		this.userRegisterMap = new ConcurrentHashMap<String, String>();
		this.userReference = new ConcurrentHashMap<String,NotifyEventInterface>();
		this.users = new ArrayList<String>();
		this.clients = new ArrayList<NotifyEventInterface>();
		this.syncObj = new Object();
	}
	
	
	public String UserRegister(String user, String password) throws RemoteException {
		
		if (userRegisterMap.containsKey(user)) {
			return "UsernameAlreadyUsed";
		} else {
			userRegisterMap.put(user, password);
			users.add(user);
			this.doCallbacks(0, users);
			return "UsernameCorrectlyRegistered";
		}
	}
	
	public ConcurrentHashMap<String,String> GetMap() {
		return userRegisterMap;
	}


	@Override
	public void registerForCallback(NotifyEventInterface ClientInterface, String user) throws RemoteException {
		synchronized (syncObj) {
			if(!clients.contains(ClientInterface)) {
				clients.add(ClientInterface);
				userReference.put(user, ClientInterface);
				System.out.println("Client registrato alla callback");
			}
		}
		
	}


	@Override
	public void unregisterForCallback(String user) throws RemoteException {
		
		synchronized(syncObj) {
			NotifyEventInterface reference = userReference.get(user);
			if(clients.remove(reference) && (userReference.remove(user) != null)) {
				System.out.println("client unregistered for callback");
			} else {
				System.out.println("unable to unregister client");
			}
		}
		
		
	}
	
	public void doCallbacks(int color, List<String> list) throws RemoteException {
		synchronized(syncObj) {
			System.out.println("Starting callbacks");
			if(!clients.isEmpty()) {
				System.out.println(clients.size());
				Iterator<NotifyEventInterface> i = clients.iterator();
				
				while(i.hasNext()) {
					NotifyEventInterface client = (NotifyEventInterface) i.next();
					client.notifyEvent(color, list);
				}
			}
		}
	}
	
	public void store() {
		
		try {
			
			FileOutputStream f = new FileOutputStream(new File(path + "registered.txt"));
	        ObjectOutputStream o = new ObjectOutputStream(f);

	        // Write objects to file
	        o.writeObject(this.userRegisterMap);

	        o.close();
	        f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void load() {
		
		try {
			
			boolean check = new File(path + "registered.txt").exists();
			
			if(!check) return;
			
			FileInputStream fi = new FileInputStream(new File(path + "registered.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            @SuppressWarnings("unchecked")
			ConcurrentHashMap<String,String> registered = (ConcurrentHashMap<String,String>) oi.readObject();
            
            Set<String> setKey = registered.keySet();
            users.addAll(setKey);
            
            this.userRegisterMap = registered;
 

            oi.close();
            fi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
