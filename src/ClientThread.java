import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;

class ClientThread extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scan;
    private static ArrayList<Client> clientList;


    public ClientThread(Socket socket) {
        this.clientSocket = socket;
        clientList = new ArrayList<>();

    }

    public void run() {
    	
    	scan = new Scanner(System.in);
    	
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             
            //receiveFile();
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
            	out.println("");
                if ("ul".equals(inputLine.subSequence(0, 2))) {
                	receiveFile();
                	System.out.println(inputLine.subSequence(2,inputLine.length()));
                	out.println(inputLine);
                	//break;
                }
                else if ("dl".equals(inputLine.subSequence(0, 2))) {
                    String path = "DataBase\\";
                    String name = inputLine.substring(2,inputLine.length());

                    File files = new File("DataBase\\Files.txt");
                    Scanner scan = new Scanner(files);
                    String[] lineArray = new String[2];
                    while (scan.hasNextLine()){
                        String line = scan.nextLine();

                        String[] tempLineArray = line.split("#");

                        if (tempLineArray[0].equals(name)){
                            if (tempLineArray[1].equals("0")){
                                out.println("0");
                                sendFile(path,name);
                                break;
                            }
                            else{
                                out.println("< The file is password protected#< Enter Password:");

                                String password = in.readLine();
                                //System.out.println(in.readLine());
                                if (password.equals(tempLineArray[1])){
                                    sendFile(path,name);
                                    break;
                                }
                            }
                        }

                    }
                    scan.close();
                }
                else if ("ls".equals(inputLine)) {
                    System.out.println("Reading files...");
                    File files = new File("DataBase\\Files.txt");
                    Scanner scan = new Scanner(files);
                    String list = "";
                    while(scan.hasNextLine()){
                        list += scan.nextLine().split("#")[0] + "#";
                    }
                    System.out.println("Done");
                    out.println(list);
                }
                else if("q".equals(inputLine.subSequence(0, 1))) {
                    out.close();
                    in.close();
                    clientSocket.close();
                	break;
                }
                 else if("lg".equals(inputLine.subSequence(0, 2))) {
                     String usrNameAndPassword = in.readLine();
                   readFile();
                   String[] elements = usrNameAndPassword.split("_");
                   Client user= new Client(elements[0],elements[1]);
                   boolean verified = verify(user);
                   out.println(verified);
                   out.println(verified);
                   if(!verified)
                   {
                      System.out.println("< Incorrect Password or User Name : "+usrNameAndPassword);
                      break;
                   }

                }

                 else if("su".equals(inputLine.subSequence(0,2)))
                {
                    //System.out.println("signUp");
                    String usrNameAndPassword = in.readLine();
                    String[] elements = usrNameAndPassword.split("_");
                    Client user= new Client(elements[0],elements[1]);
                    clientList.add(user);
                    readFile();
                    writeFile();
                }
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out.println("ytcutycuytcut");
            }

            out.println("Choose||||||||||||||||||||||||||||||");
 
            out.close();
            in.close();
           // clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

    public static boolean verify(Client obj)
    {
        for(Client i : clientList)
        {
            return obj.equals(i);
        }
        return false;
    }
    
    public static byte[] extract(byte[] bytes) {
		
		int start = bytes.length-bytes[0]-1;
		
		byte d[] = new byte[start];
		
		int e = 0;
		for(int i = start; i <bytes.length; i++) {
			d[e] = bytes[i];
			e++;
		}
		
		return d;
		
	}
    
    public void receiveFile() {
        try {
            int bytesRead;

            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());

            String path = "DataBase\\";
            String name = clientData.readUTF();
            OutputStream output = new FileOutputStream(path+name);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            System.out.println("> File "+name+" received from client.");
            output.close();
            //clientData.close();
        } catch (IOException ex) {
            System.err.println("> Client error. Connection closed.");
        }
    }

    public void sendFile(String path, String name) {
        try {

            File myFile = new File(path+name);  //handle file reading
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);


            OutputStream os = clientSocket.getOutputStream();  //handle file send over socket

            DataOutputStream dos = new DataOutputStream(os); //Sending file name and file size to the server
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+name+" sent to client.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        }
    }//
    
     public void readFile()
      {
        try
        {
           
           Scanner read = new Scanner(new FileReader("usrData.txt"));
           while (read.hasNextLine())
           {

               String line = read.nextLine();
               String[] elements = line.split("_");
               clientList.add(new Client(elements[0],elements[1]));
           }
              read.close();
        }
        catch (Exception e)
        {
            System.out.println("> File Not Found : usrData.txt");
        }
        
    }
    
    private synchronized void writeFile()
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("usrData.txt")));
            for(Client i: clientList)
            {
                String stuffToWrite = i.toString();
                writer.write(stuffToWrite+"\n");
            }
            
            writer.close();
        } catch (Exception e) {
            System.err.println("File write Exception "+e);
        }
    }

}
