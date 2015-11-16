import java.io.*;
import java.net.Socket;

/**
 * Created by priyadarshini on 11/14/15.
 */
public class Client {
    DataOutputStream writer;
    DataInputStream reader;

    public static void main(String[] args) {
        Client client = new Client();
        client.createSocket();
    }

    private void createSocket() {
        try {
            Socket client = new Socket("127.0.0.1", 30000);
            reader = new DataInputStream(client.getInputStream());
            writer = new DataOutputStream(client.getOutputStream());
            File file = new File("resources/Client/Sample.doc");
            int counter = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(counter<=100) {
                String nextString = br.readLine();
                System.out.println(nextString);
                writer.write(nextString.getBytes(), 0, 3);
                if(nextString.equals("GET")){
                    receiveFile(file, client, reader);
                } else if(nextString.equals("PUT")) {
                    sendFile(file, client, writer);
                } else if(nextString.equals("QUIT")){
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
