package progettoServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JList;
import javax.swing.SwingUtilities;

public class TaskReader implements Runnable{
	//Task che implementa il metodo run per leggere i messagi dalla chat
	String ip;
	int port;
	private int BUFFER_SIZE = 1024;
	private AtomicBoolean open;
	private JList<String> JListMessages;
	private List<String> messages;
	private MulticastSocket myMs;
	private InetAddress myGroup;

	public TaskReader(String ip, int port, JList<String> JListMessages, List<String> messages) {
		this.ip = ip;
		this.port = port;
		this.JListMessages = JListMessages;
		this.messages = messages;
		this.open = new AtomicBoolean(true);
		System.out.println(ip);
	}

	@Override
	public void run() {
		InetAddress group = null;
		try {
			group = InetAddress.getByName(ip);
			this.myGroup = group;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MulticastSocket ms = null;
		try {
			ms = new MulticastSocket(port);
			this.myMs = ms;
			ms.joinGroup(group);
			byte[] buffer = new byte[BUFFER_SIZE];
			
			while(open.get()) {
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				ms.receive(dp);
				if(open.get()) {
					String msg = new String(dp.getData(), dp.getOffset(), dp.getLength());
					messages.add(msg);
					SwingUtilities.invokeLater(new TaskMessage(JListMessages, messages));
				}
			}
			return;
		} catch (IOException ex) {
			if(open.get()) {
				ex.printStackTrace();
			}
		}
	}
	//Metodo per terminare il thread che invoca il metodo run, si chiude il socket e tramite la catch lo chiudo correttamente
	public void stop() {
		if(myMs != null) {
			try {
				myMs.leaveGroup(myGroup);
				myMs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setOpen() {
		this.open.set(false);
	}

}
