import java.rmi.*;
import java.rmi.server.*;

public class Assn6Server{
    public static void main(String args[]){
        try {
            if(args.length < 1){
                System.out.println("Usage: java Assn6Server <IP address>");
            }
            Method method = new Method();			   		   
            Naming.rebind("rmi://" + args[0] + "/cecs327", method);

            System.out.println("Method Server is ready.");
        }catch (Exception e) {
                System.out.println("Method Server failed: " + e);
        }
    }
}