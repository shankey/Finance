package tr.com.lucidcode.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.dao.*;
import tr.com.lucidcode.model.*;
import tr.com.lucidcode.pojo.DateValue;
import tr.com.lucidcode.pojo.MoneyControlDataCSV;
import tr.com.lucidcode.pojo.MoneyControlDataOutput;
import tr.com.lucidcode.util.CsvFileWriter;
import tr.com.lucidcode.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("scripDataService")
public class ScripsDataService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");
    private static String csvOutputFile = "/Users/adinema/Documents/Finance/CSVOutput/";

    ReportsDAO reportsDAO = new ReportsDAO();
    ReportDetailsDAO reportDetailsDAO = new ReportDetailsDAO();
    MoneyControlScripsDAO moneyControlScripsDAO = new MoneyControlScripsDAO();
    ReportKeyMappingsDAO reportKeyMappingsDAO = new ReportKeyMappingsDAO();

    public Map<String, Map<String, Map<String, List<DateValue>>>> getDataForSector(String sector, List<String> listData){

        List<MoneyControlDataOutput> moneyControlDataOutputList = reportDetailsDAO.findByReportIdsAndDataMapping(sector, listData);

        //ratio ->report_type -> scrip -> date and value
        Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap = new HashMap<String, Map<String, Map<String, List<DateValue>>>>();

        for(MoneyControlDataOutput moneyControlDataOutput: moneyControlDataOutputList){

            if(!filterReportTypes(moneyControlDataOutput)){
                continue;
            }

            if(!filterByDate(moneyControlDataOutput)){
                continue;
            }

            if(!ratioTypeScripDataMap.containsKey(moneyControlDataOutput.getKey())){

                ratioTypeScripDataMap.put(moneyControlDataOutput.getKey(), new HashMap<String, Map<String, List<DateValue>>>());
            }

            Map<String, Map<String, List<DateValue>>> typeScripDataMap = ratioTypeScripDataMap.get(moneyControlDataOutput.getKey());

            if(!typeScripDataMap.containsKey(moneyControlDataOutput.getReportType())){
                typeScripDataMap.put(moneyControlDataOutput.getReportType(), new HashMap<String, List<DateValue>>());
            }

            Map<String, List<DateValue>> scripDataMap = typeScripDataMap.get(moneyControlDataOutput.getReportType());

            if(!scripDataMap.containsKey(moneyControlDataOutput.getScrip())){
                scripDataMap.put(moneyControlDataOutput.getScrip(), new ArrayList<DateValue>());
            }

            List<DateValue> dataList = scripDataMap.get(moneyControlDataOutput.getScrip());


            dataList.add(new DateValue(moneyControlDataOutput.getDate(), moneyControlDataOutput.getValue()));

        }

        writeCSV(sector, ratioTypeScripDataMap);

        return ratioTypeScripDataMap;


    }

    private Boolean filterByDate(MoneyControlDataOutput mcdo){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date cutOffDate = null;
        try {
            cutOffDate = sdf.parse("2010-03-31");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(mcdo.getDate().compareTo(cutOffDate)>0){
            return true;
        }else{
            return false;
        }
    }

    private Boolean filterReportTypes(MoneyControlDataOutput mcdo){
        String reportType = mcdo.getReportType();
        String ratio = mcdo.getKey();

        if(ratio.equals("ROCE") && (reportType.equals("cons_keyfinratio")) || reportType.equals("keyfinratio")){
            return true;
        }

        if(ratio.equals("DILUTED EPS") && (reportType.equals("cons_yearly")) || reportType.equals("yearly")){
            return true;
        }


        return false;
    }

    private Set<Date> getCompliantDateSet1() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Set<Date> compliantDateSet = new TreeSet<Date>();

        try {
            Date date17 = sdf.parse("2017-03-31");
            Date date16 = sdf.parse("2016-03-31");
            Date date15 = sdf.parse("2015-03-31");
            Date date14 = sdf.parse("2014-03-31");
            Date date13 = sdf.parse("2013-03-31");
            Date date12 = sdf.parse("2012-03-31");
            Date date11 = sdf.parse("2011-03-31");

            compliantDateSet.add(date17);
            compliantDateSet.add(date16);
            compliantDateSet.add(date15);
            compliantDateSet.add(date14);
            compliantDateSet.add(date13);
            compliantDateSet.add(date12);
            compliantDateSet.add(date11);

        }catch (Exception e){
            e.printStackTrace();
        }


        return compliantDateSet;
    }

    private Set<Date> getCompliantDateSet2(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Set<Date> compliantDateSet = new TreeSet<Date>();
        try {
            Date date17 = sdf.parse("2017-12-31");
            Date date16 = sdf.parse("2016-12-31");
            Date date15 = sdf.parse("2015-12-31");
            Date date14 = sdf.parse("2014-12-31");
            Date date13 = sdf.parse("2013-12-31");
            Date date12 = sdf.parse("2012-12-31");
            Date date11 = sdf.parse("2011-12-31");

            compliantDateSet.add(date17);
            compliantDateSet.add(date16);
            compliantDateSet.add(date15);
            compliantDateSet.add(date14);
            compliantDateSet.add(date13);
            compliantDateSet.add(date12);
            compliantDateSet.add(date11);

        }catch (Exception e){
            e.printStackTrace();
        }

        return compliantDateSet;
    }

    private Boolean checkCompliance(Set<Date> compliantDateSet, List<DateValue> dataList){
        Boolean isAnnualCompliant = true;


        for(DateValue dateValue: dataList){
            if(!compliantDateSet.contains(dateValue.getDate())){
                isAnnualCompliant = false;
                break;
            }
        }

        return isAnnualCompliant;

    }

    private void writeCSV(String sector, Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap){
        List<List> csvList = new ArrayList<List>();
        for(String ratio: ratioTypeScripDataMap.keySet()){
            Map<String, Map<String, List<DateValue>>> typeScripDataMap = ratioTypeScripDataMap.get(ratio);

            for(String type: typeScripDataMap.keySet()) {
                Map<String, List<DateValue>> scripData = typeScripDataMap.get(type);

                for (String scrip : scripData.keySet()) {
                    List<DateValue> dataList = scripData.get(scrip);

                    Map<Date, DateValue> dateMap = new HashMap<Date, DateValue>();
                    for (DateValue dateValue : dataList) {
                        dateMap.put(dateValue.getDate(), dateValue);
                    }

                    Collections.sort(dataList);
                    System.out.println("-- " + dataList);
                    MoneyControlDataCSV mcds = new MoneyControlDataCSV();
                    mcds.setRatio(ratio);
                    mcds.setScrip(scrip);

                    Boolean isAnnualCompliant1 = checkCompliance(getCompliantDateSet1(), dataList);
                    Boolean isAnnualCompliant2 = checkCompliance(getCompliantDateSet2(), dataList);

                    Set<Date> compliantDateSet1 = getCompliantDateSet1();
                    Set<Date> compliantDateSet2 = getCompliantDateSet2();

                    if (isAnnualCompliant1) {
                        List li = new ArrayList();
                        li.add(ratio);
                        li.add(type);
                        li.add(scrip);

                        for (Date date : compliantDateSet1) {

                            if (dateMap.containsKey(date)) {
                                li.add(dateMap.get(date).getValue());
                            } else {
                                li.add("");
                            }
                        }
                        csvList.add(li);
                    } else {
                        if (isAnnualCompliant2) {
                            List li = new ArrayList();
                            li.add(ratio);
                            li.add(type);
                            li.add(scrip);

                            for (Date date : compliantDateSet2) {

                                if (dateMap.containsKey(date)) {
                                    li.add(dateMap.get(date).getValue());
                                } else {
                                    li.add("");
                                }
                            }
                            csvList.add(li);
                        } else {

                            List li1 = new ArrayList();
                            li1.add(ratio);
                            li1.add(type);
                            li1.add(scrip);

                            List li2 = new ArrayList();
                            li2.add(ratio);
                            li2.add(type);
                            li2.add(scrip);


                            int init = 0;
                            for (DateValue dateValue : dataList) {
                                if (init == 0) {
                                    init = 1;
                                    for (Date date : compliantDateSet1) {
                                        if (Utils.getDays(date, dateValue.getDate()) < 185) {
                                            break;
                                        } else {
                                            li1.add("");
                                            li2.add("");
                                        }
                                    }
                                }

                                li1.add(dateValue.getDate());
                                li2.add(dateValue.getValue());
                            }
                            csvList.add(li1);
                            csvList.add(li2);
                        }
                    }
                }
            }
        }

        CsvFileWriter.writeCsvFile(csvOutputFile + sector + ".csv", getCSVHeader(), csvList);
    }




    private Object[] getCSVHeader(){
        Object[] header = new Object[]{"Ratio", "Scrip", "Type",2011, 2012, 2013,2014,2015,2016,2017};
        return header;
    }
}
