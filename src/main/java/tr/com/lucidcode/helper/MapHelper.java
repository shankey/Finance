package tr.com.lucidcode.helper;

import tr.com.lucidcode.pojo.DateValue;
import tr.com.lucidcode.util.Strings;

import java.util.*;

/**
 * Created by adinema on 11/10/17.
 */
public class MapHelper {

    public static void addToMap(Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap,
                                String ratio, String reportType, String scrip, Date date, Float value){

        if(ratioTypeScripDataMap.get(ratio)==null){
            ratioTypeScripDataMap.put(ratio, new HashMap<String, Map<String, List<DateValue>>>());
        }

        Map<String, Map<String, List<DateValue>>> priceTypeScripDataMap = ratioTypeScripDataMap.get(ratio);

        if(priceTypeScripDataMap.get(reportType)==null){
            priceTypeScripDataMap.put(reportType, new HashMap<String, List<DateValue>>());
        }

        Map<String, List<DateValue>> priceScripDataMap = priceTypeScripDataMap.get(reportType);
        if(priceScripDataMap.get(scrip)==null){
            priceScripDataMap.put(scrip, new ArrayList<DateValue>());
        }

        DateValue priceDateValue = new DateValue();
        priceDateValue.setDate(date);
        priceDateValue.setValue(value);
        priceScripDataMap.get(scrip).add(priceDateValue);


    }
}
