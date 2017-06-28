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
     * updates given fields of a user other than the userName
     *
     * @param account
     * @return updated account
     * @throws ConstraintViolationException
     */
//    public Account update(Account account) throws ConstraintViolationException {
//
//        if (account == null) {
//            throw new IllegalArgumentException();
//        }
//
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//
//        //get existing user
//
//        Criteria crit = session.createCriteria(Account.class);
//        crit.add(Restrictions.eq("username", account.getUsername()));
//
//        Account existing;
//        try {
//            existing = (Account) crit.uniqueResult();
//            if (existing == null) {
//                logger.debug("User could not be found");
//                session.getTransaction().rollback();
//                return null;
//            }
//
//            //update user
//
//            existing.setName(account.getName());
//            existing.setSurname(account.getSurname());
//            existing.setPhoneNumber(account.getPhoneNumber());
//            session.save(existing);
//            session.flush();
//            session.getTransaction().commit();
//        } catch (Exception e) {
//            session.getTransaction().rollback();
//            throw new HibernateException(e.getMessage());
//        }
//
//        return account;
//    }

    /**
     *
     * get a window of results from accounts table.
     * page number starts with 0.
     *
     * @return list of accounts
     * @throws DataException
     */
    public List<Account> findAllPagified(Integer pageNum) throws DataException {

        //fix page number if null
        if (pageNum == null) {
            pageNum = 0;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Criteria crit = session.createCriteria(Account.class);
        crit.setMaxResults(DEFAULT_PAGE_SIZE);
        crit.setFirstResult(DEFAULT_PAGE_SIZE * pageNum);
        List<Account> accountList;
        try {
            accountList = crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (DataException e) {
            session.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return accountList;

    }

    /**
     *
     * get all results unpagified.
     *
     * @return List of accounts
     * @throws DataException
     */
    public List<Account> findAll() throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Criteria crit = session.createCriteria(Account.class);
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

    /**
     *
     * get number of rows in the accounts table
     *
     * @return Long - number of rows
     * @throws DataException
     */
    public Long getRowCount() throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Criteria crit = session.createCriteria(Account.class);
        crit.setProjection(Projections.rowCount());
        Long count;
        try {
            count = (Long) crit.uniqueResult();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return count;

    }

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
