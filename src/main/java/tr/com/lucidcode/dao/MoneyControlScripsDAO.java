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
import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.util.HibernateUtil;

import java.util.List;

@SuppressWarnings("unchecked")
@Service("moneyControlScripsDAO")
public class MoneyControlScripsDAO extends BaseDao<Account> {



    protected static Logger logger = Logger.getLogger("sessionListener");




    /**
     *
     * get all results unpagified.
     *
     * @return List of accounts
     * @throws DataException
//     */
//    public List<Account> findAll() throws DataException {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        final Criteria crit = session.createCriteria(Account.class);
//        List<Account> accountList;
//        try {
//            accountList = crit.list();
//            session.flush();
//            session.getTransaction().commit();
//        } catch (Exception e) {
//            session.getTransaction().rollback();
//            throw new HibernateException(e.getMessage());
//        }
//        return accountList;
//
//    }


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
    public void setStatus(Integer id, Integer status) throws DataException {

        //validate input
        if (id == null || status == null) {
            return ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(MoneyControlScrips.class);
        crit.add(Restrictions.eq("id", id));
        MoneyControlScrips existing;
        try {
            existing = (MoneyControlScrips) crit.uniqueResult();
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

    public MoneyControlScrips insert(MoneyControlScrips mcs) throws DataException {

        //validate input
        if (mcs == null || mcs.getIndustry() == null || mcs.getName()==null || mcs.getUrl() == null) {
            return null;
        }

        MoneyControlScrips existing;
        existing = getByName(mcs.getName());

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();


        try {

            if (existing != null) {
                existing.setBseId(mcs.getBseId());
                existing.setNseId(mcs.getNseId());
                existing.setSector(mcs.getSector());
            }else {
                existing = mcs;
            }

            session.saveOrUpdate(existing);
            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

        return getByName(mcs.getName());
    }


    public MoneyControlScrips getByName(String name) throws DataException {

        //validate input
        if (name == null) {
            return null ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Criteria crit = session.createCriteria(MoneyControlScrips.class);
        crit.add(Restrictions.eq("name", name));

        MoneyControlScrips existing;
        try {
            existing = (MoneyControlScrips) crit.uniqueResult();
            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return existing;
    }

    public List<MoneyControlScrips> getByIndustry(String industry) throws DataException {

        //validate input
        if (industry == null) {
            return null ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Criteria crit = session.createCriteria(MoneyControlScrips.class);
        crit.add(Restrictions.eq("industry", industry));

        List<MoneyControlScrips> existing;
        try {
            existing = (List<MoneyControlScrips>) crit.list();
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
