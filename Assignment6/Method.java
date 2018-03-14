import java.rmi.*;
import java.rmi.server.*;

public class Method extends UnicastRemoteObject implements MethodInterface{
    public Method() throws RemoteException{

    }
    public int fibonacci(int n) throws RemoteException{
        if(n < 0){
            System.out.println("Error: fibonacci() requires natural number");
            return -1;
        }
        if( n == 0){
            return 0;
        }
        if(n == 1){
            return 1;
        }
        else return fibonacci(n-1) + fibonacci(n-2);
    }

    public int factorial(int n) throws RemoteException{
        if(n < 0){
            System.out.println("Error: factorial() requires natural number");
            return -1;
        }
        if(n == 0){
            return 1;
        }
        return n * factorial(n-1);
    }
}