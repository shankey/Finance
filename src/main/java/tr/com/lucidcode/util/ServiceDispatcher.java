package tr.com.lucidcode.util;

import tr.com.lucidcode.service.*;

/**
 * 
 * Utility method for dispatching service requests.
 *
 */
public class ServiceDispatcher {

	private static AccountService singletonAccountService;
	private static StockSymbolsService singletonStockSymbolsService;
	private static ReportsService singletonReportsService;
	private static ReportDetailsService singletonReportDetailsService;
	private static StockPriceService singletonStockPriceService;
	private static RatioCalculatorService singletonRationCalculatorService;

	public static AccountService getAccountService() {
		if (singletonAccountService == null) {
			singletonAccountService = new AccountService();
		}
		return singletonAccountService;
	}

	public static StockSymbolsService getStockSymbolsService() {
		if (singletonStockSymbolsService == null) {
			singletonStockSymbolsService = new StockSymbolsService();
		}
		return singletonStockSymbolsService;
	}

	public static ReportsService getReportsService() {
		if (singletonReportsService == null) {
			singletonReportsService = new ReportsService();
		}
		return singletonReportsService;
	}

	public static ReportDetailsService getReportDetailsService() {
		if (singletonReportDetailsService == null) {
			singletonReportDetailsService = new ReportDetailsService();
		}
		return singletonReportDetailsService;
	}

	public static StockPriceService getStockPriceService() {
		if (singletonStockPriceService == null) {
			singletonStockPriceService = new StockPriceService();
		}
		return singletonStockPriceService;
	}

	public static RatioCalculatorService getRatioCalculatorService() {
		if (singletonRationCalculatorService == null) {
			singletonRationCalculatorService = new RatioCalculatorService();
		}
		return singletonRationCalculatorService;
	}
}
