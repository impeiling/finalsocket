package individualproject.finalsocket

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static individualproject.finalsocket.Server.clientCounter;
import static individualproject.finalsocket.Server.tokenSet;

public class clientHandler implements Runnable{
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private int id;

    public clientHandler(Socket socket){
        try{
            this.socket=socket;
            //create client handler socket input output stream
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.id = clientCounter.getAndAdd(1);
        } catch (IOException e) {
            //recall close everything
            closeEverything(socket,bufferedReader,bufferedWriter);

        }
    }

    @Override
    public void run() {
        String messageFromClient;
        String messageToClient = null;

        while (socket.isConnected()){
            try{
                System.out.println("Server: client"+id+" connected");
                messageFromClient=bufferedReader.readLine().trim();
                System.out.println("Server: received message from client"+id+"'"+messageFromClient+"'");

                if(messageFromClient.equals("RETRIEVE")){
                    if(tokenSet.isEmpty()){
                        messageToClient ="ERROR";
                    }else{
                        messageToClient="";
                        List<String> list = new ArrayList<String>(tokenSet);
                        for(String l:list){
                            messageToClient = l + " " +messageToClient;
                        }
                    }
                }
                //no response to quit
                if(messageFromClient.equals("QUIT")) break;
                //submit request is illegal
                if(messageFromClient.equals("SUBMIT")){
                    messageToClient="ERROR - unknown command";
                }
                //response to "submit token" command with received animal tokens
                if(messageFromClient.startsWith("SUBMIT")){
                    String token[]=messageFromClient.split(" ")
                    if(tokenSet.contains(token[1]))|| tokenSet.size()>=10){
                        messageToClient="ERROR - maximum number of tokens reached";

                    }else{
                        tokenSet.add(token[1]);
                        messageToClient = "OK";
                    }
                }
                //broadcast message to clients
                bufferedWriter.write(messageToClient);
                System.out.println("Server: sent response '"+messageToClient+"'to client" + id);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            } catch (IOException e) {
                //recall close
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }

    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter);
        try{
            if(bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
    }
}
}