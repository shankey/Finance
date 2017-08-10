package tr.com.lucidcode.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.DataException;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.MutualFunds;
import tr.com.lucidcode.model.StockPrice;
import tr.com.lucidcode.util.HibernateUtil;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("mutualFundsDAO")
public class MutualFundsDAO extends BaseDao<Account> {

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
    public List<MutualFunds> findAllBySchemeType(String schemeType) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Criteria crit = session.createCriteria(MutualFunds.class);
        crit.add(Restrictions.eq("schemeType", schemeType));
        List<MutualFunds> mutualFundsList;
        try {
            mutualFundsList = crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return mutualFundsList;
    }

    public MutualFunds findBySchemeCode(Integer schemeCode) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Criteria crit = session.createCriteria(MutualFunds.class);
        crit.add(Restrictions.eq("schemeCode", schemeCode));
        MutualFunds mutualFunds;
        try {
            mutualFunds = (MutualFunds) crit.uniqueResult();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return mutualFunds;
    }



    /**
     *
     * get account data by userName, which is unique.
     *
     * @return List<StockSymbols>
     * @throws DataException
     */
    public void insert(String schemeType, String fundHouse, Integer schemeCode, String isin, String schemeName) throws DataException {

        //validate input
        if (schemeType == null || fundHouse==null || schemeCode==null || schemeName==null) {
            return ;
        }

        MutualFunds mfExisting = findBySchemeCode(schemeCode);

        if(mfExisting!=null){
            return ;
        }


        MutualFunds mfNew = new MutualFunds();

        mfNew.setFundHouse(fundHouse);
        mfNew.setIsin(isin);
        mfNew.setSchemeCode(schemeCode);
        mfNew.setSchemeName(schemeName);
        mfNew.setSchemeType(schemeType);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {

            session.save(mfNew);

            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }


    }



}
