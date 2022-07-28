package progettoServer;

import java.nio.channels.Selector;
import java.util.Scanner;
//Task che contiene il run utilizzato dal thread che interagisce con la console lato server,
//Mi serve per terminare il programma correttamente, attende la scrittura della parola 'exit'
public class TaskConsoleReader implements Runnable{
	
	ServerServ serverServ;
	Selector sel;
	
	public TaskConsoleReader(ServerServ serverServ, Selector sel) {
		this.serverServ = serverServ;
		this.sel = sel;
	}

	public void run() {
		String reply = "";
		Scanner scanner = new Scanner(System.in);
        while(!reply.equals("exit")) {
            try {
                reply = scanner.nextLine();
            } catch(Exception e) {
                e.printStackTrace();
                scanner.close();
            }
        }
        scanner.close();
        serverServ.setExit(); //invoca il metodo per chiudere il server
        sel.wakeup(); //se il servere e' in attesa sul selector, lo sveglia
        return;
	}

}
