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
import tr.com.lucidcode.pojo.ReportVariableOutput;
import tr.com.lucidcode.util.HibernateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
@Service("reportDetailsDAO")
public class ReportDetailsDAO extends BaseDao<Account> {

    /**
     * Page size should be synchronized to this value on the front-end.
     */
    private static final int DEFAULT_PAGE_SIZE = 5;

    protected static Logger logger = Logger.getLogger("sessionListener");

    public static final String reportDetailsQuery = "select a.id, a.report_type, a.bse_id, a.report_date, c.report_key_mapping, b.report_value  " +
            "from (select id, report_type, report_date, bse_id from reports where bse_id in " +
            "(select bse_id from stock_symbols where scrip in (%scrips))) a, " +
            "(select * from report_details) b, " +
            "(select * from report_key_mappings) c " +
            "where a.id=b.report_id and b.report_key=c.report_key and c.report_key_mapping in (%report_key)";


    public String getWhereClause(List<String> ssList){
        String whereString = "";
        int index=0;
        for(String ss: ssList){

            if(index==0){
                whereString = whereString + "'" +ss + "'";
                index=1;
            }else {
                whereString = whereString + ", '" +ss + "' ";
            }
        }
        logger.info("whereString " + whereString);
        return whereString;
    }

    public List<ReportVariableOutput> getReportVariable(List<String> scripList, List<String> reportVariablesList){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        String scripQuery = reportDetailsQuery.replaceAll("%scrips", getWhereClause(scripList));
        String variableQuery = scripQuery.replaceAll("%report_key", getWhereClause(reportVariablesList));

        Query profitQuery = session.createSQLQuery(variableQuery);
        List result;
        try {
            result =  profitQuery.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

        List<ReportVariableOutput> listOutput = new ArrayList<ReportVariableOutput>();

        for(Object obj: result){
            ReportVariableOutput reportVariableOutput = new ReportVariableOutput();
            Object[] objArray = (Object[]) obj;
            reportVariableOutput.setReportId((Integer)objArray[0]);
            reportVariableOutput.setReportType((String)objArray[1]);
            reportVariableOutput.setBseId((Integer)objArray[2]);
            reportVariableOutput.setReportDate((Date)objArray[3]);
            reportVariableOutput.setReportKeyMapping((String)objArray[4]);
            reportVariableOutput.setReportValue(Float.parseFloat(((String)objArray[5]).replace(",","")));


            listOutput.add(reportVariableOutput);
        }

        return listOutput;

    }


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
