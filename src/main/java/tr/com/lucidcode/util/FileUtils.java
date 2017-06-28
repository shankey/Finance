package tr.com.lucidcode.util;

import java.io.File;

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
}
