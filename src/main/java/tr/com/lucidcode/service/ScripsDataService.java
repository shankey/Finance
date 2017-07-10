package tr.com.lucidcode.service;

import org.apache.log4j.Logger;
import org.apache.xpath.operations.Bool;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.dao.*;
import tr.com.lucidcode.model.*;
import tr.com.lucidcode.pojo.DateValue;
import tr.com.lucidcode.pojo.MoneyControlDataCSV;
import tr.com.lucidcode.pojo.MoneyControlDataOutput;
import tr.com.lucidcode.pojo.ReportVariableOutput;
import tr.com.lucidcode.util.CsvFileWriter;
import tr.com.lucidcode.util.Strings;

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
    private static String csvOutputFile = "/Users/adinema/Documents/Finance/CSVOutput/output";

    ReportsDAO reportsDAO = new ReportsDAO();
    ReportDetailsDAO reportDetailsDAO = new ReportDetailsDAO();
    MoneyControlScripsDAO moneyControlScripsDAO = new MoneyControlScripsDAO();
    ReportKeyMappingsDAO reportKeyMappingsDAO = new ReportKeyMappingsDAO();

    public Map<String, Map<String, List<DateValue>>> getDataForSector(String sector, List<String> listData){

        List<MoneyControlDataOutput> moneyControlDataOutputList = reportDetailsDAO.findByReportIdsAndDataMapping(sector, listData);

        //ratio -> scrip -> date and value
        Map<String, Map<String, List<DateValue>>> ratioScripDataMap = new HashMap<String, Map<String, List<DateValue>>>();

        for(MoneyControlDataOutput moneyControlDataOutput: moneyControlDataOutputList){

            if(!filterReportTypes(moneyControlDataOutput)){
                continue;
            }

            if(!filterByDate(moneyControlDataOutput)){
                continue;
            }

            if(!ratioScripDataMap.containsKey(moneyControlDataOutput.getKey())){


                ratioScripDataMap.put(moneyControlDataOutput.getKey(), new HashMap<String, List<DateValue>>());
            }

            Map<String, List<DateValue>> scripDataMap = ratioScripDataMap.get(moneyControlDataOutput.getKey());

            if(!scripDataMap.containsKey(moneyControlDataOutput.getScrip())){
                scripDataMap.put(moneyControlDataOutput.getScrip(), new ArrayList<DateValue>());
            }

            List<DateValue> dataList = scripDataMap.get(moneyControlDataOutput.getScrip());


            dataList.add(new DateValue(moneyControlDataOutput.getDate(), moneyControlDataOutput.getValue()));

        }

        writeCSV(ratioScripDataMap);

        return ratioScripDataMap;


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

        if(ratio.equals("ROCE") && reportType.equals("cons_keyfinratio")){
            return true;
        }

        if(ratio.equals("DILUTED EPS") && reportType.equals("cons_yearly")){
            return true;
        }

        return false;
    }

    private Set<Date> getCompliantDateSet() throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Set<Date> compliantDateSet = new TreeSet<Date>();
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


        return compliantDateSet;
    }

    private void writeCSV(Map<String, Map<String, List<DateValue>>> ratioScripDataMap){
        List<List> csvList = new ArrayList<List>();
        for(String ratio: ratioScripDataMap.keySet()){
            Map<String, List<DateValue>> scripData = ratioScripDataMap.get(ratio);

            for(String scrip: scripData.keySet()){
                List<DateValue> dataList = scripData.get(scrip);

                Collections.sort(dataList);
                System.out.println("-- " + dataList);
                MoneyControlDataCSV mcds = new MoneyControlDataCSV();
                mcds.setRatio(ratio);
                mcds.setScrip(scrip);

                Boolean isAnnualCompliant = true;
                Set<Date> compliantDateSet = null;
                try {
                    compliantDateSet = getCompliantDateSet();
                }catch (Exception e){
                    e.printStackTrace();
                }

                for(DateValue dateValue: dataList){
                    if(!compliantDateSet.contains(dateValue.getDate())){
                        isAnnualCompliant = false;
                        break;
                    }
                }

                Map<Date, DateValue> dateMap = new HashMap<Date, DateValue>();
                for(DateValue dateValue: dataList){
                    dateMap.put(dateValue.getDate(), dateValue);
                }



                if(isAnnualCompliant){
                    List li = new ArrayList();
                    li.add(ratio);
                    li.add(scrip);
                    for(Date date: compliantDateSet){
                        System.out.println(date);
                        if(dateMap.containsKey(date)){
                            li.add(dateMap.get(date).getValue());
                        }else{
                            li.add("");
                        }
                    }
                    csvList.add(li);
                }else{
                    List li1 = new ArrayList();
                    li1.add(ratio);
                    li1.add(scrip);
                    List li2 = new ArrayList();
                    li2.add(ratio);
                    li2.add(scrip);
                    for(DateValue dateValue: dataList){
                        li1.add(dateValue.getDate());
                        li2.add(dateValue.getValue());
                    }
                    csvList.add(li1);
                    csvList.add(li2);
                }

            }
        }

        CsvFileWriter.writeCsvFile(csvOutputFile, getCSVHeader(), csvList);
    }

    private Object[] getCSVHeader(){
        Object[] header = new Object[]{"Ratio", "Scrip", 2011,2012, 2013,2014,2015,2016,2017};
        return header;
    }
}
