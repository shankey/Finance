package tr.com.lucidcode.scripts;

import tr.com.lucidcode.helper.Inputs;
import tr.com.lucidcode.pojo.DateValue;
import tr.com.lucidcode.util.ServiceDispatcher;
import tr.com.lucidcode.util.Strings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adinema on 14/10/17.
 */
public class IndstryDataAggregator {

    public static void main(String args[]){
        new IndstryDataAggregator().getAllMarketCaps();
    }

    public void getAllMarketCaps(){
        List<String> industries = ServiceDispatcher.getMoneyControlScripService().getAllIndustries();
        List<String> ratios = Inputs.getRatioList();

        for(String industry: industries){
            Map<String, Map<String, Map<String, List<DateValue>>>> ratioTypeScripDataMap =
                    ServiceDispatcher.getScripsDataService().getMapDataForSector(industry, ratios);

            Map<String, Map<String, Map<String, List<DateValue>>>> mcapTypeScripDataMap =
                    new HashMap<String, Map<String, Map<String, List<DateValue>>>>();

            Map<String, Map<String, List<DateValue>>> typeScripDataMap = ratioTypeScripDataMap.get(Strings.MARKET_CAP);
            mcapTypeScripDataMap.put(Strings.MARKET_CAP, typeScripDataMap);

            List<List> mcapOutput = ServiceDispatcher.getScripsDataService().getAsList(mcapTypeScripDataMap);
            System.out.println("mcapOutput " + mcapOutput);
        }
    }
}

