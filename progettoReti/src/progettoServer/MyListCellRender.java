package progettoServer;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
//Classe utilizzata per fare il render delle celle colorato per le liste online e registrate
public class MyListCellRender extends JLabel implements ListCellRenderer<Object>{

	private static final long serialVersionUID = 1L;
	private List<String> oldList;
	private List<String> middleList;
	private String color;

	public MyListCellRender(List<String> oldList, List<String> middleList, String color) {
		this.oldList = oldList;
		this.color = color;
		this.middleList = middleList;
    }
	//Metodo sovrascritto che si occupa di scegliere il colore della linea
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Assumes the stuff in the list has a pretty toString
    	String user = value.toString();
    	
        setText(user);

        
        if(color.equals("blue")) { //sto aggiornando la lista dei register
        	if(!oldList.isEmpty()) {
        		if(!oldList.contains(user)) {
            		setForeground(Color.BLUE); //posso solo aggiungere utenti
            	} else {
            		setForeground(Color.BLACK);
            	}
        	} else {
        		setForeground(Color.BLACK);
        	}
        } else { //Caso lista online users
        	if(color.equals("green")) {
        		if(!oldList.isEmpty()) {
            		if(!middleList.isEmpty()) {
            			if(oldList.contains(user) && middleList.contains(user)) {
            				setForeground(Color.BLACK);
            			} else {
            				if(oldList.contains(user)) {
            					setForeground(Color.GRAY);
            				} else {
            					setForeground(Color.GREEN);
            				}
            			}
            		} else {
            			setForeground(Color.GRAY);
            		}
            	} else {
            		setForeground(Color.GREEN);
            	}
        	} else {
        		setForeground(Color.black);
        	}
        }

        return this;
    }
}
