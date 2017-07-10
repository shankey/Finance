package tr.com.lucidcode.scripts;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tr.com.lucidcode.model.KeyValue;
import tr.com.lucidcode.model.ReportDetails;
import tr.com.lucidcode.model.Reports;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MoneyControlDataIngest {

    Set<String> reports = new HashSet<String>();

    public static String DATE_BEGIN = "Date Begin";
    public static String DATE_END = "Date End";
    public static String DESCRIPTION = "Description";
    public static String REPORT_TYPE = "ReportType";
    public static String REPORT_SOURCE = "Source";
    public static String REPORT_DETAIL_TYPE = "ReportDetailType";
    public static String BSE_ID = "BseId";
    public static String DATE = "Date";
    public static String MONEY_CONTROL_ID = "MoneyControlId";
    public static Integer MONEY_CONTROL_SOURCE = 1;

    public String baseFolder = "/Users/adinema/Documents/MoneyControl/";


    public static void main(String argv[]) {

        new MoneyControlDataIngest().getAllFolders();

    }

    public void getAllFolders(){
        File folder = new File(baseFolder);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());

                if(listOfFiles[i].getName().equals(".DS_Store")){
                    continue;
                }

                getAllFiles(listOfFiles[i].getName());

                System.out.println("---------------------------");
            }
        }

    }

    public void getAllFiles(String moneyControlId){
        String path = baseFolder + moneyControlId;

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {


                if(listOfFiles[i].getName().equals(".DS_Store")){
                    continue;
                }

//                if(!listOfFiles[i].getName().contains("keyfin")){
//                    continue;
//                }
                System.out.println("File " + listOfFiles[i].getName());

                Map<String, KeyValue> reportMap = new HashMap<String, KeyValue>();
                reportMap.put(MONEY_CONTROL_ID, new KeyValue(MONEY_CONTROL_ID, moneyControlId, -1));
                detectReport(reportMap, listOfFiles[i].getName());

                List<Map<String, KeyValue>> listReportMaps = readDetailedReport(listOfFiles[i].getPath());
                for(Map<String, KeyValue> map: listReportMaps){
                    map.putAll(reportMap);
                    extractDateFromValues(map);
                }

                ingestReport(listReportMaps);
            }
        }

    }

    public Date extractDateFromValues(Map<String, KeyValue> map){

        if(!map.containsKey("\u00a0")){
            return null;
        }

        String dateString = (String)map.get("\u00a0").getValue();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM yy");
        Date date = null;
        try {
            date = sdf.parse(dateString.replaceAll("'", ""));


            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

            date = c.getTime();
            System.out.println(date);

            map.put(DATE, new KeyValue(DATE, date, map.get("\u00a0").getOrder()));


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public void ingestReport(List<Map<String, KeyValue>> listReportMaps){


        for(Map<String, KeyValue> map: listReportMaps){

            if(!map.containsKey(DATE)){
                continue;
            }

            Reports dbReport = ingestReportTable(map);
            ingestReportDetails(dbReport, map);

        }

        //System.out.println(reportMap);
    }

    public Reports ingestReportTable(Map<String, KeyValue> map){
        Reports reports = new Reports();
        reports.setReportType((String)map.get(REPORT_TYPE).getValue());
        reports.setReportDate((Date) map.get(DATE).getValue());
        reports.setMoneyControlId((String)map.get(MONEY_CONTROL_ID).getValue());
        reports.setSource((Integer)map.get(REPORT_SOURCE).getValue());

        return ServiceDispatcher.getReportsService().insertReport(reports);
    }


    public void ingestReportDetails(Reports dbReport, Map<String, KeyValue> map){
        List<ReportDetails> list = new ArrayList<ReportDetails>();
        for(String key: map.keySet()){
            ReportDetails reportDetails = new ReportDetails();
            reportDetails.setReportId(dbReport.getId());
            reportDetails.setReportKey(key);
            reportDetails.setReportValue(map.get(key).getValue().toString());
            reportDetails.setReportOrder(map.get(key).getOrder());
            list.add(reportDetails);
        }
        ServiceDispatcher.getReportDetailsService().insert(list);
    }

    public void detectReport(Map<String, KeyValue> reportMap, String fileName){

        System.out.println(fileName);

        //detect script and report type from folder and name

        String[] fileNameSplit = fileName.split("\\.");
        String reportType = fileNameSplit[0];
        System.out.println(reportType);
        reportMap.put(REPORT_TYPE, new KeyValue(REPORT_TYPE, reportType, -1));
        reportMap.put(REPORT_SOURCE, new KeyValue(REPORT_SOURCE, MONEY_CONTROL_SOURCE, -1));

    }



    public List<Map<String, KeyValue>> readDetailedReport( String path){
        File input = new File(path);
        Document doc = null;
        try {
            doc = Jsoup.parse(input, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Read File
        Elements tables = doc.select("table");

        Element table = tables.get(3);

        Elements rows = table.select("tr[height=\"22px\"]");


        List<Map<String, KeyValue>> listReportMaps = new ArrayList<Map<String, KeyValue>>();
        int init = 0;
        for(Element row: rows){
            Elements columns = row.select("td");

            if(init == 0){

                int length = columns.size();

                for(int i=0; i<5; i++){
                    listReportMaps.add(new HashMap<String, KeyValue>());
                }
            }

            String key=null;

            for(int i=0; i<columns.size(); i++){

                if(i==0){
                    key = columns.get(i).text();
                    continue;
                }

                if(!listReportMaps.get(i-1).containsKey(key)){
//                    System.out.println(key + " " + columns.get(i).text());
                    listReportMaps.get(i-1).put(key, new KeyValue(key, columns.get(i).text(), init));
                }


            }
            init++;

        }
        return listReportMaps;
    }



    public void readReport(String fileName){

    }
}