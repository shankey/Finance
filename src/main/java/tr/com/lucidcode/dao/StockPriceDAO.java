package tr.com.lucidcode.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.StockPrice;
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.util.HibernateUtil;
import tr.com.lucidcode.util.Utils;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("stockPriceDAO")
public class StockPriceDAO extends BaseDao<Account> {

    /**
     * Page size should be synchronized to this value on the front-end.
     */
    private static final int DEFAULT_PAGE_SIZE = 5;

    protected static Logger logger = Logger.getLogger("sessionListener");



    /**
     *
     * get all results unpagified.
     *
     * @return List of accounts
     * @throws DataException
     */
    public List<StockPrice> findAll(Integer bseId) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Criteria crit = session.createCriteria(StockPrice.class);
        crit.add(Restrictions.eq("bseId", bseId));
        List<StockPrice> stockPriceList;
        try {
            stockPriceList = crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return stockPriceList;
    }


    public List<StockPrice> findByBseIds(List<Integer> bseIds) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        System.out.println(Utils.getSparseDates(new Date()));
        final Criteria crit = session.createCriteria(StockPrice.class);
        crit.add(Restrictions.in("bseId", bseIds));
        crit.add(Restrictions.in("date", Utils.getSparseDates(new Date())));
        List<StockPrice> stockPriceList;
        try {
            stockPriceList = crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        System.out.println(stockPriceList);
        return stockPriceList;
    }



    /**
     *
     * get account data by userName, which is unique.
     *
     * @return List<StockSymbols>
     * @throws DataException
     */
    public void insert(Integer bseId, List<StockPrice> stockPriceList) throws DataException {

        //validate input
        if (stockPriceList == null) {
            return ;
        }

        List<StockPrice> existingStockPrices = findAll(bseId);
        Set<Date> exisitngStockPriceSet = new HashSet<Date>();


        List<StockPrice> newStockPrices = new ArrayList<StockPrice>();

        for(StockPrice esp: existingStockPrices){
            exisitngStockPriceSet.add(esp.getDate());
        }

        for(StockPrice sp: stockPriceList){
            if(!exisitngStockPriceSet.contains(sp.getDate())){
                newStockPrices.add(sp);
            }
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        int i=0;
        try {
            for(StockPrice sp: newStockPrices){
                session.save(sp);
            }

            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
    }



}
