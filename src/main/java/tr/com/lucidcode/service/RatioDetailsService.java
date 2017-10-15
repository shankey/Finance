package tr.com.lucidcode.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import tr.com.lucidcode.dao.RatioDetailsDAO;
import tr.com.lucidcode.dao.ReportsDAO;
import tr.com.lucidcode.model.Account;
import tr.com.lucidcode.model.RatioDetails;
import tr.com.lucidcode.model.Reports;
import tr.com.lucidcode.pojo.IndustryCap;
import tr.com.lucidcode.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Main service associated with operations on accounts table
 *
 */
@Service("ratioDetailsService")
public class RatioDetailsService extends BaseService<Account> {

    protected static Logger logger = Logger.getLogger("sessionListener");

    RatioDetailsDAO ratioDetailsDAO = new RatioDetailsDAO();

    /**
     *
     * retrieve a window of accounts
     *
     * @return List of StockSymbols
     */

    public RatioDetails insertRatioDetails(RatioDetails ratioDetails){
        return ratioDetailsDAO.insert(ratioDetails);
    }

    public List<IndustryCap> getIndustryCaps(){
        return ratioDetailsDAO.getSectorCap();
    }

    public List<RatioDetails> getScripMarketCaps(Integer startYear, Integer endYear){
        return ratioDetailsDAO.scripCaps(startYear, endYear);
    }

    public List<List> compareSectors(List<RatioDetails> ratioDetails, Integer startYear, Integer endYear){
        Map<String, List<RatioDetails>> sectorRatioDetailsListMap = new HashMap<String, List<RatioDetails>>();

        for(RatioDetails rds: ratioDetails){
            if(sectorRatioDetailsListMap.get(rds.getSector())==null){
                sectorRatioDetailsListMap.put(rds.getSector(), new ArrayList<RatioDetails>());
            }
            sectorRatioDetailsListMap.get(rds.getSector()).add(rds);
        }

        List<List> csvList = new ArrayList<List>();

        for(String sector: sectorRatioDetailsListMap.keySet()){
            Float sum2011=0f;
            Float sum2012=0f;
            Float sum2013=0f;
            Float sum2014=0f;
            Float sum2015=0f;
            Float sum2016=0f;
            Float sum2017=0f;
            String ratio= Strings.MARKET_CAP;
            String type=Strings.GENERATED;
            String scrip=sector;
            Integer index=0;

            System.out.println(sector + " " + sectorRatioDetailsListMap.get(sector));

            for(RatioDetails rds: sectorRatioDetailsListMap.get(sector)){

                if(rds.getYear2011()!=null){
                    sum2011 += rds.getYear2011();
                }

                if(rds.getYear2012()!=null){
                    sum2012 += rds.getYear2012();
                }

                if(rds.getYear2013()!=null){
                    sum2013 += rds.getYear2013();
                }

                if(rds.getYear2014()!=null){
                    sum2014 += rds.getYear2014();
                }

                if(rds.getYear2015()!=null){
                    sum2015 += rds.getYear2015();
                }

                if(rds.getYear2016()!=null){
                    sum2016 += rds.getYear2016();
                }

                if(rds.getYear2017()!=null){
                    sum2017 += rds.getYear2017();
                }
                index++;
                if(index>=4){
                    break;
                }
            }

            List li = new ArrayList();
            li.add(ratio);
            li.add(type);
            li.add(scrip);
            li.add(sum2011);
            li.add(sum2012);
            li.add(sum2013);
            li.add(sum2014);
            li.add(sum2015);
            li.add(sum2016);
            li.add(sum2017);
            csvList.add(li);
            csvList.add(addAsPercentFromBaseYear(startYear, li, Strings.MARKET_CAP_PERCENTAGE));
        }
        return csvList;

    }

    public List addAsPercentFromBaseYear(Integer baseYear, List li, String newRatio){
        int index = baseYear-2011+3;
        List percentList = new ArrayList();

        percentList.add(newRatio);
        percentList.add(li.get(1));
        percentList.add(li.get(2));

        for(int i=3; i<index; i++){
            percentList.add(null);
        }

        percentList.add(100.0f);

        for(int i=index+1; i<10; i++){
            Float base = (Float)li.get(index);
            Float current = (Float)li.get(i);

            if(current==null || base==null){
                continue;
            }
            percentList.add(100f*current/base);
        }

        return percentList;

    }
}

