import java.net.*;
import java.io.*;

public class Assn5Server{
    public static void main(String args[]){
        try{
            int serverPort = 7896;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true){
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        }catch(IOException e){
            System.out.println("Listen: " + e.getMessage());
        }
    }
}
class Connection extends Thread{
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    public Connection(Socket aClientSocket){
        try{
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        }catch(IOException e){
            System.out.println("Connection: " + e.getMessage());
        }
    }
    public void run(){
        try{
            String data = in.readUTF();
            //reverse the string
            String reversedString = "";
            for(int i = data.length() -1; i >= 0; i--){
                reversedString = reversedString + data.charAt(i);
            }
            out.writeUTF(reversedString);
        }catch(EOFException e){
            System.out.println("EOF: " + e.getMessage());
        }catch(IOException e){
            System.out.println("IO: " + e.getMessage());
        }finally{
            try{
                clientSocket.close();
            }catch(IOException e){

            }
        }
    }
}