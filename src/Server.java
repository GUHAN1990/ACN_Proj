import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by priyadarshini on 11/14/15.
 */
public class Server{

    ServerSocket serverSocket;

    Server(){
        try {
            serverSocket = new ServerSocket(30000);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    private void startServer() {
        try {
            int count = 1;
            while (true) {
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket, count);
                count++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

class ServerThread extends Thread{

    PrintWriter writer;
    BufferedReader reader;
    Socket socket;
    int clientId;

    ServerThread(Socket socket, int count){
        this.socket = socket;
        this.clientId = count;
        start();
    }

    @Override
    public void run() {
        System.out.println("into run");
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            File resource = new File("resources/sample.txt");
            System.out.println(resource.getName());
            BufferedReader fileReader = new BufferedReader(new FileReader(resource));

            int counter = 0;
            while(counter<=100) {
                String msg = reader.readLine();
                if (msg.equals("GET")) {
                    sendFile(resource, socket, new ObjectOutputStream(socket.getOutputStream()));
                } else if (msg.equals("PUT")) {
                    msg = reader.readLine();
                    System.out.println("Received msg :" + msg);
                } else if (msg.equals("QUIT")) {
                    System.exit(0);
                }
                counter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(File dir, Socket sock, ObjectOutputStream oos ) throws Exception {
        byte[] buff = new byte[sock.getSendBufferSize()];
        int bytesRead = 0;

        InputStream in = new FileInputStream(dir);

        while((bytesRead = in.read(buff))>0) {
            oos.write(buff,0,bytesRead);
        }
        in.close();
        // after sending a file you need to close the socket and reopen one.
        oos.flush();
    }


}
