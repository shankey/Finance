package tr.com.lucidcode.scripts;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.util.ServiceDispatcher;

import javax.annotation.PostConstruct;

public class StockPriceScrapper {

    ProfilesIni profile = new ProfilesIni();
    FirefoxProfile ffprofile = profile.getProfile("Selenium");
    WebDriver driver = new FirefoxDriver(ffprofile);
    StockSymbols ss = null;

    String urlTemplate = "https://in.finance.yahoo.com/quote/%scrip.BO/history";




    public void scrape(String url, StockSymbols ss){


        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.navigate().to(url);
        System.out.println("Try to click on dates");
        if(driver.findElements(By.cssSelector(".C\\(t\\).Bd\\(n\\).Bgc\\(t\\)")).size() > 0){ //
            System.out.println("Dates selection available");
            driver.findElement(By.cssSelector(".C\\(t\\).Bd\\(n\\).Bgc\\(t\\)")).click();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            driver.findElement(By.cssSelector(".P\\(5px\\)[data-value=\"MAX\"]")).click();
            System.out.println("clicked on max");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println("Sleep Interrupted");
                e.printStackTrace();
            }
            driver.findElement(By.cssSelector(".D\\(ib\\).Cur\\(p\\).Td\\(n\\)")).click();
            System.out.println("Clicked on done");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println("Sleep Interrupted");
                e.printStackTrace();
            }

            driver.findElement(By.cssSelector(".Bgc\\(\\$actionBlue\\).Fz\\(s\\).D\\(ib\\).Cur\\(p\\).Td\\(n\\).Fl\\(end\\)")).click();
            System.out.println("Clicked on apply");



            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println("Sleep Interrupted");
                e.printStackTrace();
            }

            driver.findElement(By.cssSelector(".Va\\(m\\)\\!.Mend\\(5px\\).Fill\\(\\$actionBlue\\)\\!.Cur\\(p\\)")).click();
            System.out.println("Clicked on download");
            ServiceDispatcher.getStockSymbolsService().setPriceStatus(ss.getId(), 1);


            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println("Sleep Interrupted");
                e.printStackTrace();
            }
        }
    }

    public void setupFireFox(){
        FirefoxProfile profile = new FirefoxProfile();

        //Download setting
        //profile.setEnableNativeEvents(true);
        //profile.setPreference("pdfjs.disabled", true);
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk","*,text/csv,application/pdf,application/vnd.ms-excel,application/docx,image/jpeg");
        profile.setPreference("browser.download.dir", "/Users/adinema/Documents/bse");
        //driver = new FirefoxDriver(profile);
    }


    public void closeBrowser() {
        driver.close();
    }



    // Not used in threadpool
    public void getAllCompaniesAndScrape(){
        List<StockSymbols> stockSymbolsList = ServiceDispatcher.getStockSymbolsService()
                .getStocks("Cement &amp; Cement Products");

        for(StockSymbols ss: stockSymbolsList){
            if(ss.getPriceStatus()==0){
                String url = urlTemplate.replaceFirst("%scrip",ss.getScrip());
                System.out.println("Url to go to " + url);
                scrape(url, ss);
            }
        }
    }

    @PostConstruct
    public static void main(String[] args) throws IOException {
        StockPriceScrapper stockPriceScrapper = new StockPriceScrapper();
        //webSrcapper.setupFireFox();
        stockPriceScrapper.getAllCompaniesAndScrape();

//		webSrcapper.login("admin", "12345");
//		webSrcapper.getText();
//		webSrcapper.saveScreenshot();
        stockPriceScrapper.closeBrowser();
    }
}