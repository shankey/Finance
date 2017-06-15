package tr.com.lucidcode.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.exception.DataException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.ResponseAccountList;
import tr.com.lucidcode.model.StockSymbols;
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

		List<StockSymbols> stockSymbolsList = null;


		new XMLParse().main(null);




		modelAndView.addObject("stockSymbolsList", stockSymbolsList);

		return modelAndView;
	}
}
