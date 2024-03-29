import java.net.*;
import java.io.*;
import java.util.*;
    
public class SERVER_PROGRAM{
    private static final String my_pastry_id = "0331";

    public static void main(String args[]){
        DatagramSocket aSocket = null;
        try{
            Map<String, String> routing_table = new TreeMap<String, String>();
            Map<String, String> leaf_set = new TreeMap<String, String>();

            init_leaf_set(leaf_set);
            init_routing_table(routing_table);

            aSocket = new DatagramSocket(32710);

            while(true){
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);

                String request_string = new String(request.getData());
                request_string = request_string.trim();
                System.out.println("Request: " + request_string);

                String reply_string = null;
                if(!validate_input(request_string)){
                    System.out.print("INVALID REQUEST");
                    reply_string = "INVALID REQUEST";
                
                }else{
                    reply_string = getNearestNode(request_string, leaf_set, routing_table);
                    // System.out.print(reply_string);
                }

                DatagramPacket reply = new DatagramPacket(reply_string.getBytes(),
                    reply_string.getBytes().length, request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        } catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (aSocket != null)
             aSocket.close();}
    }

    private static Boolean validate_input(String request){
        if(request.matches("[0-3]{1,4}")){
            return true;
        }
        return false;
    }
    private static void init_leaf_set(Map<String, String> leaf_set){
        leaf_set.put("0323", ":54.191.77.116");
        leaf_set.put("0330", ":18.219.103.254" );
        leaf_set.put("0332", ":18.216.240.93");
        leaf_set.put("1013", ":18.144.32.67");
    }

    private static void init_routing_table(Map<String, String> routing_table){
        routing_table.put("0", "331:52.15.231.72");
        routing_table.put("1", "220:52.53.151.57");
        routing_table.put("2", "331:18.220.68.55");
        routing_table.put("3", "222:18.188.79.150");
        routing_table.put("00", "12:18.188.6.10");
        routing_table.put("01", "11:13.57.206.74");
        routing_table.put("02", "11:34.209.81.81");
        routing_table.put("03", "31:52.15.231.72");
        routing_table.put("030", "NULL");
        routing_table.put("031", "NULL");
        routing_table.put("032", "1:34.210.105.66");
        routing_table.put("033", "1:52.15.231.72");
        routing_table.put("0330", ":18.219.103.254");
        routing_table.put("0331", ":52.15.231.72");
        routing_table.put("0332", ":18.216.240.93");
        routing_table.put("0333", "NULL");
    }

    private static String getNearestNode(String requested_id, Map<String, String> leaf_set, Map<String, String> routing_table ){
        String nearest_node = null;
        nearest_node = search_leaves(requested_id, leaf_set);
        // System.out.println(nearest_node);

        if(nearest_node != null){
            return nearest_node;
        }

        nearest_node = search_routing_table(requested_id, routing_table);

        init_leaf_set(leaf_set);

        // if(nearest_node == null || nearest_node.equals(":NULL")){
        //     nearest_node = requested_id +"NULL";
        // }
        return nearest_node;
    }

    private static String search_leaves(String requested_id, Map<String, String> leaf_set){
        String nearest_node = leaf_set.get(requested_id);
        // if(requested_id.length() == 4 && nearest_node == null){
        //     try{
        //         int min = 4445;
        //         String min_key = null;
        //         int requested_id_num = Integer.parseInt(requested_id);
        //         int candidate_key_num = 0;
        //         for(String key: leaf_set.keySet()){
        //             candidate_key_num = Integer.parseInt(key);
        //             int abs_diff = Math.abs(requested_id_num - candidate_key_num);
        //             if(abs_diff < min){
        //                 min = abs_diff;
        //                 min_key = key;
        //             }
        //             nearest_node = leaf_set.get(min_key);
        //             return nearest_node;
        //         }
        //     }catch(Exception e){
        //         e.printStackTrace();
        //     }
        // }
        if(nearest_node == null){
            return nearest_node;
        }
        nearest_node = requested_id + nearest_node;
        return nearest_node;
    }

    // private static Boolean is_id_in_range(String requested_id, Set<String> keys){
    //     for(String key: keys){
    //         requested_id
    //     }
    // }
    private static String search_routing_table(String requested_id, Map<String, String> routing_table){
        String nearest_node = null;
        char[] requested_id_char_arr = requested_id.toCharArray();
        char[] my_id_char_arr = my_pastry_id.toCharArray();

        String working_key = "";
        for(int counter = 0; counter < requested_id_char_arr.length; counter++){
            if(requested_id_char_arr[counter] == my_id_char_arr[counter]){
                working_key += my_id_char_arr[counter];
                continue;
            }else{
                working_key +=  requested_id_char_arr[counter];
                break;
            }
        }
        nearest_node = linear_probe(working_key, routing_table);

        return nearest_node;
    }

    private static String linear_probe(String key, Map<String, String> routing_table){
        String nearest_node = null;
        nearest_node = routing_table.get(key);
        // System.out.println("initial probe: " + nearest_node);
        // if(nearest_node == null){
        //     String working_key = key.substring(0, key.length() - 2);;
        //     try{
        //         int right_most_digit = Integer.parseInt("" + key.charAt(key.length() - 1));
        //         while(nearest_node == null){
        //             right_most_digit += 1 % key_length;
        //             nearest_node = routing_table.get(working_key + right_most_digit);
        //         }
        //         nearest_node = working_key + right_most_digit + nearest_node;
        //         return nearest_node;
        //     }catch(Exception e){
        //         e.printStackTrace();
        //     }            return nearest_node;
        // }
        // System.out.print(nearest_node);
        if(nearest_node == null){
            nearest_node = "NULL";
        }
        
        if(nearest_node.equals("NULL")){
            return nearest_node;
        }
        nearest_node = key + nearest_node;
        return nearest_node;
    }
}
