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

                //parse each command statement
                String[] parsedCommand = parseCommand(args[i]);
                String cmd = parsedCommand[0];
                String path = parsedCommand[1];
                String contentName = parsedCommand[2];
                //switch according to the command
                switch(cmd){
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
                        get(ftp, contentName, path);
                        break;
                    case "put":
                        put(ftp, contentName, path);
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

    private static void get(FTPClient ftp, String contentName, String curDir){
        try{
            //form the relative local filepath
            String filepath = curDir + "/" + contentName;

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
                System.out.println("get/curContentName: " + contentName + " get/testDirName: "  + testDirName);
                if(contentName.compareTo(testDirName) == 0){
                    System.out.println("setting isDirectory to true");
                    isDirectory = true;
                }
            }
            System.out.println("get/isDirectory: " + isDirectory);
            if(isDirectory){
                //if it's a directory
                //set as the working directory
                changeDirectory(ftp, contentName);
                //create it in local space
                File newDir = new File(filepath);
                newDir.mkdirs();
                //grab each of its
                //content names and take each down the
                //rabbit hole, include relative path
                String[] contentNames = ftp.listNames();
                for(String nextContentName : contentNames){
                    // String relativeFilePath = contentName + "/" + nextContentName;
                    // System.out.println("get/relativeFilePath: " + relativeFilePath);            //get the parent path
                    //recur
                    get(ftp, nextContentName, filepath);
                }

                //coming back up from recursion
                //set working directory to parent
                changeDirectory(ftp, "..");
                
            }
            else{
                //if it's a file, download it
                File file = new File(filepath);
                FileOutputStream outStream = new FileOutputStream(file);
                ftp.retrieveFile(contentName, (OutputStream) outStream);
                outStream.close();
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

    private static String[] parseCommand(String commandString){
        String[] parsedCommand = new String[3];
        //separate command and file/directory path
        String[] cmdAndPath = commandString.split(" ", 2);
        //split path into its separate directories/filename
        //only if it is a command other than ls
        if(cmdAndPath.length > 1){
            String[] path = cmdAndPath[1].split("/");
            //build back the path where file/dir is located
            String contentDir = "";
            for(int i = 0; i < path.length - 1; i++){
                contentDir += path[i] + "/";
            }
            //if contentDir is still "", then it is a local file/dir
            //make content dir "."
            if(contentDir == ""){
                contentDir = ".";
            }
            //return command, path where file/dir is located
            //and file/dir as separate strings
            String contentName = path[path.length -1];
            parsedCommand[1] = contentDir;
            parsedCommand[2] = contentName;
        }
        parsedCommand[0] = cmdAndPath[0];

        return parsedCommand;
    }

    private static void put(FTPClient ftp, String contentName, String curDir){
        try{
            String filepath = curDir + "/" + contentName;
            //create a File object using contentName
            File file = new File(filepath);
            //keep track of whether current content is a directory
            boolean isDirectory = file.isDirectory();

            System.out.println("put/isDirectory: " + isDirectory);
            if(isDirectory){
                //if it's a directory
                //make the directory in remote server
                makeDirectory(ftp, contentName);
                //set as the working directory
                changeDirectory(ftp, contentName);
                //grab each of its
                //content names in local dir and take each down the
                //rabbit hole
                String[] contentNames = file.list();
                for(String nextContentName : contentNames){
                    //form the next working dir
                    String nextDir = curDir + "/" + contentName;
                    put(ftp, nextContentName, nextDir);
                }
                //coming back up from recursion
                //set working directory to parent
                changeDirectory(ftp, "..");
                
            }
            else{
                //create the new File and inputStream
                // store it in remote server
                File newFile = new File(filepath);
                FileInputStream inStream = new FileInputStream(newFile);
                ftp.storeFile(contentName, inStream);
                inStream.close();
            }
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
                //rabbit hole
                String[] contentNames = ftp.listNames();
                for(String nextContentName : contentNames){
                    removeDirectory(ftp, nextContentName);
                }
                //coming back up from recursion
                //set working directory to parent
                changeDirectory(ftp, "..");
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