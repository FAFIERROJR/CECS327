import java.rmi.*;
import java.rmi.server.*;

public class Assn6Server{
    public static void main(String args[]){
        try {
            if(args.length < 1){
                System.out.println("Usage: java Assn6Server <IP address>");
            }
            //set the server ip as the rmi hostname
            System.setProperty("java.rmi.server.hostname", args[0]);

            Method method = new Method();
            //added port 1099		   		   
            Naming.rebind("rmi://localhost:1099/cecs327", method);

            System.out.println("Method Server is ready.");
            //added port 1099
            //changed to give remote ip
            System.out.println("Use: rmi://" + args[0] + ":1099/cecs327");
        }catch (Exception e) {
                System.out.println("Method Server failed: " + e);
        }
    }
}