package tr.com.lucidcode.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.DataException;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.model.RatioDetails;
import tr.com.lucidcode.pojo.IndustryCap;
import tr.com.lucidcode.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Service("ratioDetailsDAO")
public class RatioDetailsDAO extends BaseDao<Account> {

    String sectorCapQuery = "select sector, sum(year_2011), sum(year_2012), sum(year_2013), sum(year_2014), sum(year_2015), sum(year_2016), sum(year_2017) from ratio_details where ratio='MARKET CAP' group by sector;";

    String scripMCapQuery = "select sector, money_control_id, ratio, report_type, " +
            "year_2011, year_2012, year_2013, year_2014, year_2015, year_2016, year_2017 from " +
            "ratio_details where %year_null_checks order by sector, %year_order desc;";

//year_2013 is not null

    protected static Logger logger = Logger.getLogger("sessionListener");


    public List<RatioDetails> scripCaps(Integer startYear, Integer endYear){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        String yearNullChecks = "";
        for(Integer i=startYear; i<=endYear; i++){
            if(i<endYear){
                yearNullChecks += "year_"+i.toString() + " is not null and ";
            }else{
                yearNullChecks += "year_"+i.toString() + " is not null";
            }
        }

        String dbScriptMCapQuery = scripMCapQuery.replaceAll("%year_null_checks", yearNullChecks);
        dbScriptMCapQuery = dbScriptMCapQuery.replaceAll("%year_order", "year_"+endYear.toString());
        System.out.println(dbScriptMCapQuery);

        Query dataQuery = session.createSQLQuery(dbScriptMCapQuery);
        List result;
        try {
            result =  dataQuery.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

        List<RatioDetails> ratioDetails = new ArrayList<RatioDetails>();

        for(Object obj: result){
            RatioDetails rds = new RatioDetails();
            Object[] objArray = (Object[]) obj;
            rds.setSector((String)objArray[0]);
            rds.setMoneyControlId((String)objArray[1]);
            rds.setRatio((String)objArray[2]);
            rds.setReportType((String)objArray[3]);
            rds.setYear2011((Float)objArray[4]);
            rds.setYear2012((Float)objArray[5]);
            rds.setYear2013((Float)objArray[6]);
            rds.setYear2014((Float)objArray[7]);
            rds.setYear2015((Float)objArray[8]);
            rds.setYear2016((Float)objArray[9]);
            rds.setYear2017((Float)objArray[10]);

            ratioDetails.add(rds);

        }

        return ratioDetails;

    }

    public List<IndustryCap> getSectorCap(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Query dataQuery = session.createSQLQuery(sectorCapQuery);
        List result;
        try {
            result =  dataQuery.list();
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

        List<IndustryCap> industryCapList = new ArrayList<IndustryCap>();

        for(Object obj: result){
            IndustryCap ic = new IndustryCap();
            Object[] objArray = (Object[]) obj;
            ic.setSector((String)objArray[0]);
            ic.setYear2011((Double)objArray[1]);
            ic.setYear2012((Double)objArray[2]);
            ic.setYear2013((Double)objArray[3]);
            ic.setYear2014((Double)objArray[4]);
            ic.setYear2015((Double)objArray[5]);
            ic.setYear2016((Double)objArray[6]);
            ic.setYear2017((Double)objArray[7]);

            industryCapList.add(ic);

        }

        return industryCapList;

    }

    public RatioDetails insert(RatioDetails ratioDetails) throws DataException {

        //validate input
        if (ratioDetails == null || ratioDetails.getMoneyControlId() == null || ratioDetails.getRatio()==null) {
            return null;
        }

        RatioDetails existing;
        existing = getByMcDetails(ratioDetails.getMoneyControlId(), ratioDetails.getRatio(), ratioDetails.getReportType());

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();


        try {

            if (existing != null) {
                existing.setYear2011(ratioDetails.getYear2011());
                existing.setYear2012(ratioDetails.getYear2012());
                existing.setYear2013(ratioDetails.getYear2013());
                existing.setYear2014(ratioDetails.getYear2014());
                existing.setYear2015(ratioDetails.getYear2015());
                existing.setYear2016(ratioDetails.getYear2016());
                existing.setYear2017(ratioDetails.getYear2017());

                System.out.println("--------------------");
                System.out.println(existing);
            }else {
                existing = ratioDetails;
            }

            session.saveOrUpdate(existing);
            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }

        return getByMcDetails(ratioDetails.getMoneyControlId(), ratioDetails.getRatio(), ratioDetails.getReportType());
    }


    public RatioDetails getByMcDetails(String mcId, String ratio, String reportType) throws DataException {

        //validate input
        if (mcId == null) {
            return null ;
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Criteria crit = session.createCriteria(RatioDetails.class);
        crit.add(Restrictions.eq("moneyControlId", mcId));
        crit.add(Restrictions.eq("ratio", ratio));
        crit.add(Restrictions.eq("reportType", reportType));

        RatioDetails existing;
        try {
            existing = (RatioDetails) crit.uniqueResult();
            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new HibernateException(e.getMessage());
        }
        return existing;
    }




}
