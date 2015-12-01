package com.example.acn;

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
            int counter = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (counter <= 100) {
                System.out.println("Please enter a command: ");
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
        } else {
            System.out.println("Not proper directory!");
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
                for (Directory dir : combinedList) {
                    if (root1.containsDir(dir)) {
                        if (root2.containsDir(dir)) {
                            Directory firstFile = root1.getByDirectoryName(dir.getDirectoryName());
                            Directory secondFile = root2.getByDirectoryName(dir.getDirectoryName());
                            checkDirectory(firstFile, secondFile, path);
                        } else {
                            sendCommand("GETDIRECTORY " + path + dir.getDirectoryName());
                        }
                    } else if (root2.containsDir(dir)) {
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

    private void sendCommand(String nextString) {
        try {
            System.out.println(nextString);
            String[] commandAndFile = nextString.split("\\s+");

            if(commandAndFile.length!=2){
                System.out.println("Invalid Input! Invalid input from client! ");
                return;
            }

            long length = nextString.length();

            writer.writeLong((int) length);

            writer.write(nextString.getBytes(), 0, nextString.length());
            File file = new File(baseDirectory + commandAndFile[1]);
            if (commandAndFile[0].equals("GETF")) {
                if(getStatus())
                    receiveFile(file, reader);
            } else if (commandAndFile[0].equals("GETDIRECTORY")) {
                getfDirectory(file, reader);
            } else if (commandAndFile[0].equals("PUTDIRECTORY")) {
                putfDirectory(file, writer);
            } else if (commandAndFile[0].equals("PUTF")) {
                if(file.exists()) {
                    sendFile(file, writer);
                } else {
                    System.out.println("Error in input file!");
                }
            } else if (commandAndFile[0].equals("SYNC")) {
                if(getStatus())
                    receiveDirectoryStructure();
            } else if (commandAndFile[0].equals("QUIT")) {
                System.exit(0);
            } else {
                System.out.println("Invalid Input! Please enter valid input!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getStatus() {
        int commandSize = 0;
        try {
            commandSize = (int) reader.readLong();

            byte[] inputBuffer = new byte[commandSize];
            reader.read(inputBuffer, 0, commandSize);
            String msg = new String(inputBuffer).trim();
            System.out.println("Status :" + msg);
            if(msg.contains("Error")){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
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
