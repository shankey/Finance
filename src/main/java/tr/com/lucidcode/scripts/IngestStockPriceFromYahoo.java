package tr.com.lucidcode.scripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.HistoricalData;
import com.rainmatter.models.Instrument;
import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.model.StockPrice;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class IngestStockPriceFromYahoo {

    static String query = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=%symbol.BO&apikey=EO3WCG8GI9CN9PPU&outputsize=full";

    public static void main(String args[]) throws KiteException {



        new IngestStockPriceFromYahoo().ingestAllIndustriesPrice();

    }

    public void ingestAllIndustriesPrice(){
        List<String> industries = ServiceDispatcher.getMoneyControlScripService().getAllIndustries();
        for(String industry: industries){
            try {
                ingestAllScripsForIndustry(industry);
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

    public void ingestAllScripsForIndustry(String industry) throws  IOException {
        List<MoneyControlScrips> mcsList = ServiceDispatcher.getMoneyControlScripService().getAllByIndustry(industry);

        if(mcsList == null){
            System.out.println("McsList Null for industry " + industry);
            return;
        }

        for(MoneyControlScrips mcs: mcsList){
            System.out.println(mcs);
            if(mcs == null || mcs.getBseId()==null || mcs.getNseId()==null || mcs.getPriceStatus()==1){
                continue;
            }

            System.out.println(mcs.getName() + " - " +mcs.getNseId());
            String fQuery = query.replaceAll("%symbol", mcs.getNseId());
            try {
                URL url = new URL(fQuery);
                HttpURLConnection request = null;
                request = (HttpURLConnection) url.openConnection();
                request.connect();

                // Convert to a JSON object to print data
                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));

                if(root==null){
                    continue;
                }
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
//
                System.out.println(rootobj);
                System.out.println(rootobj.get("Monthly Time Series"));
                JsonObject timeSeriesData = rootobj.getAsJsonObject("Monthly Time Series");
                System.out.println(timeSeriesData);

                if(timeSeriesData == null){
                    continue;
                }


                HistoricalData hds = new HistoricalData();
                for(Entry<String, JsonElement> jo: timeSeriesData.entrySet()){
                    JsonElement value = jo.getValue();
                    String key = jo.getKey();
                    JsonObject innerjo = value.getAsJsonObject();

                    Float open = innerjo.get("1. open").getAsFloat();
                    Float high = innerjo.get("2. high").getAsFloat();
                    Float low = innerjo.get("3. low").getAsFloat();
                    Float close = innerjo.get("4. close").getAsFloat();
                    Long volume = innerjo.get("5. volume").getAsLong();

                    HistoricalData hd = new HistoricalData();
                    hd.open = open;
                    hd.close = close;
                    hd.high = high;
                    hd.low = low;
                    hd.volume = volume;
                    hd.timeStamp = key;
                    hds.dataArrayList.add(hd);

                    System.out.println(open + " " + high + " " + low + " " + close + " " + volume);


                }

                saveData(hds, mcs.getBseId());
                ServiceDispatcher.getMoneyControlScripService().setPriceStatus(mcs.getId(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
