import java.io.*;
import java.net.Socket;

/**
 * Created by priyadarshini on 11/14/15.
 */
public class Client {
    DataOutputStream writer;
    DataInputStream reader;
    ObjectInputStream readerObj;
    ObjectOutputStream writerObj;
    Socket client;
    String baseDirectory = "Client/";


    public static void main(String[] args) {
        Client client = new Client();
        client.createSocket();
    }

    private void createSocket() {
        try {

            client = new Socket("127.0.0.1", 30000);
//            Socket client = new Socket("127.0.0.1", 30000);
            readerObj = new ObjectInputStream(client.getInputStream());
            reader = new DataInputStream(client.getInputStream());
            writer = new DataOutputStream(client.getOutputStream());
            File file = new File("resources/Client/sample.txt");
            int counter = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(counter<=100) {
                String nextString = br.readLine();
                sendCommand(nextString);

                counter++;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveDirectoryStructure() {
        Directory receivedDirectory = receiveObject();
        String directoryName = receivedDirectory.getDirectoryName();
        File currentDirectory = new File(baseDirectory + directoryName);
        if(currentDirectory.isDirectory())
        {
            Directory rootDirectory = new Directory(directoryName);
            rootDirectory.populateDirectory(currentDirectory.listFiles());
            checkDirectory(receivedDirectory, rootDirectory, "");
            if(receivedDirectory.equals(rootDirectory)){
                System.out.println("equal");
            } else{
                System.out.println("not equal");
            }
        } else{
            System.out.println("not pro dir");
        }

    }

    public  boolean checkIfEqual(Directory thisDirectory, Directory thatDirectory) {
        if(thisDirectory.getDirectoryName().equals(thatDirectory.getDirectoryName())){
            return true;
        }
        return false;
    }

    public  boolean checkIfEqual(FileDetails thisFile, FileDetails thatFile) {
        if(thisFile.equals(thatFile)){
            return true;
        }
        return false;
    }


    public  boolean checkDirectory(Directory root1, Directory root2, String path) {
        boolean isEqualDirectory = false;
        path = path + root2.getDirectoryName() + "/";
        if(checkIfEqual(root1, root2)){
            if(root1.directoryList.size() == root2.directoryList.size()){
                for (int i=0; i< root1.directoryList.size(); i++){
                    isEqualDirectory = checkDirectory(root1.directoryList.get(i), root2.directoryList.get(i), path);
                }
            }
            if(root1.fileDetailsList.size() == root2.fileDetailsList.size()){
                for (int i=0; i< root1.fileDetailsList.size(); i++){
                    boolean isEqual = checkIfEqual(root1.fileDetailsList.get(i), root2.fileDetailsList.get(i));
                    if(!isEqual){
                        String filePath = path + root2.fileDetailsList.get(i).fileName;
                        System.out.println("File " + filePath + "is not equal!");
                        getFile(filePath);
                    }
                }
            }
        }
        return isEqualDirectory;
    }

    private void getFile(String filePath) {
        System.out.println("File " + filePath + " has been received!");
        sendCommand("GETF " + filePath);

    }

    private void sendCommand(String nextString) {
        try {
            System.out.println(nextString);
            long length = nextString.length();

            writer.writeLong((int) length);

            writer.write(nextString.getBytes(), 0, nextString.length());
            String[] commandAndFile = nextString.split("\\s+");
            File file = new File(baseDirectory + commandAndFile[1]);
            if(commandAndFile[0].equals("GETF")){
                receiveFile(file,reader);
            } else if(commandAndFile[0].equals("PUTF")) {
                sendFile(file, writer);
            } else if(commandAndFile[0].equals("SYNC")) {
                receiveDirectoryStructure();
            } else if(commandAndFile[0].equals("QUIT")){
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Directory receiveObject() {
        Directory dirObj = null;
        try {
            dirObj = (Directory)readerObj.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dirObj;

    }

    private void sendFile(File dir,DataOutputStream oos ) throws Exception {
        byte[] buff = new byte[client.getSendBufferSize()];
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


    private void receiveFile(File dir,DataInputStream ois ) throws Exception {
        FileOutputStream wr = new FileOutputStream(dir);
        byte[] outBuffer = new byte[client.getReceiveBufferSize()];
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
