import java.net.*;
import java.io.*;
import java.util.*;

public class CLIENT_PROGRAM{
    private static Random rand;
    private static final String my_ip = "52.15.231.72";
    private static final String my_id = "0331";

    public static void main(String args[]){
        rand = new Random();
        
        String requested_id = "";
        int rounds = 1000;
        int cur_round = 0;
        int num_hops_per_round[] = new int[rounds];

        while(cur_round < rounds){
            requested_id = getRandId();
            int result = 0;

            if(requested_id.equals(my_id)){
                result = -1;
            }else{
                result = findNode(requested_id);
            }

            if(result > 0){
                num_hops_per_round[cur_round] = result;
                System.out.println("Success: " + num_hops_per_round[cur_round] + " hops Round: " + cur_round);
                cur_round += 1;
            }
        }

        serializeData(num_hops_per_round);
        try{
            Process p = Runtime.getRuntime().exec("python3 genhist.py");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String getRandId(){
        String rand_id = "";
        for(int i = 0; i < 4; i++){
            int digit = rand.nextInt() % 4;
            rand_id += digit;
        }
        rand_id = rand_id.replace("-", "");
        return rand_id;
    }

    private static String queryServer(String request_id, String ip_address){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            aSocket.setSoTimeout(2000);
            byte [] m = request_id.getBytes();
            InetAddress aHost = InetAddress.getByName(ip_address);
            int serverPort = 32710;
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            aSocket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            // System.out.println("Reply: " + new String(reply.getData()));
            return new String(reply.getData());
        } catch(SocketTimeoutException e){
            e.printStackTrace();
        } catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally { 
            if(aSocket != null)
            aSocket.close();
        }
        return null;
    }

    private static int findNode(String requested_id){
        int num_hops = 0;
        String received_id = "";
        String received_ip = my_ip;
        String reply = "";
        String reply_tokens[] = new String[2];

        // System.out.println("Requested id:" + requested_id);

        //querying intermediary servers
        do{
            num_hops++;
            // System.out.println("Received id: " + received_id);
            reply = queryServer(requested_id, received_ip);

            reply_tokens = parseReply(reply);
            if(reply_tokens == null){
                return -1;
            }
            System.out.println("node: " + requested_id + " hop: " + num_hops);
            
            received_id = reply_tokens[0];
            if(received_ip.equals(reply_tokens[1])){
                return -1;
            }
            received_ip = reply_tokens[1];

            if(received_ip.equalsIgnoreCase("NULL")){
                return num_hops;
            }
        }while(!received_id.equals(requested_id));

        reply = queryServer(requested_id, received_ip);
        reply_tokens = parseReply(reply);

        return num_hops;
    }

    private static void serializeData(int[] num_hops_per_round){
        try{
            FileOutputStream fos = new FileOutputStream("numhops.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(num_hops_per_round);
            oos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String[] parseReply(String reply){
        //timeout
        if(reply == null){
            return null;
        }
        reply = reply.replace(" ", "");
        if(reply.contains("NULL") || reply.contains("null")){
            String[] reply_tokens = {"NULL", "NULL"};
            return reply_tokens;
        }
        
        String[] reply_tokens = reply.split(":");
        if(reply_tokens.length < 2){
            return null;
        }

        return reply_tokens;
    }



}