package tr.com.lucidcode.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created by adinema on 15/06/17.
 */
public class Utils {

    public static  Long getDays(Date date1, Date date2){
        Long diff = date1.getTime() - date2.getTime();
        Long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffDays;
    }

    public static  Date get3MonthsClosestForward(Date date, Set<Date> dateSet){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 90);

        Date dt=c.getTime();
        for(int i=0; i<=90; i++) {

            if(dateSet.contains(dt)){
                return dt;
            }

            c.add(Calendar.DATE, 1);  // number of days to add
            dt = c.getTime();  // dt is now the new date
        }
        return null;
    }
}
