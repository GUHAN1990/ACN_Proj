//import java.io.Serializable;
//import java.util.Date;
//
///**
// * Created by priyadarshini on 11/20/15.
// */
//public class FileDetails implements Serializable{
//    String fileName;
//    Date dateModified;
//    long fileSize;
//
//    FileDetails(String fileName, Date dateModified, long fileSize) {
//        this.dateModified = dateModified;
//        this.fileName = fileName;
//        this.fileSize = fileSize;
//    }
//
//    @Override
//    public String toString() {
//        return "FileDetails{" +
//                "fileName='" + fileName + '\'' +
//                ", dateModified=" + dateModified +
//                ", fileSize=" + fileSize +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        FileDetails that = (FileDetails) o;
//
////        if (fileSize != that.fileSize) return false;
////        if (dateModified != null ? !dateModified.equals(that.dateModified) : that.dateModified != null) return false;
//        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = fileName != null ? fileName.hashCode() : 0;
//        result = 31 * result + (dateModified != null ? dateModified.hashCode() : 0);
//        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
//        return result;
//    }
//
//    public boolean isLatestFile(FileDetails fileDetails){
//        return this.dateModified.after(fileDetails.dateModified);
//    }
//
//
//    public boolean isSame(FileDetails secondFile) {
//        return this.dateModified.equals(secondFile.dateModified);
//    }
//}
