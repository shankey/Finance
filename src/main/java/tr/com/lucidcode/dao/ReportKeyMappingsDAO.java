package tr.com.lucidcode.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.DataException;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.ReportDetails;
import tr.com.lucidcode.model.ReportKeyMappings;
import tr.com.lucidcode.pojo.ReportVariableOutput;
import tr.com.lucidcode.util.HibernateUtil;

import java.util.*;

@SuppressWarnings("unchecked")
@Service("reportKeyMappingsDAO")
public class ReportKeyMappingsDAO extends BaseDao<Account> {


    protected static Logger logger = Logger.getLogger("sessionListener");


    public List<ReportKeyMappings> findAll(List<String> mapping) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Criteria crit = session.createCriteria(ReportKeyMappings.class);
        crit.add(Restrictions.in("reportKeyMapping", mapping));

        List<ReportKeyMappings> existingReports = null;

        try {
            existingReports = (List<ReportKeyMappings>) crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

        System.out.println("mappings " + mapping +existingReports);
        return existingReports;

    }


}
