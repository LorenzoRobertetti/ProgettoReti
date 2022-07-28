package progettoServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.rmi.registry.*;
 //Classe che implementa il servizio di server, con la classica struttura NIO
	class ServerServ {
	    
	    private final int BUFFER_DIMENSION = 1024; //Dimensione del buffer
	    
	    private final String EXIT_CMD = "exit";
	    
	    private final int port;
	    
	    private AtomicBoolean exit; //Variabile per la terminazione
	    
	    public ServerServ(int port){
	        this.port = port;
	        this.exit = new AtomicBoolean(false);
	    }
	   //Metodo di avvio
    public void start() {
        try (
                ServerSocketChannel s_channel = ServerSocketChannel.open();
        ){
            s_channel.socket().bind(new InetSocketAddress(this.port));
            s_channel.configureBlocking(false);
            Selector sel = Selector.open();
            s_channel.register(sel, SelectionKey.OP_ACCEPT);
            System.out.printf("Server: in attesa di connessioni sulla porta %d\n", this.port);
            //Esportazione oggetto remoto
            UserServerImpl userServerImpl = new UserServerImpl();
            userServerImpl.load();
            UserServerInterface stub = (UserServerInterface) UnicastRemoteObject.exportObject(userServerImpl,0);
            LocateRegistry.createRegistry(1920);
            Registry r = LocateRegistry.getRegistry(1920);
            r.rebind("SERVEROBJ", stub);
            //Inizializzo Manager request
            Data database = new Data();
            //Carico i dati
            database.load();
            ManagerRequest managerRequest = new ManagerRequest(userServerImpl.GetMap(), database, userServerImpl);
            //Avvio il thread di interazione con la console
            Thread thread = new Thread(new TaskConsoleReader(this, sel));
            thread.start();
            
            while(!exit.get()){
                if (sel.select() == 0)
                    continue;
                
                Set<SelectionKey> selectedKeys = sel.selectedKeys();
                
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    try {       
                        if (key.isAcceptable()) { 
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel c_channel = server.accept();
                            c_channel.configureBlocking(false);
                            System.out.println("Server: accettata nuova connessione dal client: " + c_channel.getRemoteAddress());
                            
                            this.registerRead(sel, c_channel, true, null); //Utente nuovo, metto a true il terzo argomento
                        } else if (key.isReadable()) {                  // READABLE
                            this.readClientMessage(sel, key, managerRequest);
                        } else {
                        	if (key.isWritable()) {                 // WRITABLE   
                            	this.answer(sel, key);
    	                    }
                        }
                    } catch (IOException e) {   //Terminazione del client con la 'x'
                    	System.out.println("Terminazione improvvisa!!");
                    	AttachmentInfo attachmentInfo = (AttachmentInfo) key.attachment();
                    	String user = attachmentInfo.getUser();
                    	System.out.println(user);
                    	if(!user.equals("ignoto")) {
                    		userServerImpl.unregisterForCallback(user);
                    		database.getOnlineUsers().remove(user);
                    		userServerImpl.doCallbacks(1, database.getOnlineUsers());
                    	}
                        key.channel().close();
                        key.cancel();
                    }
                    
                }
            }
            //Operazioni di uscita, quindi backup nella cartella 
            UnicastRemoteObject.unexportObject(userServerImpl, true);
            database.store();
            userServerImpl.store();
            return;
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //Metodo per registrare una chiave
    private void registerRead(Selector sel, SocketChannel c_channel, boolean newUser, AttachmentInfo attachmentInfo) throws IOException {

        // crea il buffer
        ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer message = ByteBuffer.allocate(BUFFER_DIMENSION);
        ByteBuffer[] bfs = {length, message};
        if(newUser) { //Se l'utente e' nuovo creo l'attachement con ignoto e ignoro il quarto argomento
        	AttachmentInfo attachmentInfoNew = new AttachmentInfo();
            attachmentInfoNew.setUser("ignoto");
            attachmentInfoNew.setBfs(bfs);
            c_channel.register(sel, SelectionKey.OP_READ, attachmentInfoNew);
        } else { //altrimenti utilizzo quello passato, era gia presente
        	attachmentInfo.setBfs(bfs);
        	c_channel.register(sel, SelectionKey.OP_READ, attachmentInfo);
        }
        
    }
    //Metodo per leggere i messaggi dal client
    private void readClientMessage(Selector sel, SelectionKey key, ManagerRequest managerRequest) throws IOException {
        
        SocketChannel c_channel = (SocketChannel) key.channel();
        // 
        AttachmentInfo attachmentInfo = (AttachmentInfo) key.attachment();
        ByteBuffer[] bfs = attachmentInfo.getBfs();
        if(c_channel.read(bfs) == -1) {
        	throw new IOException();
        }
        
        if (!bfs[0].hasRemaining()){
            bfs[0].flip();
            int l = bfs[0].getInt();

            if (bfs[1].position() == l) {
                bfs[1].flip();
                String msg = new String(bfs[1].array()).trim();
                System.out.printf("Server: ricevuto %s\n", msg);
                String answer = managerRequest.ElaborateRequest(msg, attachmentInfo);
                
                if (answer.equals(this.EXIT_CMD)){
                    System.out.println("Server: chiusa la connessione con il client " + c_channel.getRemoteAddress());
                    c_channel.close();
                    key.cancel();
                } else {
                	attachmentInfo.setAnswer(answer);
                    c_channel.register(sel, SelectionKey.OP_WRITE, attachmentInfo);
                }
            }
        }
    }
    //Metodo per rispondere al client
    private void answer(Selector sel, SelectionKey key) throws IOException {
        SocketChannel c_channel = (SocketChannel) key.channel();
        AttachmentInfo attachmentInfo = (AttachmentInfo) key.attachment();
        String echoAnsw = (String) attachmentInfo.getAnswer();
        ByteBuffer bbEchoAnsw = ByteBuffer.wrap(echoAnsw.getBytes());
        c_channel.write(bbEchoAnsw);
        System.out.println("Server: " + echoAnsw + " inviato al client " + c_channel.getRemoteAddress());
        if (!bbEchoAnsw.hasRemaining()) {
            bbEchoAnsw.clear();
            this.registerRead(sel, c_channel, false, attachmentInfo);
        }
    }
    //metodo per uscire dal loop e terminare il programma
    public void setExit() {
    	this.exit.set(true);
    }
    
}