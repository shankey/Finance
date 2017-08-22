package tr.com.lucidcode.service;

import org.apache.log4j.Logger;
import org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.dao.*;
import tr.com.lucidcode.model.*;
import tr.com.lucidcode.pojo.DateValue;
import tr.com.lucidcode.pojo.MoneyControlDataCSV;
import tr.com.lucidcode.pojo.MoneyControlDataOutput;
import tr.com.lucidcode.util.CsvFileWriter;
import tr.com.lucidcode.util.Strings;
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
    StockPriceDAO stockPriceDAO = new StockPriceDAO();

    public List<MoneyControlDataOutput> getDataForScrip(String name, List<String> listData){

        Long t1 = System.currentTimeMillis();
        List<MoneyControlDataOutput> moneyControlDataOutputList = reportDetailsDAO.findByReportIdsAndDataMappingByScrip(name, listData);
        Long t2 = System.currentTimeMillis();
        System.out.println("Full Data Function = " + (t2-t1));

        List<MoneyControlDataOutput> filterMoneyControlDataOutputList = new ArrayList<MoneyControlDataOutput>();

        // Stock -> Metrics -> Data - Value
        Map<String, Map<String, List<Map<String, String>>>> scripRatioDataMap = new HashMap<String, Map<String, List<Map<String, String>>>>();

        for(MoneyControlDataOutput moneyControlDataOutput: moneyControlDataOutputList){

            if(!filterReportTypes(moneyControlDataOutput)){
                continue;
            }

            if(!filterByDate(moneyControlDataOutput)){
                continue;
            }

            filterMoneyControlDataOutputList.add(moneyControlDataOutput);
        }

        calculatePEForScrip(name, filterMoneyControlDataOutputList);

        return filterMoneyControlDataOutputList;

    }

    public List<List> getDataForSector(String sector, List<String> listData){

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

        calculatePE(sector, ratioTypeScripDataMap);

        return writeCSV(sector, ratioTypeScripDataMap);


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

    private List<List> writeCSV(String sector, Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap){
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
                                li.add(null);
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
                                    li.add(null);
                                }
                            }
                            csvList.add(li);
                        } else {

                            List li3 = new ArrayList(15);
                            li3.add(ratio);
                            li3.add(type);
                            li3.add(scrip);

                            Float[] arr3 = new Float[10];

                            for (DateValue dateValue : dataList) {
                                Integer index = getClosestIndex(dateValue.getDate(), compliantDateSet1);
                                System.out.println(index);
                                arr3[index] = dateValue.getValue();
                            }

                            for(Float fl: arr3){
                                if(fl==null){
                                    li3.add(null);
                                }else{
                                    li3.add(fl);
                                }

                            }

                            csvList.add(li3);
                        }
                    }
                }
            }
        }

        try {
            CsvFileWriter.writeCsvFile(csvOutputFile + sector + ".csv", getCSVHeader(), csvList);
        }catch (NullPointerException npe){
            logger.error("CSV file not found : ", npe);
        }
        return csvList;
    }

    public Integer getClosestIndex(Date inputDate, Set<Date> compliantDateSet){
        List<Date> compliantDateList = new ArrayList();
        compliantDateList.addAll(compliantDateSet);
        Collections.sort(compliantDateList);

        Long closest = 5000l;
        Integer closestIndex = -1;
        Integer index = 0;
        for(Date date: compliantDateList){
            Long diff = Math.abs(Utils.getDays(inputDate, date));

            if(closest > diff){
                closest = diff;
                closestIndex = index;
            }

            index++;
        }

        return closestIndex;
    }

    private void calculatePEForScrip(String name, List<MoneyControlDataOutput> mcdoList){

        Map<Date, StockPrice> datePriceMap = getPricesForScrip(name);
        List<MoneyControlDataOutput> pricesMcdoList = new ArrayList<MoneyControlDataOutput>();
        List<MoneyControlDataOutput> peMcdoList = new ArrayList<MoneyControlDataOutput>();

        for(MoneyControlDataOutput mcdo : mcdoList){

            if(mcdo.getKey().equals(Strings.DILUTED_EPS)){

                if(mcdo.getValue()==null || mcdo.getDate()==null){
                    continue;
                }

                Date dt = Utils.get3MonthsClosestForward(mcdo.getDate(), datePriceMap.keySet());

                if(dt==null){
                    System.out.println("Null forward date for " + mcdo.getDate());
                    continue;
                }

                StockPrice peStockPrice = datePriceMap.get(dt);

                MoneyControlDataOutput pricemcdo = new MoneyControlDataOutput();
                pricemcdo.setDate(dt);
                pricemcdo.setValue(peStockPrice.getClose());
                pricemcdo.setScrip(name);
                pricemcdo.setKey("Price");
                pricemcdo.setReportType(mcdo.getReportType());
                pricesMcdoList.add(pricemcdo);

                MoneyControlDataOutput pemcdo = new MoneyControlDataOutput();
                pemcdo.setDate(dt);
                pemcdo.setValue(mcdo.getValue()/peStockPrice.getClose());
                pemcdo.setScrip(name);
                pemcdo.setKey("PE");
                pemcdo.setReportType(mcdo.getReportType());
                peMcdoList.add(pemcdo);
            }
        }
        mcdoList.addAll(pricesMcdoList);
        mcdoList.addAll(peMcdoList);
    }

    private void calculatePE(String sector, Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap){
        Map<String, Map<String, List<DateValue>>> typeScripDataMap = ratioTypeScripDataMap.get(Strings.DILUTED_EPS);

        if(typeScripDataMap==null || typeScripDataMap.size()==0){
            return;
        }

        Map<String, Map<Date, StockPrice>> bseIdPriceMap = getPricesForIndustry(sector);
        for(String reportType : typeScripDataMap.keySet()){

            Map<String, List<DateValue>> scripDataMap = typeScripDataMap.get(reportType);

            for(String scrip : scripDataMap.keySet()){
                List<DateValue> priceList = new ArrayList<DateValue>();
                List<DateValue> pbyeList = new ArrayList<DateValue>();

                Map<Date, StockPrice> spList = bseIdPriceMap.get(scrip);
                if(spList==null){
                    System.out.println("No prices for "+scrip);
                    continue;
                }
                Set<Date> dateSet = spList.keySet();

                List<DateValue> dataList = scripDataMap.get(scrip);

                for(DateValue dateValue: dataList){
                    Date dt = Utils.get3MonthsClosestForward(dateValue.getDate(), dateSet);
                    if(dt==null){
                        continue;
                    }
                    StockPrice peStockPrice = spList.get(dt);

                    DateValue dv= new DateValue();
                    dv.setDate(dateValue.getDate());
                    dv.setValue(peStockPrice.getClose());

                    priceList.add(dv);


                    if(peStockPrice.getClose()!=null && dateValue.getValue()!=null){
                        DateValue peDv= new DateValue();
                        peDv.setDate(dateValue.getDate());
                        peDv.setValue(peStockPrice.getClose()/dateValue.getValue());
                        pbyeList.add(peDv);
                    }

                }

                insertIntoMap(pbyeList, scrip, reportType, "PE", ratioTypeScripDataMap);
                insertIntoMap(priceList, scrip, reportType, "prices", ratioTypeScripDataMap);

            }
        }
    }

    private void insertIntoMap(List<DateValue> dateList, String scrip, String reportType, String ratio, Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap){

        if(!ratioTypeScripDataMap.containsKey(ratio)){
            Map<String, Map<String, List<DateValue>>> peTypeScripDataMap = new HashMap<String, Map<String, List<DateValue>>>();
            ratioTypeScripDataMap.put(ratio, peTypeScripDataMap);
        }

        Map<String, Map<String, List<DateValue>>> peTypeScripDataMap = ratioTypeScripDataMap.get(ratio);

        if(!peTypeScripDataMap.containsKey(reportType)){
            Map<String, List<DateValue>> peScripDataMap = new HashMap<String, List<DateValue>>();
            peTypeScripDataMap.put(reportType, peScripDataMap);
        }

        Map<String, List<DateValue>> peScripDataMap = peTypeScripDataMap.get(reportType);

        if(!peScripDataMap.containsKey(scrip)){
            List<DateValue> peDataMap = new ArrayList<DateValue>();
            peScripDataMap.put(scrip, peDataMap);
        }

        peScripDataMap.get(scrip).addAll(dateList);

    }

    private Map<String, Map<Date, StockPrice>> getPricesForIndustry(String sector){
        System.out.println("INSIDE getPrices");
        List<MoneyControlScrips> mcsList = moneyControlScripsDAO.getByIndustry(sector);
        System.out.println("mcsList = "+mcsList);
        Map<Integer, String> bseIdsScripMap = new HashMap<Integer, String>();
        for(MoneyControlScrips mcs: mcsList){
            bseIdsScripMap.put(mcs.getBseId(), mcs.getName());
        }
        List<StockPrice> spsList = stockPriceDAO.findByBseIds(new ArrayList<Integer>(bseIdsScripMap.keySet()));
        System.out.println("spsList =" + spsList);

        Map<String, Map<Date, StockPrice>> scripPriceMap = new HashMap<String, Map<Date, StockPrice>>();

        for(StockPrice sp: spsList){
            String scrip = bseIdsScripMap.get(sp.getBseId());
            if(!scripPriceMap.containsKey(scrip)){
                scripPriceMap.put(scrip, new HashMap<Date, StockPrice>());
            }
            scripPriceMap.get(scrip).put(sp.getDate(), sp);
        }

        return scripPriceMap;
    }

    private Map<Date, StockPrice> getPricesForScrip(String name){
        System.out.println("INSIDE getPricesForScrip");

        Long t1 = System.currentTimeMillis();
        MoneyControlScrips mcs = moneyControlScripsDAO.getByName(name);
        Long t2 = System.currentTimeMillis();
        System.out.println("MCS Get By Name " + (t2-t1));

        System.out.println("mcs = "+mcs);

        if(mcs==null || mcs.getBseId()==null){
            return null;
        }

        List bseIds = new ArrayList<String>();
        bseIds.add(mcs.getBseId());
        Long t3 = System.currentTimeMillis();
        List<StockPrice> spsList = stockPriceDAO.findByBseIds(bseIds);
        Long t4 = System.currentTimeMillis();
        System.out.println("Prices for Name " + (t4-t3));
        System.out.println("spsList =" + spsList);

        Map<Date, StockPrice> datePriceMap = new HashMap<Date, StockPrice>();

        for(StockPrice sp: spsList){
            datePriceMap.put(sp.getDate(), sp);
        }

        return datePriceMap;
    }


    private Object[] getCSVHeader(){
        Object[] header = new Object[]{"Ratio", "Scrip", "Type",2011, 2012, 2013,2014,2015,2016,2017};
        return header;
    }
}
