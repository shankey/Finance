package tr.com.lucidcode.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.dao.MutualFundsDAO;
import tr.com.lucidcode.dao.StockSymbolsDAO;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.MutualFunds;
import tr.com.lucidcode.model.StockSymbols;

import java.util.List;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("mutualFundsService")
public class MutualFundsService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    MutualFundsDAO mutualFundsDAO = new MutualFundsDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */

    public void insert(String schemeType, String fundHouse, Integer schemeCode, String isin, String schemeName) {
        mutualFundsDAO.insert(schemeType, fundHouse, schemeCode, isin, schemeName);
    }

    public List<MutualFunds> findAllBySchemeType(String schemeType) {
        return mutualFundsDAO.findAllBySchemeType(schemeType);
    }

}
