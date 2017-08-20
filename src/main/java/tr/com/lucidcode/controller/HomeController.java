package tr.com.lucidcode.controller;

import java.io.File;
import java.util.*;

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

import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.pojo.*;
import tr.com.lucidcode.scripts.MoneyControlDataIngest;
import tr.com.lucidcode.scripts.MoneyControlScrapper;
import tr.com.lucidcode.scripts.XMLParse;
import tr.com.lucidcode.service.AccountService;
import tr.com.lucidcode.util.ServiceDispatcher;
import tr.com.lucidcode.util.Strings;
import tr.com.lucidcode.util.Utils;

@Controller
public class HomeController {

	protected static Logger logger = Logger.getLogger("sessionListener");

	@Resource(name = "accountService")
	private AccountService accountService;

	@RequestMapping(value = "/home")
	public ModelAndView listAll() {
		
		logger.debug("Homepage requested");
        System.out.println("Catalina Base "+System.getProperty("catalina.base") + "/MoneyControl/");
        System.out.println("Current path " + new File(".").getAbsolutePath());

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
			//new MoneyControlScrapper().scrapeForIndustry(value);
			//new MoneyControlDataIngest().getAllFolders(value);
		}



		Gson gson = new GsonBuilder().setPrettyPrinting()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonProfitOutput = gson.toJson(map.get(Strings.INDUSTRY));

		return jsonProfitOutput;
	}

	@RequestMapping(value = "/portfoliovalue", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public String portfolioValue(HttpServletResponse response, HttpServletRequest request){

		Map<String, String[]> map = request.getParameterMap();

		Integer noOfStocks = map.get(Strings.NAME).length;
		Float perStockAllocation = 10000.0f/noOfStocks;

		List<MoneyControlScrips> mcsList = ServiceDispatcher.getMoneyControlScripService().getByNames(Arrays.asList(map.get(Strings.NAME)));


		Map<Integer, String> bseIdNameMap = new HashMap<Integer, String>();

		for(MoneyControlScrips mcs: mcsList){
			bseIdNameMap.put(mcs.getBseId(), mcs.getName());
		}

		List<Integer> bseIds = new ArrayList<Integer>();
		bseIds.addAll(bseIdNameMap.keySet());

		Map<Integer, Map<Date, PricesOutput>> bsePriceMap = ServiceDispatcher.getStockPriceService().getAllStockPrices(bseIds);

		List<ScripRatioData> scripRatioDataList = new ArrayList<ScripRatioData>();
		for(Integer bseId: bsePriceMap.keySet()){
			Map<Date, PricesOutput> datePriceMap = bsePriceMap.get(bseId);

			System.out.println("datePriceMap = " + datePriceMap);

			ScripRatioData scd = new ScripRatioData();
			scd.setName(bseIdNameMap.get(bseId));
			scd.setRatio("PRICE");

			//2011
			Date d2011 = Utils.getDate(2011, 7, 1, Collections.min(datePriceMap.keySet()));
			System.out.println(d2011);
			Date p2011 = Utils.nextForward(d2011, datePriceMap.keySet());
			System.out.println("found date = " +p2011);

			if(p2011!=null){
				PricesOutput po2011 = datePriceMap.get(p2011);
				scd.setYear_2011(po2011.getClose());
			}



			//2012
			Date d2012 = Utils.getDate(2012, 7, 1,  Collections.min(datePriceMap.keySet()));
			Date p2012 = Utils.nextForward(d2012, datePriceMap.keySet());

			if(p2012!=null) {
				PricesOutput po2012 = datePriceMap.get(p2012);
				scd.setYear_2012(po2012.getClose());
			}

			//2011
			Date d2013 = Utils.getDate(2013, 7, 1,  Collections.min(datePriceMap.keySet()));
			Date p2013 = Utils.nextForward(d2013, datePriceMap.keySet());

			if(p2013!=null) {
				PricesOutput po2013 = datePriceMap.get(p2013);
				scd.setYear_2013(po2013.getClose());
			}

			//2011
			Date d2014 = Utils.getDate(2014, 7, 1,  Collections.min(datePriceMap.keySet()));
			Date p2014 = Utils.nextForward(d2014, datePriceMap.keySet());

			if(p2014!=null) {
				PricesOutput po2014 = datePriceMap.get(p2014);
				scd.setYear_2014(po2014.getClose());
			}

			//2011
			Date d2015 = Utils.getDate(2015, 7, 1,  Collections.min(datePriceMap.keySet()));
			Date p2015 = Utils.nextForward(d2015, datePriceMap.keySet());

			if(p2015!=null) {
				PricesOutput po2015 = datePriceMap.get(p2015);
				scd.setYear_2015(po2015.getClose());
			}

			//2011
			Date d2016 = Utils.getDate(2016, 7, 1,  Collections.min(datePriceMap.keySet()));
			Date p2016 = Utils.nextForward(d2016, datePriceMap.keySet());

			if(p2016!=null) {
				PricesOutput po2016 = datePriceMap.get(p2016);
				scd.setYear_2016(po2016.getClose());
			}

			//2011
			Date d2017 = Utils.getDate(2017, 7, 1,  Collections.min(datePriceMap.keySet()));
			Date p2017 = Utils.nextForward(d2017, datePriceMap.keySet());

			if(p2017!=null) {
				PricesOutput po2017 = datePriceMap.get(p2017);
				scd.setYear_2017(po2017.getClose());
			}

			scripRatioDataList.add(scd);
		}


		Gson gson = new GsonBuilder().setPrettyPrinting()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonScripRatioDataList = gson.toJson(scripRatioDataList);

		return jsonScripRatioDataList;
	}
}
