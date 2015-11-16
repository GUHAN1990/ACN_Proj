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

//    PrintWriter writer;
    DataOutputStream writer;
    DataInputStream reader;
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
            writer = new DataOutputStream(socket.getOutputStream());
            reader = new DataInputStream(socket.getInputStream());
            File resource = new File("resources/sample.txt");

            int counter = 0;
            byte[] inputBuffer = new byte[10];
            while(counter<=100) {
                System.out.println("File received count" + counter);
                reader.read(inputBuffer, 0,3);
                String msg =new String(inputBuffer).trim();
                System.out.println("received msg "+msg);

                if (msg.equals("GET")) {
                    sendFile(resource, socket, writer);
                    System.out.println("Done");
                } else if (msg.equals("PUT")) {
//                    msg = reader.readLine();
//                    System.out.println("Received msg :" + msg);
                } else if (msg.equals("QUIT")) {
                    System.exit(0);
                }
                counter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(File dir, Socket sock, DataOutputStream oos ) throws Exception {
        byte[] buff = new byte[sock.getSendBufferSize()];
        int bytesRead = 0;

        InputStream fileReader = new FileInputStream(dir);
        long length = dir.length();

        oos.writeLong((int) length);

        while((bytesRead = fileReader.read(buff))>0) {
            oos.write(buff, 0, bytesRead);
        }
        oos.flush();

        fileReader.close();
    }


}
