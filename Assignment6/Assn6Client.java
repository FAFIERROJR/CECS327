import java.rmi.*;

public class Assn6Client{
    public static void main(String args[]){
        try{
            if(args.length < 3){
                System.out.println("Usage: java Assn5Client <RMI Location> <fibonacci | factorial>" 
                    + "<non-negative integer>");
                System.exit(0);
            }

            String rmiLoc = args[0];
            String methodName = args[1];
            int argument = Integer.parseInt(args[2]);

            MethodInterface method = (MethodInterface) Naming.lookup(rmiLoc);

            int response = 0;
            switch(methodName){
                case "fibonacci":
                    response = method.fibonacci(argument);
                    System.out.println("The fibonacci of " + argument + " is " + response);
                    break;
                case "factorial":
                    response = method.factorial(argument);
                    System.out.println("The factorial of " + argument + " is " + response);
                    break;
                default:
                    System.out.println("Error: No such method exists");
                    break;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}