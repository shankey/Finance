package tr.com.lucidcode.scripts;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import tr.com.lucidcode.model.ReportDetails;
import tr.com.lucidcode.model.Reports;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLParse {

    Set<String> PnL = new HashSet<String>();

    public static String DATE_BEGIN = "Date Begin";
    public static String DATE_END = "Date End";
    public static String DESCRIPTION = "Description";
    public static String REPORT_TYPE = "ReportType";
    public static String REPORT_DETAIL_TYPE = "ReportDetailType";
    public static String BSE_ID = "BseId";
    public static String DATE = "Date";


    public static void main(String argv[]) {

        new XMLParse().getAllFiles();

    }

    public void getAllFiles(){
        File folder = new File("/Users/adinema/Documents/bse/");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());

                if(listOfFiles[i].getName().equals(".DS_Store")){
                    continue;
                }

                Map<String, Object> reportMap = new HashMap<String, Object>();
                detectReport(reportMap, listOfFiles[i].getName());
                List<Map<String, String>> listReportMaps = readDetailedReport(reportMap, listOfFiles[i].getName());
                System.out.println(listReportMaps);
                System.out.println(listReportMaps.size());
                ingestReport(reportMap, listReportMaps);

                System.out.println("---------------------------");
                //readReport(listOfFiles[i].getName());
            }
        }
        System.out.println(PnL);
        System.out.println(PnL.size());
    }

    public void ingestReport(Map<String, Object> reportMap, List<Map<String, String>> listReportMaps){
        Reports reports = new Reports();
        reports.setBseId((String)reportMap.get(BSE_ID));
        reports.setReportType((String)reportMap.get(REPORT_TYPE));


        for(Map<String, String> map: listReportMaps){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
            Date date = null;
            try {
                date = sdf.parse(map.get(DATE_BEGIN));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            reports.setReportDate(date);
            Reports dbReport = ServiceDispatcher.getReportsService().insertReport(reports);
            ingestReportDetails(dbReport.getId(), map);

        }

        //System.out.println(reportMap);
    }

    public void ingestReportDetails(Integer reportId, Map<String, String> map){

        for(String key: map.keySet()){
            ReportDetails reportDetails = new ReportDetails();
            reportDetails.setReportId(reportId);
            reportDetails.setReportKey(key);
            reportDetails.setReportValue(map.get(key));
            ServiceDispatcher.getReportDetailsService().insert(reportDetails);
        }
    }

    public void detectReport(Map<String, Object> reportMap, String fileName){

        System.out.println(fileName);

        String[] fileNameSplit = fileName.split("_");
        String bseId = fileNameSplit[0];
        String monthYear = fileNameSplit[1];
        String reportType = fileNameSplit[2];
        String reportDetailType = fileNameSplit[3];

        reportMap.put(BSE_ID, bseId);
        reportMap.put(DATE, (Object)getDateFromReport(monthYear));
        reportMap.put(REPORT_TYPE, reportType);
        reportMap.put(REPORT_DETAIL_TYPE, reportDetailType.split("\\.")[0]);

    }


    public Date getDateFromReport(String monthYear){
        String month = monthYear.substring(0, 2);
        String year = monthYear.substring(2,4);

        if(Integer.parseInt(year) > 50){
            year = "19" + year;
        }else{
            year = "20" + year;
        }

        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(year), Integer.parseInt(month), 0, 0, 0);

        return c.getTime();
    }

    public List<Map<String, String>> readDetailedReport(Map<String, Object> reportMap, String fileName){
        File input = new File("/Users/adinema/Documents/bse/"+fileName);
        Document doc = null;
        try {
            doc = Jsoup.parse(input, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<String, String>> listReportMaps = new ArrayList<Map<String, String>>();
        int initialize = 0;

        //Read File
        Elements rows = doc.select("tr");

        for (Element row : rows) {
            Elements columns = row.select("td");

            //initialize list for the no of clumns in reports
            if(initialize == 0){
                initialize = 1;
                for(int reporti=1; reporti<columns.size(); reporti++){
                    Map<String, String> map = new HashMap<String, String>();
                    listReportMaps.add(map);
                }
            }

            for(int i=1; i<columns.size(); i++){
                listReportMaps.get(i-1).put(columns.eq(0).text(), columns.eq(i).text());
            }
        }
        return listReportMaps;
    }

    public void readReport(String fileName){

    }
}