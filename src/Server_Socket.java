import java.io.IOException;
import java.net.ServerSocket;

public class Server_Socket {

    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new ClientThread(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Server_Socket server=new Server_Socket();
        server.start(6666);
    }
}