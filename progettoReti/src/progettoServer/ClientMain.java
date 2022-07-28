package progettoServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientMain {
	
	//Main del programma, avvia semplicemente la schermata di login dopo il parsing degli argomenti 
    final static int DEFAULT_PORT = 9999;

    public static void main(String[] args){
    	int myPort = DEFAULT_PORT;
    	String ip = null;
        if (args.length > 0) {
        	ip = args[0];
        	try {
				InetAddress.getByName(ip);
			} catch (UnknownHostException e1) {
				System.out.println("Non e' stato possibile trasformare l'indirizzo ip");
				System.exit(-1);
			}
            try {
                myPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Fornire il numero di porta come intero");
                System.exit(-1);
            }
            
        }
        // crea e avvia il client
        @SuppressWarnings("unused")
		LoginPage loginPage = new LoginPage(ip, myPort);
    }

}