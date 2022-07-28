package progettoServer;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
//Task che contiene il run per attivare il bottone della campanella 
public class TaskCallback implements Runnable{
	
	JButton bell;

	public TaskCallback(JButton bell) {
		this.bell = bell;
	}

	@Override
	public void run() {
		bell.setEnabled(true);
		System.out.println(SwingUtilities.isEventDispatchThread());
	}

}
