package tr.com.lucidcode.scripts;

import tr.com.lucidcode.helper.Inputs;
import tr.com.lucidcode.model.RatioDetails;
import tr.com.lucidcode.pojo.DateValue;
import tr.com.lucidcode.util.ServiceDispatcher;
import tr.com.lucidcode.util.Strings;

import javax.xml.ws.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adinema on 14/10/17.
 */
public class IndstryDataAggregator {

    public static void main(String args[]){
        List<RatioDetails> ratioDetails = ServiceDispatcher.getRatioDetailsService().getScripMarketCaps(2013, 2016);
        System.out.println("Ratio Details = " + ratioDetails);
        //System.out.println(ServiceDispatcher.getRatioDetailsService().compareSectors(ratioDetails));
        //System.out.println(ServiceDispatcher.getRatioDetailsService().getIndustryCaps());
        //new IndstryDataAggregator().ingestAllMarketCaps();
    }

    public void ingestAllMarketCaps(){
        List<String> industries = ServiceDispatcher.getMoneyControlScripService().getAllIndustries();
        List<String> ratios = Inputs.getRatioList();

        for(String sector: industries){
            Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap =
                    ServiceDispatcher.getScripsDataService().getMapDataForSector(sector, ratios);

            Map<String, Map<String, Map<String, List<DateValue>>>> mcapTypeScripDataMap =
                    new HashMap<String, Map<String, Map<String, List<DateValue>>>>();

            Map<String, Map<String, List<DateValue>>> typeScripDataMap = ratioTypeScripDataMap.get(Strings.MARKET_CAP);
            mcapTypeScripDataMap.put(Strings.MARKET_CAP, typeScripDataMap);

            List<List> mcapOutput = ServiceDispatcher.getScripsDataService().getAsList(mcapTypeScripDataMap);
            System.out.println("mcapOutput " + mcapOutput);

            if(mcapOutput==null || mcapOutput.size()==0){
                continue;
            }

            for(List li: mcapOutput){
                RatioDetails ratioDetails = new RatioDetails();

                ratioDetails.setRatio((String)li.get(0));
                ratioDetails.setReportType((String)li.get(1));
                ratioDetails.setMoneyControlId((String)li.get(2));
                ratioDetails.setSector(sector);

                if(li.get(3)!=null){
                    ratioDetails.setYear2011((Float) li.get(3));
                }

                if(li.get(4)!=null){
                    ratioDetails.setYear2012((Float)li.get(4));
                }

                if(li.get(5)!=null){
                    ratioDetails.setYear2013((Float)li.get(5));
                }

                if(li.get(6)!=null){
                    ratioDetails.setYear2014((Float)li.get(6));
                }

                if(li.get(7)!=null){
                    ratioDetails.setYear2015((Float) li.get(7));
                }

                if(li.get(8)!=null){
                    ratioDetails.setYear2016((Float) li.get(8));
                }

                if(li.get(9)!=null){
                    ratioDetails.setYear2017((Float) li.get(9));
                }

                ServiceDispatcher.getRatioDetailsService().insertRatioDetails(ratioDetails);
            }
        }
    }
}

