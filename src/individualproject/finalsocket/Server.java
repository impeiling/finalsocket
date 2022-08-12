
package individualproject.finalsocket

import java.awt.datatransfer.Clipboard;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private ServerSocket serverSocket;
    //use treeSet to avoid the duplicates
    static Set<String> tokenSet = new TreeSet<String>();
    //atomic integer to count the client number;
    private int id;
    public static AtomicInteger clientCounter = new AtomicInteger();

    //server constructor
    public Server(ServerSocket serverSocket){
        this.serverSocket= serverSocket;
        this.id=clientCounter.getAndAdd(1);

    }

    public void startServerSocket() {
        try{
            System.out.println("Server: waiting for a client to connect");
            //server socket start to receive the clients requests
            while (!serverSocket.isClosed()){
                //start listening client request
                Socket socket=serverSocket.accept();
                //thread handles the client
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread((Runnable) clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //finally, close server socket
            try{
                if(serverSocket!=null){
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException{
        //server listens on port 9999 to connect through 127.0.0.1
        ServerSocket serverSocket = new ServerSocket(9999);
        Server server=new Server(serverSocket);
        System.out.println("Server: waiting for a client to connect");
        server.startServerSocket();


    }

}
