package tr.com.lucidcode.service;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import tr.com.lucidcode.dao.ReportDetailsDAO;
import tr.com.lucidcode.dao.StockSymbolsDAO;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.pojo.ReportVariableOutput;
import tr.com.lucidcode.util.Strings;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("ratioCalculatorService")
public class RatioCalculatorService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    ReportDetailsDAO reportDetailsDAO = new ReportDetailsDAO();
    StockSymbolsDAO stockSymbolsDAO = new StockSymbolsDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */

    public List<ReportVariableOutput> getProfitDetails(){
        List<StockSymbols> stockSymbolsList = stockSymbolsDAO.findByIndustry("Cement &amp; Cement Products");
        List<ReportVariableOutput> list = reportDetailsDAO.getReportVariable(getScripsFromStocksSymbols(stockSymbolsList), getReportVariableList(new String[]{"PAT", "NET PROFIT"}));
        List<ReportVariableOutput> outputList = new ArrayList<ReportVariableOutput>();
        Map<Integer, Map<String, ReportVariableOutput>> map = new HashMap<Integer, Map<String, ReportVariableOutput>>();

        for(ReportVariableOutput reportVariableOutput : list){
            if(!map.containsKey(reportVariableOutput.getReportId())){
                map.put(reportVariableOutput.getReportId(), new HashMap<String, ReportVariableOutput>());
            }

            map.get(reportVariableOutput.getReportId()).put(reportVariableOutput.getReportKeyMapping(), reportVariableOutput);
        }

        for(Integer reportId: map.keySet()){
            if(!map.get(reportId).containsKey(Strings.NET_PROFIT)){
                if(map.get(reportId).containsKey(Strings.PAT)){
                    map.get(reportId).put(Strings.NET_PROFIT, map.get(reportId).get(Strings.PAT));
                    map.get(reportId).get(Strings.NET_PROFIT).setReportKeyMapping(Strings.NET_PROFIT);

                    outputList.add(map.get(reportId).get(Strings.NET_PROFIT));
                }
            }else {
                outputList.add(map.get(reportId).get(Strings.NET_PROFIT));
            }
        }

        return outputList;
    }

    public List<ReportVariableOutput> getEPS(){
        List<StockSymbols> stockSymbolsList = stockSymbolsDAO.findByIndustry("Cement &amp; Cement Products");
        List<ReportVariableOutput> list = reportDetailsDAO.getReportVariable(getScripsFromStocksSymbols(stockSymbolsList), getReportVariableList(new String[]{"DILUTED EPS", "BASIC EPS"}));
        List<ReportVariableOutput> outputList = new ArrayList<ReportVariableOutput>();
        Map<Integer, Map<String, ReportVariableOutput>> map = new HashMap<Integer, Map<String, ReportVariableOutput>>();

        for(ReportVariableOutput reportVariableOutput : list){
            if(!map.containsKey(reportVariableOutput.getReportId())){
                map.put(reportVariableOutput.getReportId(), new HashMap<String, ReportVariableOutput>());
            }

            map.get(reportVariableOutput.getReportId()).put(reportVariableOutput.getReportKeyMapping(), reportVariableOutput);
        }

        for(Integer reportId: map.keySet()){
            if(!map.get(reportId).containsKey(Strings.DILUTED_EPS)){
                if(map.get(reportId).containsKey(Strings.BASIC_EPS)){
                    map.get(reportId).put(Strings.DILUTED_EPS, map.get(reportId).get(Strings.BASIC_EPS));
                    map.get(reportId).get(Strings.DILUTED_EPS).setReportKeyMapping(Strings.DILUTED_EPS);

                    outputList.add(map.get(reportId).get(Strings.DILUTED_EPS));
                }
            }else {
                outputList.add(map.get(reportId).get(Strings.DILUTED_EPS));
            }
        }

        return outputList;
    }

    private List<String> getScripsFromStocksSymbols(List<StockSymbols> list){
        List<String> ssList = new ArrayList<String>();
        for(StockSymbols ss: list){
            ssList.add(ss.getScrip());
        }

        return ssList;
    }

    private List<String> getReportVariableList(String[] variables){
        List<String> ssList = new ArrayList<String>();
        for(String ss: variables){
            ssList.add(ss);
        }

        return ssList;
    }

}
