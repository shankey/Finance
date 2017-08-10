package tr.com.lucidcode.scripts;

import com.neovisionaries.ws.client.WebSocketException;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.*;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.ticker.KiteTicker;
import com.rainmatter.ticker.OnConnect;
import com.rainmatter.ticker.OnDisconnect;
import com.rainmatter.ticker.OnTick;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by sujith on 15/10/16.
 */
public class ZerodhaWrapper {


    /** Get historical data for an instrument.*/
    public HistoricalData getHistoricalData(KiteConnect kiteconnect, Date start, Date end, long token) throws KiteException {
        /** Get historical data dump, requires from and to date, intrument token, interval
         * returns historical data object which will have list of historical data inside the object*/
        Map<String, Object> param8 = new HashMap<String, Object>(){
            {
                put("from", "2012-03-01");
                put("to", "2017-07-31");
            }
        };
        HistoricalData historicalData = kiteconnect.getHistoricalData(param8, token+"", "day");


        return historicalData;

    }

    public Map<Long, Instrument> getAllInstruments(KiteConnect kiteconnect) throws KiteException, IOException {
        // Get all instruments list. This call is very expensive as it involves downloading of large data dump.
        // Hence, it is recommended that this call be made once and the results stored locally once every morning before market opening.
        List<Instrument> instruments = kiteconnect.getInstruments("BSE");
        Map<Long, Instrument> scripInstrumentMap = new HashMap<Long, Instrument>();
        for(Instrument instrument: instruments){
            System.out.println(instrument.getName() + " - " + instrument.getInstrument_token() + " - " +
                    instrument.getExchange_token() +" - " + instrument.getTradingsymbol());

            scripInstrumentMap.put(new Long(instrument.getExchange_token()), instrument);
        }

        return scripInstrumentMap;

    }


}