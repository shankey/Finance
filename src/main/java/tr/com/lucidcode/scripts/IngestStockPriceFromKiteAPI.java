package tr.com.lucidcode.scripts;

import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.HistoricalData;
import com.rainmatter.models.Instrument;
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

    public static void main(String args[]) {


        kiteconnect = new KiteConnect("djl7dkctcoioiijb");

        kiteconnect.setUserId("ZP2315");

        String url = kiteconnect.getLoginUrl();
        System.out.println(url);

        kiteconnect.setAccessToken("de143us0byppcp3y460wxh7kuzf89mwo");
        kiteconnect.setPublicToken("676653312f62378ec304492d324bf350");


        try {
            new IngestStockPriceFromKiteAPI().getAllFiles();
        } catch (KiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

//        for (int i = 0; i < listOfFiles.length; i++) {
//            if (listOfFiles[i].isFile()) {
//                System.out.println("File " + listOfFiles[i].getName());
//
//                if(listOfFiles[i].getName().equals(".DS_Store")){
//                    continue;
//                }
//
//                List<StockPrice> stockPricesList = new ArrayList<StockPrice>();
//                String scrip = getScripFromFilename(listOfFiles[i].getName());
//                Integer bseId = ServiceDispatcher.getStockSymbolsService().getBseIdFromScrip(scrip);
//
//                try {
//                    File file = listOfFiles[i];
//                    FileReader fileReader = new FileReader(file);
//                    BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//
//                    String line;
//                    int j=0;
//                    while ((line = bufferedReader.readLine()) != null) {
//
//                        if(j==0){
//                            j=1;
//                            continue;
//                        }
//
//                        String candle[] = line.split(",");
//
//
//                        StockPrice sp = new StockPrice();
//
//                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                        try{
//
//
//                            sp.setBseId(bseId);
//                            sp.setDate(formatter.parse(candle[0]));
//                            sp.setOpen(Float.parseFloat(candle[1]));
//                            sp.setHigh(Float.parseFloat(candle[2]));
//                            sp.setLow(Float.parseFloat(candle[3]));
//                            sp.setClose(Float.parseFloat(candle[4]));
//                            sp.setVolume(Integer.parseInt(candle[6]));
//                            stockPricesList.add(sp);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                    }
//                    ServiceDispatcher.getStockPriceService().insert(bseId, stockPricesList);
//                    fileReader.close();
//                    FileUtils.moveFile(listOfFiles[i].getAbsolutePath(), "/Users/adinema/Documents/old_prices/");
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

    }
}
