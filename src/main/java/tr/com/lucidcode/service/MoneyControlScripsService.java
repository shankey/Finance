package tr.com.lucidcode.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.dao.MoneyControlScripsDAO;
import tr.com.lucidcode.dao.StockSymbolsDAO;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.model.StockSymbols;

import java.util.List;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("moneyControlScripsService")
public class MoneyControlScripsService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    MoneyControlScripsDAO moneyControlScripsDAO = new MoneyControlScripsDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */


    public MoneyControlScrips insert(MoneyControlScrips mcs) {
        return moneyControlScripsDAO.insert(mcs);
    }

    public MoneyControlScrips insert(String name, String industry, String url, Integer bseId, String nseId, String sector) {
        MoneyControlScrips mcs = new MoneyControlScrips();
        mcs.setIndustry(industry);
        mcs.setName(name);
        mcs.setUrl(url);
        mcs.setBseId(bseId);
        mcs.setNseId(nseId);
        mcs.setSector(sector);

        return moneyControlScripsDAO.insert(mcs);
    }

    public List<MoneyControlScrips> getAllByIndustry(String industry) {

        return moneyControlScripsDAO.getByIndustry(industry);
    }

    public List<String> getAllIndustries() {
        return moneyControlScripsDAO.getAllIndustries();
    }

    public void setStatus(Integer id, Integer status) {
        moneyControlScripsDAO.setStatus(id, status);
    }

    public void setPriceStatus(Integer id, Integer status) {
        moneyControlScripsDAO.setPriceStatus(id, status);
    }

    public List<MoneyControlScrips> getByNames(List<String> names){
        return moneyControlScripsDAO.getByNames(names);
    }


}
