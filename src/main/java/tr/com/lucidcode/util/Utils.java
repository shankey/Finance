package tr.com.lucidcode.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by adinema on 15/06/17.
 */
public class Utils {

    public static  Long getDays(Date date1, Date date2){
        Long diff = date1.getTime() - date2.getTime();
        Long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffDays;
    }
}
