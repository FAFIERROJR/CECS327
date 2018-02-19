import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.File;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

public class Assn3{
    public static void main(String[] args){
        //print usage if bad command
        if(args.length < 1){
            System.out.println("Usage: java -cp \".:commons-net-3.6.jar\" Assn3.java ServerIPAddress Username:Password \"command\"...");
        }
        //instantiate ftp client
        FTPClient ftp = new FTPClient();

        //grab ip address, id, and password from command line
        String serverAddress = args[0];
        String[] idAndPassword = args[1].split(":");

        try{
            //connect and login
            ftp.connect(serverAddress);
            ftp.login(idAndPassword[0], idAndPassword[1]);

            for(int i = 2; i < args.length; i++){

                //split each command into tokens
                //will split a content's name if it contains spaces
                String[] tokens = args[i].split(" ");

                //put file/directory back together if it contained spaces
                String contentName = "";
                for(int j = 1; j < tokens.length; j++){
                    if(j > 1){
                        contentName += " ";
                    }
                    contentName += tokens[j];
                }

                //switch according to the command
                switch(tokens[0]){
                    case "ls":
                        listDirectory(ftp);
                        break;
                    case "cd":
                        changeDirectory(ftp, contentName);
                        break;
                    case "delete":
                        delete(ftp, contentName);
                        break;
                    case "get":
                        get(ftp, contentName);
                        break;
                    case "put":
                        put(ftp, contentName);
                        break;
                    case "mkdir":
                        makeDirectory(ftp, contentName);
                        break;
                    case "rmdir":
                        removeDirectory(ftp, contentName);
                        break;

                    default:
                        System.out.println("Unrecognized or missing command");
                        break;
                }
            }

            //logout and disconnect when done
            ftp.logout();
            ftp.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void changeDirectory(FTPClient ftp, String directoryName){
        try{
            //go up if ".."
            if(directoryName == ".."){
                ftp.changeToParentDirectory();
                return;
            }
            //change to the given directory
            ftp.changeWorkingDirectory(directoryName);
        }catch(Exception e){
            System.out.println("Operations failed: ");
            e.printStackTrace();
        }
    }

    private static void delete(FTPClient ftp, String fileName){
        try{
            ftp.deleteFile(fileName);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void get(FTPClient ftp, String contentName){
        try{
            //if receveing a path
            //extract just the name
            String[] path = contentName.split("/");
            String curContentName = path[path.length -1];

            System.out.println("get/contentName: " + contentName);
            // //grab the content's details
            // FTPFile curFile = ftp.mlistFile(contentName);
            //grab list of directories in current dir
            FTPFile[] directories = ftp.listDirectories();
            //keep track of whether current content is a directory
            boolean isDirectory = false;
            //check if given content name is a directory
            for(int i = 0; i < directories.length; i++){
                String[] absDirName = directories[i].getName().split("/"); //split the full path name 
                String testDirName = absDirName[absDirName.length -1]; //grab just the dir name
                System.out.println("get/curContentName: " + curContentName + " get/testDirName: "  + testDirName);
                if(curContentName.compareTo(testDirName) == 0){
                    System.out.println("setting isDirectory to true");
                    isDirectory = true;
                }
            }
            System.out.println("get/isDirectory: " + isDirectory);
            if(isDirectory){
                //if it's a directory
                //set as the working directory
                ftp.changeWorkingDirectory(curContentName);
                //create it in local space
                File newDir = new File(contentName);
                newDir.mkdirs();
                //grab each of its
                //content names and take each down the
                //rabbit hole, include relative path
                String[] contentNames = ftp.listNames();
                for(String nextContentName : contentNames){
                    String relativeFilePath = contentName + "/" + nextContentName;
                    System.out.println("get/relativeFilePath: " + relativeFilePath);            //get the parent path
                    get(ftp, relativeFilePath);
                }

                //coming back up from recursion
                //set working directory to parent
                ftp.changeToParentDirectory();
                
            }
            else{
                //if it's a file, download it
                File file = new File(contentName);
                FileOutputStream outStream = new FileOutputStream(file);
                ftp.retrieveFile(curContentName, (OutputStream) outStream);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static  void listDirectory(FTPClient ftp){
        try{
            //get a list of the directory's contents
            String[] directoryContents = ftp.listNames();
            //print it out
            System.out.println(ftp.printWorkingDirectory());
            for(int i = 0; i < directoryContents.length; i++){
                System.out.println("\t" + directoryContents[i]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void makeDirectory(FTPClient ftp, String directoryName){
        try{
            ftp.makeDirectory(directoryName);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void put(FTPClient ftp, String fileName){
        try{
            File file = new File(fileName);
            FileInputStream inStream = new FileInputStream(file);
            ftp.storeFile(fileName, inStream);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void removeDirectory(FTPClient ftp, String contentName){
        try{
            //grab list of directories in current dir
            FTPFile[] directories = ftp.listDirectories();
            //keep track of whether current content is a directory
            boolean isDirectory = false;
            //check if given content name is a directory
            for(int i = 0; i < directories.length; i++){
                String[] absDirName = directories[i].getName().split("/"); //split the full path name 
                String testDirName = absDirName[absDirName.length -1]; //grab just the dir name
                System.out.println("rm/curContentName: " + contentName + " rm/testDirName: "  + testDirName);
                if(contentName.compareTo(testDirName) == 0){
                    System.out.println("setting isDirectory to true");
                    isDirectory = true;
                }
            }
            System.out.println("rm/isDirectory: " + isDirectory);
            if(isDirectory){
                //if it's a directory
                //set as the working directory
                ftp.changeWorkingDirectory(contentName);
                //grab each of its
                //content names and take each down the
                //rabbit hole, include relative path
                String[] contentNames = ftp.listNames();
                for(String nextContentName : contentNames){
                    removeDirectory(ftp, nextContentName);
                }
                //coming back up from recursion
                //set working directory to parent
                ftp.changeToParentDirectory();
                //remove the emptied directory
                ftp.removeDirectory(contentName);
                
            }
            else{
                //if it's a file, delete it
                delete(ftp, contentName);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}