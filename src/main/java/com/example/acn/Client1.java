package com.example.acn;//import java.io.*;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by priyadarshini on 11/14/15.
// */
//public class Client1 {
//    DataOutputStream writer;
//    DataInputStream reader;
//    ObjectInputStream readerObj;
//    ObjectOutputStream writerObj;
//    Socket client;
//    String baseDirectory = "Client/";
//
//
//    public static void main(String[] args) {
//        Client client = new Client();
//        client.createSocket();
//    }
//
//    private void createSocket() {
//        try {
//
//            client = new Socket("127.0.0.1", 30000);
////            Socket client = new Socket("127.0.0.1", 30000);
//            readerObj = new ObjectInputStream(client.getInputStream());
//            reader = new DataInputStream(client.getInputStream());
//            writer = new DataOutputStream(client.getOutputStream());
//            File file = new File("resources/Client/sample.txt");
//            int counter = 0;
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            while (counter <= 100) {
//                String nextString = br.readLine();
//                sendCommand(nextString);
//
//                counter++;
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void receiveDirectoryStructure() {
//        Directory receivedDirectory = receiveObject();
//        String directoryName = receivedDirectory.getDirectoryName();
//        File currentDirectory = new File(baseDirectory + directoryName);
//        if (currentDirectory.isDirectory()) {
//            Directory rootDirectory = new Directory(directoryName);
//            rootDirectory.populateDirectory(currentDirectory.listFiles());
//            checkDirectory(receivedDirectory, rootDirectory, "");
//            if (receivedDirectory.equals(rootDirectory)) {
//                System.out.println("equal");
//            } else {
//                System.out.println("not equal");
//            }
//        } else {
//            System.out.println("not pro dir");
//        }
//
//    }
//
//    public boolean checkIfEqual(Directory thisDirectory, Directory thatDirectory) {
//        if (thisDirectory.getDirectoryName().equals(thatDirectory.getDirectoryName())) {
//            return true;
//        }
//        return false;
//    }
//
//    public boolean checkIfEqual(FileDetails thisFile, FileDetails thatFile) {
//        if (thisFile.equals(thatFile)) {
//            return true;
//        }
//        return false;
//    }
//
//
//    public boolean checkDirectory(Directory root1, Directory root2, String path) {
//        boolean isEqualDirectory = false;
//        path = path + root2.getDirectoryName() + "/";
//        if (checkIfEqual(root1, root2)) {
//            List<Directory> combinedList = createCombinedList(root1.directoryList, root2.directoryList);
//            System.out.println(combinedList.size());
//            System.out.println(root1.directoryList.size());
//            System.out.println(root2.directoryList.size());
//            if (combinedList.size() != root1.directoryList.size() || combinedList.size() != root2.directoryList.size()) {
//                for (Directory dir : combinedList) {
//                    if (root1.containsDir(dir)) {
//                        if (root2.containsDir(dir)) {
//                            System.out.println("found in both client and server!");
//                            checkDirectory(dir, dir, path);
//                            System.out.println(dir);
//                        } else {
//                            System.out.println("found in root alone");
//                            //send command getf for folder
//                            System.out.println(path + dir.getDirectoryName());
//                            sendCommand1("GETF " + path + dir.getDirectoryName(), dir);
//                            System.out.println(dir);
//                        }
//                    } else if (root2.containsDir(dir)) {
//                        System.out.println("found in client alone");
//                        //send command putf for folder
//                        System.out.println(path + dir.getDirectoryName());
//                        sendCommand("PUTF " + path + dir.getDirectoryName());
//                        System.out.println(dir);
//                    }
//                }
//            }
////            for (int j=0; j< root2.directoryList.size(); j++){
////                for (int i=0; i< root1.directoryList.size(); i++){
////                    Directory rootDir2 = root2.directoryList.get(j);
////                    Directory rootDir1 = root2.directoryList.get(i);
//////                    System.out.println(rootDir1.directoryName.equals(rootDir2.getDirectoryName()));
//////                    System.out.println(rootDir2.directoryName + " " + rootDir1.getDirectoryName());
////                    isEqualDirectory = checkDirectory(root1.directoryList.get(i), rootDir2, path);
////                }
////            }
//            if (root1.fileDetailsList.size() == root2.fileDetailsList.size()) {
//                for (int i = 0; i < root1.fileDetailsList.size(); i++) {
//                    boolean isEqual = checkIfEqual(root1.fileDetailsList.get(i), root2.fileDetailsList.get(i));
//                    if (!isEqual) {
//                        String filePath = path + root2.fileDetailsList.get(i).fileName;
////                        System.out.println("File " + filePath + "is not equal!");
//                        getFile(filePath);
//                    }
//                }
//            }
//        }
//        return isEqualDirectory;
//    }
//
//    private List<Directory> createCombinedList(List<Directory> directoryList, List<Directory> directoryList1) {
//        List<Directory> newList = new ArrayList<>();
//        for (Directory dir : directoryList) {
//            newList.add(dir);
//        }
//        for (Directory dir : directoryList1) {
//            if (!newList.contains(dir))
//                newList.add(dir);
//        }
////        System.out.println(newList.size());
//        return newList;
//    }
//
//    private List<FileDetails> createCombinedFileList(List<FileDetails> fileList, List<FileDetails> fileList1) {
//        List<FileDetails> newList = new ArrayList<>();
//        for (FileDetails dir : fileList) {
//            newList.add(dir);
//        }
//        for (FileDetails dir : fileList1) {
//            if (!newList.contains(dir))
//                newList.add(dir);
//        }
//        System.out.println(newList.size());
//        return newList;
//    }
//
//    private void getFile(String filePath) {
//        System.out.println("File " + filePath + " has been received!");
//        sendCommand("GETF " + filePath);
//
//    }
//
//    private void sendCommand(String nextString) {
//        try {
//            System.out.println(nextString);
//            long length = nextString.length();
//
//            writer.writeLong((int) length);
//
//            writer.write(nextString.getBytes(), 0, nextString.length());
//            String[] commandAndFile = nextString.split("\\s+");
//            File file = new File(baseDirectory + commandAndFile[1]);
//            if (commandAndFile[0].equals("GETF")) {
//                getf(file, reader);
//                //receiveFile(file, reader);
//            } else if (commandAndFile[0].equals("PUTF")) {
//                putf(file, writer);
////                sendFile(file, writer);
//            } else if (commandAndFile[0].equals("SYNC")) {
//                receiveDirectoryStructure();
//            } else if (commandAndFile[0].equals("QUIT")) {
//                System.exit(0);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendCommand1(String nextString) {
//        try {
//            System.out.println(nextString);
//            long length = nextString.length();
//
//            writer.writeLong((int) length);
//
//            writer.write(nextString.getBytes(), 0, nextString.length());
//            String[] commandAndFile = nextString.split("\\s+");
//            File file = new File(baseDirectory + commandAndFile[1]);
//            if (commandAndFile[0].equals("GETF")) {
//                if(file.isDirectory()){
//                    Directory dir = new Directory(file.getName());
//                    dir.populateDirectory(file.listFiles());
//                    getf(reader, dir);
//                } else {
//                    receiveFile(file, reader);
//                }
//            } else if (commandAndFile[0].equals("PUTF")) {
//                putf(file, writer);
////                sendFile(file, writer);
//            } else if (commandAndFile[0].equals("SYNC")) {
//                receiveDirectoryStructure();
//            } else if (commandAndFile[0].equals("QUIT")) {
//                System.exit(0);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void getf(DataInputStream reader, Directory dir) {
//        try {
//            Directory serverDir = receiveObject();
//            if (fileInput.isDirectory()) {
//                File[] files = fileInput.listFiles();
//                for (File file : files) {
//                    receiveFile(file, reader);
//                }
//            } else {
//                receiveFile(fileInput, reader);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private Directory receiveObject() {
//        Directory dirObj = null;
//        try {
//            dirObj = (Directory) readerObj.readObject();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return dirObj;
//
//    }
//
//    private void sendFile(File dir, DataOutputStream oos) throws Exception {
//        byte[] buff = new byte[client.getSendBufferSize()];
//        int bytesRead = 0;
//
//        InputStream fileReader = new FileInputStream(dir);
//        long length = dir.length();
//
//        oos.writeLong((int) length);
//
//        while ((bytesRead = fileReader.read(buff)) > 0) {
//            oos.write(buff, 0, bytesRead);
//        }
//        oos.flush();
//
//        fileReader.close();
//    }
//
//
//
//    private void putf(File fileInput, DataOutputStream reader) {
//        try {
//
//            if (fileInput.isDirectory()) {
//                File[] files = fileInput.listFiles();
//                for (File file : files) {
//                    sendFile(file, reader);
//                }
//            } else {
//                sendFile(fileInput, reader);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void receiveFile(File dir, DataInputStream ois) throws Exception {
//        FileOutputStream wr = new FileOutputStream(dir);
//        byte[] outBuffer = new byte[client.getReceiveBufferSize()];
//        int bytesReceived = 0;
//        long fileSize = ois.readLong();
//        while (fileSize > 0 && (bytesReceived = ois.read(outBuffer, 0, (int) Math.min(outBuffer.length, fileSize))) != -1) {
//            wr.write(outBuffer, 0, bytesReceived);
//            fileSize -= bytesReceived;
//        }
//        wr.flush();
//        wr.close();
//
//    }
//}
