import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
            readerObj = new ObjectInputStream(client.getInputStream());
            writerObj = new ObjectOutputStream(client.getOutputStream());
            reader = new DataInputStream(client.getInputStream());
            writer = new DataOutputStream(client.getOutputStream());
            File file = new File("resources/Client/sample.txt");
            int counter = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (counter <= 100) {
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
        if (currentDirectory.isDirectory()) {
            Directory rootDirectory = new Directory(directoryName);
            rootDirectory.populateDirectory(currentDirectory.listFiles());
            checkDirectory(receivedDirectory, rootDirectory, "");
            if (receivedDirectory.equals(rootDirectory)) {
                System.out.println("equal");
            } else {
                System.out.println("not equal");
            }
        } else {
            System.out.println("not pro dir");
        }

    }

    public boolean checkIfEqual(Directory thisDirectory, Directory thatDirectory) {
        if (thisDirectory.getDirectoryName().equals(thatDirectory.getDirectoryName())) {
            return true;
        }
        return false;
    }

    public boolean checkIfEqual(FileDetails thisFile, FileDetails thatFile) {
        if (thisFile.equals(thatFile)) {
            return true;
        }
        return false;
    }


    public boolean checkDirectory(Directory root1, Directory root2, String path) {
        boolean isEqualDirectory = false;
        path = path + root2.getDirectoryName() + "/";
        if (checkIfEqual(root1, root2)) {
            List<Directory> combinedList = createCombinedList(root1.directoryList, root2.directoryList);
            System.out.println(combinedList.size());
            System.out.println(root1.directoryList.size());
            System.out.println(root2.directoryList.size());
                for (Directory dir : combinedList) {
                    if (root1.containsDir(dir)) {
                        if (root2.containsDir(dir)) {
                            System.out.println("found in both client and server!");
                            Directory firstFile = root1.getByDirectoryName(dir.getDirectoryName());
                            Directory secondFile = root2.getByDirectoryName(dir.getDirectoryName());
                            checkDirectory(firstFile, secondFile, path);
                        } else {
                            System.out.println("found in root alone");
                            sendCommand("GETDIRECTORY " + path + dir.getDirectoryName());
                        }
                    } else if (root2.containsDir(dir)) {
                        System.out.println("found in client alone");
                        System.out.println(path + dir.getDirectoryName());
                        sendCommand("PUTDIRECTORY " + path + dir.getDirectoryName());
                    }
                }
            List<FileDetails> combinedFileList = createCombinedFileList(root1.fileDetailsList, root2.fileDetailsList);
                for (FileDetails dir : combinedFileList) {
                    if (root1.containsFile(dir)) {
                        if (root2.containsFile(dir)) {
                            FileDetails firstFile = root1.getByFileName(dir.fileName);
                            FileDetails secondFile = root2.getByFileName(dir.fileName);
                            if(!firstFile.isSame(secondFile)) {
                                if (firstFile.isLatestFile(secondFile)) {
                                    sendCommand("GETF " + path + dir.fileName);
                                } else {
                                    sendCommand("PUTF " + path + dir.fileName);
                                }
                            }

                        } else {
                            sendCommand("GETF " + path + dir.fileName);
                        }
                    } else if (root2.containsFile(dir)) {
                        sendCommand("PUTF " + path + dir.fileName);
                    }
                }
            }
        return isEqualDirectory;
    }

    private List<Directory> createCombinedList(List<Directory> directoryList, List<Directory> directoryList1) {
        List<Directory> newList = new ArrayList<>();
        for (Directory dir : directoryList) {
            newList.add(dir);
        }
        for (Directory dir : directoryList1) {
            if (!newList.contains(dir))
                newList.add(dir);
        }
        return newList;
    }

    private List<FileDetails> createCombinedFileList(List<FileDetails> fileList, List<FileDetails> fileList1) {
        List<FileDetails> newList = new ArrayList<>();
        for (FileDetails dir : fileList) {
            newList.add(dir);
        }
        for (FileDetails dir : fileList1) {
            if (!newList.contains(dir))
                newList.add(dir);
        }
        System.out.println(newList.size());
        return newList;
    }

    private void getFile(String filePath) {
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
            if (commandAndFile[0].equals("GETF")) {
                receiveFile(file, reader);
            } else if (commandAndFile[0].equals("GETDIRECTORY")) {
                getfDirectory(file, reader);
            } else if (commandAndFile[0].equals("PUTDIRECTORY")) {
                putfDirectory(file, writer);
            } else if (commandAndFile[0].equals("PUTF")) {
                sendFile(file, writer);
            } else if (commandAndFile[0].equals("SYNC")) {
                receiveDirectoryStructure();
            } else if (commandAndFile[0].equals("QUIT")) {
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getfDirectory(File file, DataInputStream reader) {
        Directory directory = receiveObject();
        System.out.println(file.getPath());
        getf(directory, reader, file.getPath());

    }

    private void getf(Directory dir,  DataInputStream reader, String path) {
        try {
            System.out.println(path);
            System.out.println(dir.getDirectoryName());
            File file = new File(path);
            if(!file.exists()){
                file.mkdir();
            }
            System.out.println(dir.getDirectoryName());
            for (Directory directory : dir.directoryList) {
                    getf(directory, reader, path);
            }
            for(FileDetails fileDetails : dir.fileDetailsList) {
                File inputFile = new File(path + "/" + fileDetails.fileName);
                receiveFile(inputFile, reader);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendObject(Directory rootDirectory) {
        try {
            writerObj.writeObject(rootDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putfDirectory(File file, DataOutputStream reader) {
        Directory dir = new Directory(file.getName());
        dir.populateDirectory(file.listFiles());
        sendObject(dir);
        String path = file.getPath();
        putf(dir, reader, path);

    }

    private void putf(Directory dir, DataOutputStream reader, String path) {
        try {

            System.out.println(path);
            for (Directory directory : dir.directoryList) {
                File inputFile = new File(directory.getDirectoryName());
                if (inputFile.isDirectory()) {
                    putf(directory, reader, inputFile.getPath());
                } else {
                    sendFile(inputFile, reader);
                }
            }
            for(FileDetails fileDetails : dir.fileDetailsList) {
                File inputFile = new File(path + "/" + fileDetails.fileName);
                sendFile(inputFile, reader);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Directory receiveObject() {
        Directory dirObj = null;
        try {
            dirObj = (Directory) readerObj.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dirObj;

    }

    private void sendFile(File dir, DataOutputStream oos) throws Exception {
        byte[] buff = new byte[client.getSendBufferSize()];
        int bytesRead = 0;

        InputStream fileReader = new FileInputStream(dir);
        long length = dir.length();

        oos.writeLong((int) length);

        while ((bytesRead = fileReader.read(buff)) > 0) {
            oos.write(buff, 0, bytesRead);
        }
        oos.flush();

        fileReader.close();
    }

    private void receiveFile(File dir, DataInputStream ois) throws Exception {
        if(!dir.exists()){
            dir.createNewFile();
        }
        FileOutputStream wr = new FileOutputStream(dir, false);
        byte[] outBuffer = new byte[client.getReceiveBufferSize()];
        int bytesReceived = 0;
        long fileSize = ois.readLong();
        while (fileSize > 0 && (bytesReceived = ois.read(outBuffer, 0, (int) Math.min(outBuffer.length, fileSize))) != -1) {
            wr.write(outBuffer, 0, bytesReceived);
            fileSize -= bytesReceived;
        }
        wr.flush();
        wr.close();

    }
}
