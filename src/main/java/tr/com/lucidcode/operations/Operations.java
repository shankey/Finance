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

                if(typeScripDataMap==null){
                    return;
                }

                for(String type: typeScripDataMap.keySet()){
                    Map<String, List<DateValue>> scripDataMap = typeScripDataMap.get(type);

                    for(String scrip: scripDataMap.keySet()){
                        List<DateValue> dateValues1 = scripDataMap.get(scrip);

                        if(ratioTypeScripDataMap.get(ratio2)==null
                                || ratioTypeScripDataMap.get(ratio2).get(type)==null
                                || ratioTypeScripDataMap.get(ratio2).get(type).get(scrip)==null){

                            continue;
                        }

                        List<DateValue> dateValues2 = ratioTypeScripDataMap.get(ratio2).get(type).get(scrip);

                        for(DateValue dateValue1: dateValues1){
                            Date date1 = dateValue1.getDate();
                            DateValue dateValue2 = findInDateValues(dateValues2, date1);

                            if(dateValue2==null || dateValue1.getValue()==null || dateValue2.getValue()==null){
                                continue;
                            }

                            Float value = null;
                            switch (operator){
                                case ADD:
                                    value = dateValue1.getValue() + dateValue2.getValue();
                                    break;
                                case SUBS:
                                    value = dateValue1.getValue() - dateValue2.getValue();
                                    break;
                                case MUL:
                                    value = dateValue1.getValue() * dateValue2.getValue();
                                    break;
                                case DIV:
                                    value = dateValue1.getValue() / dateValue2.getValue();
                                    break;
                            }
                            if(value!=null){
                                MapHelper.addToMap(ratioTypeScripDataMap, newRatio, type, scrip, date1, value);
                            }

                        }
                }

        }

    }

    public static void operate(String newRatio, String newReportType, Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap,
                               String ratio1, String reportType1, String ratio2, String reportType2, Operator operator){


        Map<String, Map<String, List<DateValue>>> typeScripDataMap = ratioTypeScripDataMap.get(ratio1);
        if(typeScripDataMap==null){
            return;
        }

        Map<String, List<DateValue>> scripDataMap = typeScripDataMap.get(reportType1);
        if(scripDataMap==null){
            return;
        }

            for(String scrip: scripDataMap.keySet()){
                List<DateValue> dateValues1 = scripDataMap.get(scrip);

                if(ratioTypeScripDataMap.get(ratio2)==null
                        || ratioTypeScripDataMap.get(ratio2).get(reportType2)==null
                        || ratioTypeScripDataMap.get(ratio2).get(reportType2).get(scrip)==null){

                    continue;
                }

                List<DateValue> dateValues2 = ratioTypeScripDataMap.get(ratio2).get(reportType2).get(scrip);

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
                        MapHelper.addToMap(ratioTypeScripDataMap, newRatio, newReportType, scrip, date1, value);
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
