package tr.com.lucidcode.scripts;

import tr.com.lucidcode.dao.StockPriceDAO;
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.pojo.MoneyControlDataOutput;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by adinema on 18/06/17.
 */
public class Main {

    public static void main(String args[]) throws ParseException {



        long t1 = System.currentTimeMillis();
        List<String> list = new ArrayList<String>();
        list.add("ROCE");
        list.add("DILUTED EPS");
        List<MoneyControlDataOutput> mcdoList = ServiceDispatcher.getScripsDataService().getDataForScrip("ambujacements", list);
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);

        System.out.println(mcdoList);

//        List<String> list = new ArrayList<String>();
//        list.add("ROCE");
//        list.add("DILUTED EPS");
//
//        ServiceDispatcher.getScripsDataService().getDataForSector("mediaentertainment", list);


//        System.out.println(ss);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        Date date1 = sdf.parse("2017-03-31");
//        Date date2 = sdf.parse("2016-03-31");
//        Date date3 = sdf.parse("2015-03-31");
//
//        System.out.println(getDays(date1, date2));
//        System.out.println(getDays(date2, date3));
//        System.out.println(getDays(date3, date1));


    }

}
