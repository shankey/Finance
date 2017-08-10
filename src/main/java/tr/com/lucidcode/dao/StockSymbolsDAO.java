package tr.com.lucidcode.dao;

import java.util.List;
import java.util.StringTokenizer;

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
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.util.HibernateUtil;

@SuppressWarnings("unchecked")
@Service("stockSymbolsDAO")
public class StockSymbolsDAO extends BaseDao<Account> {

    /**
     * Page size should be synchronized to this value on the front-end.
     */
    private static final int DEFAULT_PAGE_SIZE = 5;

    protected static Logger logger = Logger.getLogger("sessionListener");

    /**
     *
     * get account data by userName, which is unique.
     *
     * @param industry
     * @return List<StockSymbols>
     * @throws DataException
     */
    public List<StockSymbols> findByIndustry(String industry) throws DataException {

        //validate input
        if (industry == null) {
            return null;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(StockSymbols.class);
        crit.add(Restrictions.eq("industry", industry));
        List<StockSymbols> stockSymbols;
        try {
            stockSymbols = (List<StockSymbols>) crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        logger.info(stockSymbols);
        System.out.println(stockSymbols);
        return stockSymbols;
    }


    /**
     *
     * get account data by userName, which is unique.
     *
     *
     * @throws DataException
     */
    public void setStatus(Integer id, Float status) throws DataException {

        //validate input
        if (id == null || status == null) {
            return ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(StockSymbols.class);
        crit.add(Restrictions.eq("id", id));
        StockSymbols existing;
        try {
            existing = (StockSymbols) crit.uniqueResult();
            if (existing == null) {
                logger.debug("Stock Symbol could not be found");
                session.getTransaction().rollback();
                return;
            }

            //update stock symbol status

            existing.setStatus(status);
            session.save(existing);
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
    }

    public void setPriceStatus(Integer id, Integer status) throws DataException {

        //validate input
        if (id == null || status == null) {
            return ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(StockSymbols.class);
        crit.add(Restrictions.eq("id", id));
        StockSymbols existing;
        try {
            existing = (StockSymbols) crit.uniqueResult();
            if (existing == null) {
                logger.debug("Stock Symbol could not be found");
                session.getTransaction().rollback();
                return;
            }

            //update stock symbol status

            existing.setPriceStatus(status);
            session.save(existing);
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
    }

    public Integer getBseIdFromScrip(String scrip) throws DataException {

        //validate input
        if (scrip == null) {
            return null ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(StockSymbols.class);
        crit.add(Restrictions.eq("scrip", scrip));
        StockSymbols existing;
        try {
            existing = (StockSymbols) crit.uniqueResult();
            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return existing.getBseId();
    }

    public StockSymbols getByBseId(Integer bseId) throws DataException {

        //validate input
        if (bseId == null) {
            return null ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(StockSymbols.class);
        crit.add(Restrictions.eq("bseId", bseId));
        StockSymbols existing;
        try {
            existing = (StockSymbols) crit.uniqueResult();
            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return existing;
    }


    /**
     *
     * search account by a keyword which is then
     * compared to name and surname fields
     *
     * @param text
     * @return list of accounts
     * @throws DataException
     */
    public List<Account> textSearch(String text) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Account.class);
        Criterion name = Restrictions.like("name", text);
        Criterion surname = Restrictions.like("surname", text);
        LogicalExpression expression = Restrictions.or(name, surname);

        crit.add(expression);
        List<Account> accountList;
        try {
            accountList = crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return accountList;
    }

}
