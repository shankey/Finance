package tr.com.lucidcode.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.DataException;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.model.*;
import tr.com.lucidcode.pojo.MoneyControlDataOutput;
import tr.com.lucidcode.pojo.ReportVariableOutput;
import tr.com.lucidcode.util.HibernateUtil;

import java.util.*;

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

    public static final String moneyControlSripDetailsQuery = "select mcs.name, r.report_type, r.report_date, rkm.report_key_mapping, rd.report_value " +
            "from moneycontrol_scrips mcs, reports r, report_details rd, report_key_mappings rkm " +
            "where mcs.industry='%industry' and mcs.name=r.money_control_id and rd.report_id=r.id " +
            "and rkm.report_key=rd.report_key and rkm.report_key_mapping in (%datakeys) ;";


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

    public void insertAll(List<ReportDetails> list) throws DataException {

        //validate input
        if (list == null) {
            return ;
        }

        List<ReportDetails> existing = findAll(list.get(0).getReportId());
        List<ReportDetails> dbReportDetails = new ArrayList<ReportDetails>();

        Map<String, ReportDetails> keySet = new HashMap<String, ReportDetails>();
        for(ReportDetails reportDetails: existing){
            keySet.put(reportDetails.getReportKey(), reportDetails);
        }

        for(ReportDetails reportDetails: list){
            if(!keySet.containsKey(reportDetails.getReportKey())){
                dbReportDetails.add(reportDetails);
            }
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try {

            for(ReportDetails reportDetails: dbReportDetails){
                session.save(reportDetails);
            }

            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

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

    public List<ReportDetails> findAll(Integer reportId) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Criteria crit = session.createCriteria(ReportDetails.class);
        crit.add(Restrictions.eq("reportId", reportId));


        List<ReportDetails> existingReports = null;

        try {
            existingReports = (List<ReportDetails>) crit.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return existingReports;

    }

    public List<MoneyControlDataOutput> findByReportIdsAndDataMapping(String industry, List<String> reportKeyMappings) throws DataException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();


        String industryQuery = moneyControlSripDetailsQuery.replaceAll("%industry", industry);
        String keysQuery = industryQuery.replaceAll("%datakeys", getWhereClause(reportKeyMappings));

        System.out.println(keysQuery);

        Query dataQuery = session.createSQLQuery(keysQuery);
        List result;
        try {
            result =  dataQuery.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

        List<MoneyControlDataOutput> listOutput = new ArrayList<MoneyControlDataOutput>();

        //mcs.name, r.report_type,r.report_date, rd.report_key, rd.report_value

        for(Object obj: result){
            MoneyControlDataOutput moneyControlDataOutput = new MoneyControlDataOutput();
            Object[] objArray = (Object[]) obj;
            moneyControlDataOutput.setScrip((String)objArray[0]);
            moneyControlDataOutput.setReportType((String)objArray[1]);
            moneyControlDataOutput.setDate((Date)objArray[2]);
            moneyControlDataOutput.setKey((String)objArray[3]);
            try{
                moneyControlDataOutput.setValue(Float.parseFloat(((String)objArray[4]).replace(",","")));
            }catch (NumberFormatException e){
                moneyControlDataOutput.setValue(null);
            }


            listOutput.add(moneyControlDataOutput);
        }

        return listOutput;

    }




}
