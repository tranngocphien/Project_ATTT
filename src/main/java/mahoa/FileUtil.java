package mahoa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static void writeToFile(File file, byte[] fileContent) {
        System.out.println("Writing bytes to file...");
        //Path path = Paths.get(file.getAbsolutePath());
        System.out.println(file);
        try {
            //Files.write(path, fileContent);
//            File file1 = new File(file);
//            file1.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(fileContent);
            outputStream.close();

            System.out.println("write done!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Done writing bytes to file...");
    }

    public static byte[] readBytesFromFile(File file) {
        System.out.println("Reading bytes from file...");
        Path path = Paths.get(file.getAbsolutePath());
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Done reading from file...");
        return data;
    }

    public static byte[] combineBytes(byte[] a, byte[] b){
        byte[] combined = new byte[a.length + b.length];
        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);

        return combined;
    }
}
