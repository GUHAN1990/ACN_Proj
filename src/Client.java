import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by priyadarshini on 11/14/15.
 */
public class Client {
    BufferedReader reader;
    PrintWriter writer;

    public static void main(String[] args) {
        Client client = new Client();
        client.createSocket();
    }

    private void createSocket() {
        try {
            Socket client = new Socket("127.0.0.1", 30000);
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
            File file = new File("resources/Server/sample.txt");
            int counter = 0;
            while(counter<=100) {
                Scanner sc = new Scanner(System.in);
                String nextString = sc.next();
                writer.println(nextString);
                if(nextString.equals("GET")){
                    receiveFile(file, client, new ObjectInputStream(client.getInputStream()));
                } else if(nextString.equals("PUT")) {
                    writer.println("this is the msg from client");
                } else if(nextString.equals("QUIT")){
                    System.exit(0);
                }

                counter++;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receiveFile(File dir, Socket sock, ObjectInputStream ois ) throws Exception {
        FileOutputStream wr = new FileOutputStream(dir);
        byte[] outBuffer = new byte[sock.getReceiveBufferSize()];
        int bytesReceived = 0;
        while((bytesReceived = ois.read(outBuffer))>0) {
            wr.write(outBuffer,0,bytesReceived);
        }
        wr.flush();
        wr.close();

    }
}
