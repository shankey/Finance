package tr.com.lucidcode.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import tr.com.lucidcode.dao.AccountDao;
import tr.com.lucidcode.dao.ReportsDAO;
import tr.com.lucidcode.dao.StockSymbolsDAO;
import tr.com.lucidcode.model.*;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("reportsService")
public class ReportsService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    ReportsDAO reportsDAO = new ReportsDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */

    public Reports insertReport(Reports reports){
        return reportsDAO.insert(reports);
    }
}
