package progettoServer;

//Classe per aggiornare le liste nei pannelli
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

public class UpdateListBox {
	//Metodo per aggiornare la lista
	public void updateListBox(JList<String> jListBox, String msg) { 
		
		jListBox.removeAll();
		
		StringTokenizer st = new StringTokenizer(msg, "*");
		String toAdd;
		
		if(msg.equals("vuota")) return;
		
		List<String> list = new ArrayList<String>();
		
		while(st.hasMoreTokens()) {
			toAdd = st.nextToken();
			list.add(toAdd);
		}
		//La ordino in ordine alfabetico
		java.util.Collections.sort(list, String.CASE_INSENSITIVE_ORDER); 
		
		System.out.println(list);
		
		DefaultListModel<String> demoList = new DefaultListModel<String>(); //nuovo modello
		
		demoList.addAll(list);		
		
		jListBox.setModel(demoList);
		
		jListBox.setCellRenderer(new MyListCellRender(null, null, "black"));
	
	}
	
	public void updateUnsortListBox(JList<String> jListBox, String msg) { //Ordine non alfabetico(utilizzato per la chat)
		
		jListBox.removeAll();
		
		StringTokenizer st = new StringTokenizer(msg, "*");
		String toAdd;
		
		if(msg.equals("vuota")) return;
		
		List<String> list = new ArrayList<String>();
		
		while(st.hasMoreTokens()) {
			toAdd = st.nextToken();
			list.add(toAdd);
		}
		
		DefaultListModel<String> demoList = new DefaultListModel<String>();
		
		demoList.addAll(list);		
		
		jListBox.setModel(demoList);
		
		jListBox.setCellRenderer(new MyListCellRender(null, null, "black"));
	
	}
	//Metodo per aggiornare le liste colorate
	//Effettua l'unione "insiemistica" tra la vecchia lista e la nuova lista poi colora le differenze
	//In base a quale lista ci si sta riferendo(online users o registered)
	public void updateDeepListBox(JList<String> jListBox, List<String> middleList, List<String> newList, String color) {
		
		ListModel<String> oldListModel = jListBox.getModel();
		List<String> oldList = new ArrayList<String>();
	
		for(int i = 0; i< oldListModel.getSize();i ++){
			oldList.add(oldListModel.getElementAt(i));
	    }
		
		jListBox.removeAll();
		
		if(newList.isEmpty()) return;
		
		java.util.Collections.sort(newList, String.CASE_INSENSITIVE_ORDER);
		
		DefaultListModel<String> demoList = new DefaultListModel<String>();
		
		demoList.addAll(newList);		
		
		jListBox.setModel(demoList);
		
		jListBox.setCellRenderer(new MyListCellRender(oldList, middleList, color));
		
	}

}