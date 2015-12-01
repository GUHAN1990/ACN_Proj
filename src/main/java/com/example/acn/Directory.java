package com.example.acn;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by priyadarshini on 11/20/15.
 */
public class Directory implements Serializable{
    private static final long serialVersionUID = 1L;
    List<FileDetails> fileDetailsList;
    List<Directory> directoryList;
    String directoryName;

    public Directory(String directoryName) {
        this.directoryName = directoryName;
        this.directoryList = new ArrayList<Directory>();
        this.fileDetailsList = new ArrayList<FileDetails>();
    }

    protected void addFile(FileDetails file){
        fileDetailsList.add(file);
    }


    protected void addDirectory(Directory directory){
        directoryList.add(directory);
    }

    public String getDirectoryName() {
        return directoryName;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "fileDetailsList=" + fileDetailsList.size() +
                ", directoryList=" + directoryList.size() +
                ", directoryName='" + directoryName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Directory directory = (Directory) o;

//        if (directoryList != null ? !directoryList.equals(directory.directoryList) : directory.directoryList != null)
//            return false;
        if (directoryName != null ? !directoryName.equals(directory.directoryName) : directory.directoryName != null)
            return false;
//        if (fileDetailsList != null ? !fileDetailsList.equals(directory.fileDetailsList) : directory.fileDetailsList != null)
//            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileDetailsList != null ? fileDetailsList.hashCode() : 0;
        result = 31 * result + (directoryList != null ? directoryList.hashCode() : 0);
        result = 31 * result + (directoryName != null ? directoryName.hashCode() : 0);
        return result;
    }



    public  void populateDirectory(File[] files) {
        for (File file : files) {
            long start = file.lastModified();
//            System.out.println(new Date(start));
            if (file.isDirectory()) {
                Directory directory = new Directory(file.getName());
                addDirectory(directory);
//                System.out.println("Directory: " + file.getName());
                directory.populateDirectory(file.listFiles()); // Calls same method again.
            } else {
                addFile(new FileDetails(file.getName(), new Date(start), file.length()));
//                System.out.println("File: " + file.getName());
            }
        }
    }

    public boolean containsDir(Directory dir) {
        return directoryList.contains(dir);
    }

    public boolean containsFile(FileDetails fileDetails) {
        return fileDetailsList.contains(fileDetails);
    }

    public FileDetails getByFileName(String fileName) {
        for(FileDetails fileDetails:fileDetailsList){
            if(fileDetails.fileName.equals(fileName))
            {
                return fileDetails;
            }
        }
        return null;
    }

    public Directory getByDirectoryName(String dirName) {
        for(Directory directory:directoryList){
            if(directory.directoryName.equals(dirName))
            {
                return directory;
            }
        }
        return null;
    }
}
