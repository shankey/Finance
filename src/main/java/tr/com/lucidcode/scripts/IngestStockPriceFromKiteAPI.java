package tr.com.lucidcode.scripts;

import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.HistoricalData;
import com.rainmatter.models.Instrument;
import com.rainmatter.models.UserModel;
import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.model.StockPrice;
import tr.com.lucidcode.util.FileUtils;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class IngestStockPriceFromKiteAPI {

    public static ZerodhaWrapper zw = new ZerodhaWrapper();
    public static KiteConnect kiteconnect = null;

    public static void main(String args[]) throws KiteException {

        if(args==null || args[0]==null || args[0]=="" || args[1]==null || args[1]==""){
            return;
        }

        kiteconnect = new KiteConnect("djl7dkctcoioiijb");
        kiteconnect.setUserId("ZP2315");

        kiteconnect.setAccessToken(args[0]);
        kiteconnect.setPublicToken(args[1]);

        new IngestStockPriceFromKiteAPI().ingestAllIndustriesPrice();

    }

    public void ingestAllIndustriesPrice(){
        List<String> industries = ServiceDispatcher.getMoneyControlScripService().getAllIndustries();
        for(String industry: industries){

            try {
                ingestAllScripsForIndustry(industry);
            } catch (KiteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveData(HistoricalData hds, Integer bseId){

        List<StockPrice> stockPricesList = new ArrayList<StockPrice>();
        System.out.println("No of rows found = " + hds.dataArrayList.size());

        for(HistoricalData hd: hds.dataArrayList){


            StockPrice sp = new StockPrice();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try{

                sp.setBseId(bseId);
                sp.setDate(formatter.parse(hd.timeStamp));
                sp.setOpen(Float.parseFloat(hd.open+""));
                sp.setHigh(Float.parseFloat(hd.high+""));
                sp.setLow(Float.parseFloat(hd.low+""));
                sp.setClose(Float.parseFloat(hd.close+""));
                sp.setVolume(Integer.parseInt(hd.volume+""));
                stockPricesList.add(sp);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        ServiceDispatcher.getStockPriceService().insert(bseId, stockPricesList);
    }

    public void ingestAllScripsForIndustry(String industry) throws KiteException, IOException {
        List<MoneyControlScrips> mcsList = ServiceDispatcher.getMoneyControlScripService().getAllByIndustry("mediaentertainment");
        Map<Long, Instrument> scripInstrumentMap = zw.getAllInstruments(kiteconnect);

        for(MoneyControlScrips mcs: mcsList){
            if(mcs.getBseId()==null || mcs.getNseId()==null){
                continue;
            }

            System.out.println(mcs.getName() + " - " +mcs.getNseId());

            Instrument ins = scripInstrumentMap.get(Long.parseLong(mcs.getBseId().toString()));

            if(ins == null){
                continue;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                HistoricalData hds = zw.getHistoricalData(kiteconnect, null, null, ins.getInstrument_token());
                saveData(hds, mcs.getBseId());
                ServiceDispatcher.getMoneyControlScripService().setPriceStatus(mcs.getId(), 1);
            } catch (KiteException e) {

                e.printStackTrace();
            }
        }
    }
}
