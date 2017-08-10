package tr.com.lucidcode.mutualfund;

import tr.com.lucidcode.util.ServiceDispatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adinema on 04/08/17.
 */
public class DailyNAVScraper {

    public static void main(String args[]){

        new DailyNAVScraper().hitUrl();

    }

    public void hitUrl(){
        try {
            // get URL content
            URL url = new URL("https://www.amfiindia.com/spages/NAVOpen.txt");
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;

            Integer lineNo = 0;

            while ((inputLine = br.readLine()) != null) {
                lineNo++;

                if(lineNo==1){

                    continue;

                }

                if(inputLine.equals("")){
                    continue;
                }


                if(inputLine.contains("Open Ended Schemes")) {


                    //Mutual Fund Type
                    System.out.println("Scheme Type = " + inputLine);
                    String schemeType = inputLine;

                    Integer end=0;
                    while ((inputLine = br.readLine()) != null) {



                        if (inputLine.equals("")) {
                            if(end==1){
                                break;
                            }
                            end=0;
                            continue;
                        }

                        //Mutual Fund House
                        System.out.println("Fund house = " + inputLine);
                        String fundHouse = inputLine;

                        Integer schemeLineNo = 0;

                        while ((inputLine = br.readLine()) != null) {
                            end=0;
                            schemeLineNo++;
                            if (schemeLineNo == 1 && inputLine.equals("")) {
                                continue;
                            }

                            if (inputLine.equals("")) {
                                end=1;
                                break;
                            }

                            System.out.println("NAV = " + inputLine);
                            String[] fundDetails = inputLine.split(";");

                            Integer schemeCode = Integer.parseInt(fundDetails[0]);
                            String isin = fundDetails[1];
                            String schemeName = fundDetails[3];
                            if(fundDetails[4]!=null && !fundDetails[4].contains("N.A.")){
                                Float nav = Float.parseFloat(fundDetails[4]);
                            }

                            if(fundDetails[5]!=null && !fundDetails[5].contains("N.A.")){
                                Float repurchasePrice = Float.parseFloat(fundDetails[5]);
                            }

                            if(fundDetails[6]!=null && !fundDetails[6].contains("N.A.")){
                                Float salePrice = Float.parseFloat(fundDetails[6]);
                            }

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                            Date date = null;
                            try {
                                date = sdf.parse(fundDetails[7]);
                                ServiceDispatcher.getMutualFundsService().insert(schemeType, fundHouse, schemeCode, isin, schemeName);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }


                    }


                }

            }
            br.close();

            System.out.println("Done");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
