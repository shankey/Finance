package tr.com.lucidcode.scripts;

import tr.com.lucidcode.model.StockPrice;
import tr.com.lucidcode.util.FileUtils;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adinema on 15/06/17.
 */
public class IngestStockPrice {

    public static void main(String args[]){
        new IngestStockPrice().getAllFiles();
    }

    public String getScripFromFilename(String fileName){
        return fileName.split("\\.")[0];
    }

    public void getAllFiles(){
        File folder = new File("/Users/adinema/Documents/prices/");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());

                if(listOfFiles[i].getName().equals(".DS_Store")){
                    continue;
                }

                List<StockPrice> stockPricesList = new ArrayList<StockPrice>();
                String scrip = getScripFromFilename(listOfFiles[i].getName());
                Integer bseId = ServiceDispatcher.getStockSymbolsService().getBseIdFromScrip(scrip);

                try {
                    File file = listOfFiles[i];
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);


                    String line;
                    int j=0;
                    while ((line = bufferedReader.readLine()) != null) {

                        if(j==0){
                            j=1;
                            continue;
                        }

                        String candle[] = line.split(",");


                        StockPrice sp = new StockPrice();

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        try{


                            sp.setBseId(bseId);
                            sp.setDate(formatter.parse(candle[0]));
                            sp.setOpen(Float.parseFloat(candle[1]));
                            sp.setHigh(Float.parseFloat(candle[2]));
                            sp.setLow(Float.parseFloat(candle[3]));
                            sp.setClose(Float.parseFloat(candle[4]));
                            sp.setVolume(Integer.parseInt(candle[6]));
                            stockPricesList.add(sp);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    ServiceDispatcher.getStockPriceService().insert(bseId, stockPricesList);
                    fileReader.close();
                    FileUtils.moveFile(listOfFiles[i].getAbsolutePath(), "/Users/adinema/Documents/old_prices/");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
