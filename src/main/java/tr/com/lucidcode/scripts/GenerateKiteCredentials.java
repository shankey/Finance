package tr.com.lucidcode.scripts;

import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.HistoricalData;
import com.rainmatter.models.Instrument;
import com.rainmatter.models.UserModel;
import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.model.StockPrice;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GenerateKiteCredentials {

    public static ZerodhaWrapper zw = new ZerodhaWrapper();
    public static KiteConnect kiteconnect = null;

    public static void main(String args[]) throws KiteException {


        kiteconnect = new KiteConnect("djl7dkctcoioiijb");

        kiteconnect.setUserId("ZP2315");

        String url = kiteconnect.getLoginUrl();
        System.out.println(url);

        if(args==null || args[0]==null || args[0].equals("")){
            return;
        }

        UserModel userModel =  kiteconnect.requestAccessToken(args[0], "lw1w0uu962fjibes6kt7fbovvwust7oy");

        System.out.println("Access Token = "+userModel.accessToken);
        System.out.println("Public Token = "+userModel.publicToken);

    }

    public void saveData(HistoricalData hds, Integer bseId){

        List<StockPrice> stockPricesList = new ArrayList<StockPrice>();

        for(HistoricalData hd: hds.dataArrayList){
            System.out.println(hd.timeStamp);
            System.out.println(hd.open);
            System.out.println(hd.close);
            System.out.println(hd.volume);
            System.out.println("--------------");

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

    public void getAllFiles() throws KiteException, IOException {
        List<MoneyControlScrips> mcsList = ServiceDispatcher.getMoneyControlScripService().getAllByIndustry("mediaentertainment");
        Map<Long, Instrument> scripInstrumentMap = zw.getAllInstruments(kiteconnect);

        for(MoneyControlScrips mcs: mcsList){
            if(mcs.getBseId()==null || mcs.getNseId()==null){
                continue;
            }

            System.out.println(mcs.getNseId());

            Instrument ins = scripInstrumentMap.get(Long.parseLong(mcs.getBseId().toString()));

            if(ins == null){
                continue;
            }

            System.out.println(ins.getInstrument_token());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            try {
                HistoricalData hds = zw.getHistoricalData(kiteconnect, null, null, ins.getInstrument_token());
                saveData(hds, mcs.getBseId());
            } catch (KiteException e) {

                e.printStackTrace();
            }

        }

    }
}
