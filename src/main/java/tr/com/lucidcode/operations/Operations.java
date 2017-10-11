package tr.com.lucidcode.operations;

import tr.com.lucidcode.helper.MapHelper;
import tr.com.lucidcode.pojo.DateValue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import tr.com.lucidcode.operations.Operator;


/**
 * Created by adinema on 09/10/17.
 */
public class Operations {

    public static void operate(String newRatio, Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap,
                        String ratio1, String ratio2, Operator operator){


                Map<String, Map<String, List<DateValue>>> typeScripDataMap = ratioTypeScripDataMap.get(ratio1);
                for(String type: typeScripDataMap.keySet()){
                    Map<String, List<DateValue>> scripDataMap = typeScripDataMap.get(type);

                    for(String scrip: scripDataMap.keySet()){
                        List<DateValue> dateValues1 = scripDataMap.get(scrip);
                        List<DateValue> dateValues2 = ratioTypeScripDataMap.get(ratio2).get(type).get(scrip);

                        for(DateValue dateValue1: dateValues1){
                            Date date1 = dateValue1.getDate();
                            DateValue dateValue2 = findInDateValues(dateValues2, date1);

                            if(dateValue2==null){
                                continue;
                            }

                            Float value = null;
                            switch (operator){
                                case ADD:
                                    value = dateValue1.getValue() + dateValue2.getValue();
                                case SUBS:
                                    value = dateValue1.getValue() - dateValue2.getValue();
                                case MUL:
                                    value = dateValue1.getValue() * dateValue2.getValue();
                                case DIV:
                                    value = dateValue1.getValue() / dateValue2.getValue();
                            }
                            if(value!=null){
                                MapHelper.addToMap(ratioTypeScripDataMap, newRatio, type, scrip, date1, value);
                            }

                        }
                }

        }

    }

    private static DateValue findInDateValues(List<DateValue> dateValues, Date date){
        for(DateValue dateValue: dateValues){
            if(dateValue.getDate().compareTo(date)==0){
                return dateValue;
            }
        }
        return null;
    }


}
