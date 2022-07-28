package progettoServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Random;

	//Classe che genera l'indirizzo multicast per un un nuovo progetto 
	public class MulticastAddress {
		private static final String startAddress = "224.0.0.0";
		private static final String endAddress = "239.255.255.255";

		private String address;
	
	
	public MulticastAddress() throws UnknownHostException {
		this.address = generateAddress();
	}
	
	
	public String getInetAddress() {
		return address;
	}
	
	
	 //Genera un nuovo indirizzo random 
	 //tra 224.0.0.0 e 239.255.255.255.
	 
	private String generateAddress() throws UnknownHostException {
		//Converto l'inizio e la fine a long int
		long start = ByteBuffer.allocate(8).putInt(0).
				put(InetAddress.getByName(startAddress).getAddress()).flip().getLong();
		long end = ByteBuffer.allocate(8).putInt(0).
				put(InetAddress.getByName(endAddress).getAddress()).flip().getLong();
		// Genero un long tra inizio e fine
		Random random = new Random();
		long r = Math.abs(random.nextLong()) % (end - start + 1) + start;
		// Converto long in stringa
		return String.valueOf(r);
	}
	
}
