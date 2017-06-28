package tr.com.lucidcode.service;

import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import tr.com.lucidcode.dao.AccountDao;
import tr.com.lucidcode.dao.StockPriceDAO;
import tr.com.lucidcode.dao.StockSymbolsDAO;
import tr.com.lucidcode.model.*;
import tr.com.lucidcode.pojo.PricesOutput;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("stockPriceService")
public class StockPriceService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    StockPriceDAO stockPriceDAO = new StockPriceDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */

    public List<PricesOutput> getStockPrices(Integer bseId) {

        logger.info("Start Database Operation");
        List<StockPrice> list = stockPriceDAO.findAll(bseId);
        logger.info("End Database Operation");
        List<PricesOutput> outputList = new ArrayList<PricesOutput>();
        Map<Date, PricesOutput> map = new HashMap<Date, PricesOutput>();
        for(StockPrice sp: list){
            PricesOutput po = new PricesOutput();
            po.setBseId(sp.getBseId());
            po.setClose(sp.getClose());
            po.setOpen(sp.getOpen());
            po.setHigh(sp.getHigh());
            po.setLow(sp.getLow());
            po.setVolume(sp.getVolume());
            po.setPriceDate(sp.getDate());

            outputList.add(po);
            map.put(po.getPriceDate(), po);
        }
        logger.info("Fill Date Operation");
        Date minDate = Collections.min(map.keySet());
        Date maxDate = Collections.max(map.keySet());

        Set<Date> dateSet = getDateSetBetween(minDate, maxDate);

        for(Date date: dateSet){
            if(!map.containsKey(date)){
                PricesOutput po = new PricesOutput();
                po.setBseId(bseId);
                po.setPriceDate(date);
                outputList.add(po);
            }
        }
        logger.info("End Date Operation");

        return outputList;
    }

    public Set<Date> getDateSetBetween(Date minDate, Date maxDate){

        Set<Date> dateSet = new HashSet<Date>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(minDate);

        while(!minDate.equals(maxDate)){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            minDate = calendar.getTime();
            dateSet.add(minDate);
        }

        return dateSet;
    }

    public void insert(Integer bseId, List<StockPrice> stockPriceList) {
        stockPriceDAO.insert(bseId, stockPriceList);
    }

}
