package tr.com.lucidcode.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.dao.ReportDetailsDAO;
import tr.com.lucidcode.dao.StockSymbolsDAO;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.ReportDetails;
import tr.com.lucidcode.model.StockSymbols;

import java.util.List;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("stockSymbolsService")
public class ReportDetailsService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    ReportDetailsDAO reportDetailsDAO = new ReportDetailsDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */

    public void insert(ReportDetails reportDetails){
        reportDetailsDAO.insert(reportDetails);
    }

}
