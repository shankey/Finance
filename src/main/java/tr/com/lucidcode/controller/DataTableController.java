package tr.com.lucidcode.controller;

import org.apache.log4j.Logger;
import org.hsqldb.lib.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import tr.com.lucidcode.pojo.ScripRatioData;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping ("/dt")
public class DataTableController {
    protected static Logger logger = Logger.getLogger("sessionListener");

    @RequestMapping(value = {"", "/"})
    public ModelAndView defaultView() {
        logger.debug("Default view requested");

        ModelAndView modelAndView = new ModelAndView();

        List<String> industries = ServiceDispatcher.getMoneyControlScripService().getAllIndustries();
        modelAndView.addObject("industries", industries);

        modelAndView.setViewName("datatable");
        return modelAndView;
    }

    @RequestMapping(value = "/{industry}")
    public ModelAndView industryView(@PathVariable String industry) {
        logger.debug("View for industry : " + industry + " requested");

        if (StringUtil.isEmpty(industry)) return defaultView();

        ModelAndView modelAndView = new ModelAndView();

        List<String> industries = ServiceDispatcher.getMoneyControlScripService().getAllIndustries();
        modelAndView.addObject("industries", industries);
        modelAndView.addObject("industryData", getIndustryData(industry));
        modelAndView.addObject("selectedIndustry", industry);

        modelAndView.setViewName("datatable");
        return modelAndView;
    }

    private List<ScripRatioData> getIndustryData(String industry){
        List<String> list = new ArrayList<String>();
        list.add("ROCE");
        list.add("DILUTED EPS");
        list.add("EQUITY_SHARE_CAPITAL");
        list.add("FACE VALUE");

        List<List> ratioScripDataMap = ServiceDispatcher.getScripsDataService().getDataForSector(industry, list);
        List<ScripRatioData> scripRatioDataList = new ArrayList<ScripRatioData>();

        for(List li: ratioScripDataMap) {
            ScripRatioData scripRatioData = new ScripRatioData();
            scripRatioData.setRatio((String) li.get(0));
            scripRatioData.setReportType((String) li.get(1));
            scripRatioData.setName((String) li.get(2));


            scripRatioData.setYear_2011((Float)li.get(3));
            scripRatioData.setYear_2012((Float)li.get(4));
            scripRatioData.setYear_2013((Float)li.get(5));
            scripRatioData.setYear_2014((Float)li.get(6));
            scripRatioData.setYear_2015((Float)li.get(7));
            scripRatioData.setYear_2016((Float)li.get(8));
            scripRatioData.setYear_2017((Float)li.get(9));

            scripRatioDataList.add(scripRatioData);
        }

        return scripRatioDataList;
    }
}
