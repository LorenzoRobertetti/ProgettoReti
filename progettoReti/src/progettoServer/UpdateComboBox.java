package progettoServer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
//Classe che implementa i metodi per aggiornare i bottoni combobox
public class UpdateComboBox {
	
	public void updateComboBox(JComboBox<String> comboBox, String msg) {
		
		comboBox.removeAllItems();
		
		StringTokenizer st = new StringTokenizer(msg, "*");
		String toAdd;
		
		if(msg.equals("vuota")) return;
		
		List<String> list = new ArrayList<String>();
		
		while(st.hasMoreTokens()) {
			toAdd = st.nextToken();
			list.add(toAdd);
		}
		
		java.util.Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		
		for(String name : list) {
			comboBox.addItem(name);
		}
	
	}

}
