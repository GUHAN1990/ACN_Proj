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
            File resource = new File("resources/Sample.doc");

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
                    receiveFile(resource, socket, reader);
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


    private static void receiveFile(File dir, Socket sock, DataInputStream ois ) throws Exception {
        FileOutputStream wr = new FileOutputStream(dir);
        byte[] outBuffer = new byte[sock.getReceiveBufferSize()];
        int bytesReceived = 0;
        long fileSize = ois.readLong();
        while (fileSize > 0 && (bytesReceived = ois.read(outBuffer, 0, (int)Math.min(outBuffer.length, fileSize))) != -1)
        {
            wr.write(outBuffer,0,bytesReceived);
            fileSize -= bytesReceived;
        }
        wr.flush();
        wr.close();

    }

}
