package tr.com.lucidcode.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import tr.com.lucidcode.pojo.MoneyControlDataOutput;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/gph")
public class GraphsController {
    protected static Logger logger = Logger.getLogger("sessionListener");

    @RequestMapping(value = {"", "/"})
    public ModelAndView defaultGraph() {
        logger.debug("Default graph requested");

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("graph");

        return modelAndView;
    }

    @RequestMapping("/{industry}/{stock}")
    public ModelAndView stockGraph(@PathVariable String industry, @PathVariable String stock) {
        logger.debug("Stock graph requested for:" + stock);
        ModelAndView modelAndView = new ModelAndView();

        List<String> list = new ArrayList<String>();
        list.add("ROCE");
        list.add("DILUTED EPS");

        List<MoneyControlDataOutput> ratioScripDataMap = ServiceDispatcher.getScripsDataService().getDataForScrip(stock, list);


		    //stock     //metric
		Map<String, Map<String, List<Map<String, String>>>> graphJsonDS = new HashMap<String, Map<String, List<Map<String, String>>>>();

		for (MoneyControlDataOutput li: ratioScripDataMap){
            String cur_stock = li.getScrip();
            String ratio = li.getKey() + "_" + li.getReportType();
            ratio = ratio.replace(' ', '-');
            if(!stock.equalsIgnoreCase(cur_stock)) {
                continue;
            }

            // Stock -> Metrics -> Data - Value
            Map<String, List<Map<String, String>>> stockMap = graphJsonDS.containsKey(cur_stock) ?
                    graphJsonDS.get(cur_stock) : new HashMap<String, List<Map<String, String>>>();

            List<Map<String, String>> ratioData = stockMap.containsKey(ratio) ?
                    stockMap.get(ratio) : new ArrayList<Map<String, String>>();

            Map<String, String> keyVal = new HashMap<String, String>();
            keyVal.put("date", li.getDate().toString() );
            keyVal.put("value", li.getValue().toString());

            ratioData.add(keyVal);

            stockMap.put(ratio, ratioData);
            graphJsonDS.put(cur_stock, stockMap);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting()
                .setDateFormat("dd-MM-yyyy").create();

        String graphData = gson.toJson(graphJsonDS);

        modelAndView.setViewName("graph");
        modelAndView.addObject("graphJson", graphData);

        return modelAndView;
    }
}
