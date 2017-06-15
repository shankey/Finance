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
import tr.com.lucidcode.model.ReportDetails;
import tr.com.lucidcode.model.Reports;
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.util.HibernateUtil;

import java.util.List;

@SuppressWarnings("unchecked")
@Service("reportDetailsDAO")
public class ReportDetailsDAO extends BaseDao<Account> {

    /**
     * Page size should be synchronized to this value on the front-end.
     */
    private static final int DEFAULT_PAGE_SIZE = 5;

    protected static Logger logger = Logger.getLogger("sessionListener");


    public ReportDetails insert(ReportDetails reportDetails) throws DataException {

        //validate input
        if (reportDetails == null) {
            return null;
        }

        ReportDetails existing = find(reportDetails);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {

            if (existing != null) {
                session.getTransaction().rollback();
                return existing;
            }else{
                session.save(reportDetails);
                session.flush();
                session.getTransaction().commit();

            }

            //update stock symbol status


        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return find(reportDetails);
    }

    public ReportDetails find(ReportDetails reportDetails) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Criteria crit = session.createCriteria(ReportDetails.class);
        crit.add(Restrictions.eq("reportId", reportDetails.getReportId()));
        crit.add(Restrictions.eq("reportKey", reportDetails.getReportKey()));

        ReportDetails existingReports = null;

        try {
            existingReports = (ReportDetails) crit.uniqueResult();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return existingReports;

    }


}
