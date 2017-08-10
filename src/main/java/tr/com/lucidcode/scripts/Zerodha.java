package tr.com.lucidcode.scripts;

import com.neovisionaries.ws.client.WebSocketException;
import com.rainmatter.kitehttp.SessionExpiryHook;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.*;
import org.apache.http.HttpHost;
import org.json.JSONException;
import org.json.JSONObject;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.ticker.OnConnect;
import com.rainmatter.ticker.OnDisconnect;
import com.rainmatter.ticker.OnTick;
import com.rainmatter.ticker.KiteTicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sujith on 7/10/16.
 * This class has example of how to initialize kiteSdk and make rest api calls to place order, get orders, modify order, cancel order,
 * get positions, get holdings, convert positions, get instruments, logout user, get historical data dump, get trades
 */
public class Zerodha {

    public static void main(String[] args){
        try {
            // First you should get request_token, public_token using kitconnect login and then use request_token, public_token, api_secret to make any kiteconnect api call.
            // Initialize KiteSdk with your apiKey.
            KiteConnect kiteconnect = new KiteConnect("djl7dkctcoioiijb");

            // set userId
            kiteconnect.setUserId("ZP2315");



            // Get login url
            String url = kiteconnect.getLoginUrl();
            System.out.println(url);

            // Set session expiry callback.
//            kiteconnect.registerHook(new SessionExpiryHook() {
//                @Override
//                public void sessionExpired() {
//                    System.out.println("session expired");
//                }
//            });

            // Set request token and public token which are obtained from login process.
            UserModel userModel =  kiteconnect.requestAccessToken("zamrmuakt8yxuv7s4f48uy2g72xjdxkf", "lw1w0uu962fjibes6kt7fbovvwust7oy");

            System.out.println(userModel.accessToken + " " + userModel.publicToken + " accessed data");
            kiteconnect.setAccessToken("nauunbma5et4hj9cl2f2lh1xs2tzgorh");
            kiteconnect.setPublicToken("c0e9d3078fea57681b13f1bcb65aebca");

            ZerodhaWrapper zp = new ZerodhaWrapper();
            zp.getAllInstruments(kiteconnect);
            //zp.getHistoricalData(kiteconnect, null, null, 0);


        } catch (KiteException e) {
            e.printStackTrace();
            System.out.println(e.message);
        } catch (JSONException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}