package tr.com.lucidcode.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by adinema on 15/06/17.
 */
public class FileUtils {

    public static void moveFile(String source, String destination){
        try{

            File file =new File(source);

            if(file.renameTo(new File(destination + "/" +file.getName()))){
                System.out.println("File is moved successful! " + file.getName());
            }else{
                System.out.println("File is failed to move! " + file.getName());
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void createFolder(String folder){
        File theDir = new File(folder);

        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                System.out.println("DIR created "+ folder);
            }
        }
    }

    public static void createFile(String fileName, String content){
        createParentDirectoryIfNot(fileName);

        try{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void createParentDirectoryIfNot(String fileName){
        final File file = new File(fileName);
        final File parent_directory = file.getParentFile();

        if (null != parent_directory)
        {
            parent_directory.mkdirs();
        }

    }
}
