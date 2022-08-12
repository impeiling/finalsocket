package individualproject.finalsocket

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    //client constructor
    public client(Socket socket){
        try{
            this.socket =socket;
            //create client socket input and output stream
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            //close everything
            closeEverything(socket,bufferedWriter,bufferedReader);




        }

    }

    public void sendMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                try{
                    Scanner sc= new Scanner(System.in);
                    //connect server
                    while(socket.isConnected()){
                        //get commands from console input
                        String commands =sc.nextLine();
                        commands = commands.toUpperCase();
                        //send commands to server immediately
                        bufferedWriter.write(commands);
                        if(commands.equals("QUIT")) break;
                        System.out.println("Client: sent '"+commands+"' to server");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        //listen from group chat on the server
                        msgFromGroupChat = bufferedReader.readLine();
                        if(msgFromGroupChat == null){
                            msgFromGroupChat = "ERROR - unknown command";

                        }
                        System.out.println("Client: received message from server '"+msgFromGroupChat+"'");


                    }
                } catch (IOException e) {
                    closeEverything(socket,bufferedWriter,bufferedReader);
                }
            }
        }).start();
    }

    //close socket, buffer reader&writer


    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        //connect through 127.0.0.1 when bound to localhost
        Socket socket =new Socket("Localhost",9999);
        client client = new client(socket);
        client.sendMessage();
        System.out.println("Client: connected to the server");
    }
}
