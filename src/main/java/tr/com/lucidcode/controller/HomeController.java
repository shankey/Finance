package tr.com.lucidcode.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.pojo.*;
import tr.com.lucidcode.scripts.MoneyControlDataIngest;
import tr.com.lucidcode.scripts.MoneyControlScrapper;
import tr.com.lucidcode.scripts.XMLParse;
import tr.com.lucidcode.service.AccountService;
import tr.com.lucidcode.util.ServiceDispatcher;
import tr.com.lucidcode.util.Strings;

@Controller
public class HomeController {

	protected static Logger logger = Logger.getLogger("sessionListener");

	@Resource(name = "accountService")
	private AccountService accountService;

	@RequestMapping(value = "/home")
	public ModelAndView listAll() {
		
		logger.debug("Homepage requested");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");

		List<String> industries = ServiceDispatcher.getMoneyControlScripService().getAllIndustries();


		modelAndView.addObject("industries", industries);

		return modelAndView;
	}

	@RequestMapping(value = "/profits", produces = "application/json")
	@ResponseBody
	public String getProfits(HttpServletResponse response) {

		addCORS(response);

		List<ReportVariableOutput> list = ServiceDispatcher.getRatioCalculatorService().getProfitDetails();

		Gson gson = new GsonBuilder()
				.setDateFormat("dd-MM-yyyy").create();
		String jsonProfitOutput = gson.toJson(list);

		return jsonProfitOutput;
	}


	@RequestMapping(value = "/profitWDC")
	public ModelAndView getWDC(HttpServletResponse response) {

		addCORS(response);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("profitWDC");


		return modelAndView;

	}

	@RequestMapping(value = "/prices", produces = "application/json")
	@ResponseBody
	public String getPrices(HttpServletResponse response) {

		addCORS(response);

		logger.debug("Profits requested");

		List<PricesOutput> list = ServiceDispatcher.getStockPriceService().getStockPrices(500387);

		Gson gson = new GsonBuilder()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonProfitOutput = gson.toJson(list);

		return jsonProfitOutput;
	}

	@RequestMapping(value = "/pricesWDC")
	public ModelAndView getPricesWDC(HttpServletResponse response) {

		addCORS(response);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("pricesWDC");

		// intercept OPTIONS method

		return modelAndView;

	}

	@RequestMapping(value = "/eps", produces = "application/json")
	@ResponseBody
	public String getEps(HttpServletResponse response) {

		addCORS(response);

		logger.debug("EPS requested");

		List<ReportVariableOutput> list = ServiceDispatcher.getRatioCalculatorService().getEPS();

		Gson gson = new GsonBuilder()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonProfitOutput = gson.toJson(list);

		return jsonProfitOutput;
	}

	@RequestMapping(value = "/EPSWDC")
	public ModelAndView getEPSWDC(HttpServletResponse response) {

		addCORS(response);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("EPSWDC");

		// intercept OPTIONS method

		return modelAndView;

	}


	public void addCORS(HttpServletResponse response){
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		response.addHeader("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");

	}

	@RequestMapping(value = "/mcdata", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public String MCData(HttpServletResponse response, HttpServletRequest request) {

        logger.debug("MC Data requested");

        List<String> list = new ArrayList<String>();
        list.add("ROCE");
        list.add("DILUTED EPS");

        Map<String, String[]> map = request.getParameterMap();

        String industry = map.get(Strings.INDUSTRY)[0];
        if(industry==null || industry.equals("")){
            return null;
        }
        List<List> ratioScripDataMap = ServiceDispatcher.getScripsDataService().getDataForSector(industry, list);

//		List<Map<String, String>> scripRatioDataMapList = new ArrayList<Map<String, String>>();
//
//		for(List li: ratioScripDataMap){
//			Map<String, String> keyVal = new HashMap<String, String>();
//			keyVal.put("Ratio", (String)li.get(0));
//			keyVal.put("Report Type", (String)li.get(1));
//			keyVal.put("Scrip Name", (String)li.get(2));
//
//			Integer yearStart = 2011;
//			for(int i=3; i<=9; i++){
//				if(li.get(i)!=null){
//					keyVal.put(yearStart.toString(), li.get(i).toString());
//				}else{
//					keyVal.put(yearStart.toString(), "");
//				}
//				yearStart++;
//
//			}
//        }

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

		addCORS(response);

		Gson gson = new GsonBuilder().setPrettyPrinting()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonProfitOutput = gson.toJson(scripRatioDataList);

		return jsonProfitOutput;
	}



	@RequestMapping(value = "/mcscrape", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public String MCScrape(HttpServletResponse response, HttpServletRequest request){

		Map<String, String[]> map = request.getParameterMap();

		for(String key: map.keySet()){
			logger.info("Parms = " + key);
			for(String value: map.get(key)){
				logger.info("Parm Value = "+ value);
			}
		}

		for(String value: map.get(Strings.INDUSTRY)){
			new MoneyControlScrapper().scrapeForIndustry(value);
			new MoneyControlDataIngest().getAllFolders(value);
		}



		Gson gson = new GsonBuilder().setPrettyPrinting()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonProfitOutput = gson.toJson(map.get(Strings.INDUSTRY));

		return jsonProfitOutput;
	}
}
