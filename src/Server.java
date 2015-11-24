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
    ObjectInputStream readerObj;
    ObjectOutputStream writerObj;
    Directory rootDirectory;


    Socket socket;
    int clientId;

    ServerThread(Socket socket, int count){
        this.socket = socket;
        this.clientId = count;
//        this.rootDirectory = rootDirectory;
        start();
    }

    @Override
    public void run() {

        try {
            writerObj = new ObjectOutputStream(socket.getOutputStream());
            writer = new DataOutputStream(socket.getOutputStream());
            reader = new DataInputStream(socket.getInputStream());

            int counter = 0;
            while(counter<=100) {
                System.out.println("File received count" + counter);
                int commandSize = (int)reader.readLong();
                byte[] inputBuffer = new byte[commandSize];
                reader.read(inputBuffer, 0, commandSize);
                String msg =new String(inputBuffer).trim();
                System.out.println("Received msg "+msg);
                String[] commandAndFile = msg.split("\\s+");
                File file = new File(commandAndFile[1]);

                if (commandAndFile[0].equals("GETF")) {
                    sendFile(file, socket, writer);
                    System.out.println("Done");
                } else if (commandAndFile[0].equals("PUTF")) {
                    receiveFile(file, socket, reader);
                } else if (commandAndFile[0].equals("SYNC")) {
                    sendDirectoryStructure(commandAndFile[1]);
                }else if (commandAndFile[0].equals("QUIT")) {
                    System.exit(0);
                }
                counter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDirectoryStructure(String dirName) {
        File file = new File(dirName);
        Directory rootDirectory = new Directory(dirName);

        rootDirectory.populateDirectory(file.listFiles());
        System.out.println(rootDirectory);

        sendObject(rootDirectory);
    }

    private void sendObject(Directory rootDirectory) {
        try {
            writerObj.writeObject(rootDirectory);
        } catch(Exception e){
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
