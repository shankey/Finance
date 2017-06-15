package tr.com.lucidcode.util;

import tr.com.lucidcode.service.AccountService;
import tr.com.lucidcode.service.ReportDetailsService;
import tr.com.lucidcode.service.ReportsService;
import tr.com.lucidcode.service.StockSymbolsService;

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
}
