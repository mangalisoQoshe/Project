import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class Client_Socket {
    private static Socket clientSocket;
    private PrintWriter out;
    private static BufferedReader in;
    private static String fileName;
    private static Scanner scan;

    public static void assertEquals(String text, String response){
        if(response.equals(text)){
            System.out.println("This is the server response");//////////////////////////////
        }
    }

    public static void main(String[] args) throws IOException {
    	
    	scan = new Scanner(System.in);

        Client_Socket client = new Client_Socket();
        InetAddress inetAddress = InetAddress.getLocalHost();
        client.startConnection("localhost", 6666);
        
        boolean done = false; 
        
        String type;
        String command;
        boolean loop = true;
            System.out.println("1.LogIn - lg\n2.SignUp- su\nEnter command:");
            System.out.print("> ");
            String msg = scan.nextLine();

            if(msg.equals("lg"))
            {
               String logInData= getUsrNameAndPassword();
               client.sendMessage("lg");
               client.sendMessage(logInData);
               if(in.readLine().equalsIgnoreCase("true"))
               {
                   loop = false;
               }
                else {
                   System.out.println("< Incorrect Password or User Name ");
                   done = true;
               }
            }
            else if(msg.equals("su"))
                {
                    String signUpData= getUsrNameAndPassword();
                    client.sendMessage("su");
                    client.sendMessage(signUpData);

                }
            else{
               System.out.println("< Invalind command");
                done = true;
            }

               
               
        while(done == false) {
             
            System.out.println("1. Download - dl\n2. Upload - ul\n3. quit - q\n4. Read - ls\nEnter command:");
           	System.out.print("> ");
    	      String message = scan.nextLine();

            
            if(message.equals("ul")) {
                System.out.println("< Enter file name: (case sensitive)");
                System.out.print("> ");
                String FileName = scan.nextLine();

                System.out.println("< passwordStatus Protect?(y/n)");
                System.out.print("> ");
                String passwordStatus = scan.nextLine().toUpperCase();
                String password = "";
                if(passwordStatus.equals("Y")) {
                    passwordStatus = "K1";
                    System.out.print("< Enter password:\n> ");
                    password = scan.nextLine();
                }
                else {passwordStatus = "K0";}

                System.out.println("< Who can view the file?(u/g/w)");
                System.out.print("> ");
                String viewer = scan.nextLine().toUpperCase();

                String permissions = "S";
                if(passwordStatus.equals("k0")){
                    permissions = "S";
                }
                else if(passwordStatus.equals("K1")){
                    permissions = "P";
                }

                type = "D";
                command = message;

                String header = command + "_" +passwordStatus+viewer+permissions+type;
                String resp1 = client.sendMessage(header + "_" + password);
                sendFile(FileName);
            }
            else if(message.equals("dl")) {
                System.out.println("< Enter file name: (case sensitive)");
                System.out.print("> ");
                String FileName = scan.nextLine();

                String resp1 = client.sendMessage(message+FileName);
                String resp2 = client.sendMessage(message);

                if(resp2.equals("0")){receiveFile(FileName);}
                else{
                    String response[] = resp2.split("#");
                    for (int i = 0; i < response.length; i++){
                        System.out.println(response[i]);
                    }
                    System.out.print("> ");
                    String p = scan.nextLine() + "\n";
                    //out.println(p);

                    receiveFile(FileName);
                }
            }
            else if(message.equals("ls")) {
                String resp1 = client.sendMessage(message);
                String resp2 = client.sendMessage(message);
                String files[] = resp2.split("#");
                for (int i = 0; i < files.length; i++){
                    System.out.println(files[i]);
                }
            }
            else if(message.equals("q")) {
            	break;
            }
            else {System.err.println("< Invalind command");}
            
            System.out.println("\n______________________________________________________________");
        	System.out.print("> ");
        	message = scan.nextLine();
        }        
        System.out.println("< Closing....");
       clientSocket.close();
        in.close();
        scan.close();

    }
    private static String getUsrNameAndPassword()
    {
         System.out.println("Enter User Name:");
         System.out.print("> ");
       	String usrName = scan.nextLine();
         System.out.println("Enter Password:");
         System.out.print("> ");
       	String passWord = scan.nextLine();
         return usrName+"_"+passWord;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void sendFile(String file) {
        try {
            
            fileName = file;

            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];
            
		    if(!myFile.exists()) {
				System.err.println("< File does not exist..");
				return;
			}

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);
		
            OutputStream os = clientSocket.getOutputStream();

            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            
            System.out.println("< File "+fileName+" sent to Server.");
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public static void receiveFile(String fileName) {
        try {
            int bytesRead;
            InputStream in = clientSocket.getInputStream();

            DataInputStream clientData = new DataInputStream(in);
            String path = "";
            String name = clientData.readUTF();
            fileName = path+name;
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            in.close();

            System.out.println("< File "+fileName+" received from Server.");
        } catch (IOException e) {
        	e.printStackTrace();
         }
    
}//space_cataclysm_planet_art_explosion_asteroids_comets_fragments_98315_1920x1080.jpg

}