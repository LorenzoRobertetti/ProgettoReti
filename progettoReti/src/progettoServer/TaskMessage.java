package progettoServer;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
//Task utilizzato per aggiornare i messaggi sulla chat
public class TaskMessage implements Runnable{
	
	private JList<String> JListMessages;
	private List<String> listMessages;
 
	public TaskMessage(JList<String> JListMessages, List<String> listMessages) {
		this.JListMessages = JListMessages;
		this.listMessages = listMessages;
	}
	
	public void run() {
		
		DefaultListModel<String> demoList = new DefaultListModel<String>();
		
		demoList.addAll(listMessages);
		
		JListMessages.setModel(demoList);
		
	}

}
