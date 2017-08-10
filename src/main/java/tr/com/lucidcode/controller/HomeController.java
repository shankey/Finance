package tr.com.lucidcode.controller;

import java.util.ArrayList;
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
import tr.com.lucidcode.pojo.DateValue;
import tr.com.lucidcode.pojo.MoneyControlDataOutput;
import tr.com.lucidcode.pojo.PricesOutput;
import tr.com.lucidcode.pojo.ReportVariableOutput;
import tr.com.lucidcode.scripts.XMLParse;
import tr.com.lucidcode.service.AccountService;
import tr.com.lucidcode.util.ServiceDispatcher;

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

	@RequestMapping(value = "/mcdata", produces = "application/json")
	@ResponseBody
	public String MCData(HttpServletResponse response){

		List<String> list = new ArrayList<String>();
		list.add("ROCE");
		list.add("DILUTED EPS");

		List<List> ratioScripDataMap = ServiceDispatcher.getScripsDataService().getDataForSector("cementmajor", list);

		addCORS(response);

		logger.debug("MC Data requested");

		Gson gson = new GsonBuilder().setPrettyPrinting()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonProfitOutput = gson.toJson(ratioScripDataMap);

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

		Gson gson = new GsonBuilder().setPrettyPrinting()
				.setDateFormat("dd-MM-yyyy").create();

		String jsonProfitOutput = gson.toJson("");

		return jsonProfitOutput;
	}
}
