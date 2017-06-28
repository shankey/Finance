package tr.com.lucidcode.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import tr.com.lucidcode.dao.AccountDao;
import tr.com.lucidcode.dao.StockSymbolsDAO;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.ResponseAccount;
import tr.com.lucidcode.model.ResponseAccountList;
import tr.com.lucidcode.model.StockSymbols;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("stockSymbolsService")
public class StockSymbolsService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    StockSymbolsDAO stockSymbolsDAO = new StockSymbolsDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */

    public List<StockSymbols> getStocks(String industry) {
        return stockSymbolsDAO.findByIndustry(industry);
    }

    public void setStatus(Integer id, Float status) {
        stockSymbolsDAO.setStatus(id, status);
    }

    public void setPriceStatus(Integer id, Integer status) {
        stockSymbolsDAO.setPriceStatus(id, status);
    }

    public Integer getBseIdFromScrip(String scrip){
        return stockSymbolsDAO.getBseIdFromScrip(scrip);
    }

}
