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

public class WebScrapper {

    ProfilesIni profile = new ProfilesIni();
    FirefoxProfile ffprofile = profile.getProfile("Selenium");
    WebDriver driver = new FirefoxDriver(ffprofile);
    StockSymbols ss = null;

    String urlTemplate = "http://www.bseindia.com/corporates/Results.aspx?Code=%code&Company=%company&qtr=%qtr&RType=";


    public void scrapeScrip(StockSymbols ss){
        this.ss = ss;

        String url = urlTemplate.replaceFirst("%code",ss.getBseId());
        url = url.replaceFirst("%company", URLEncoder.encode(ss.getCompany()));
        generateAndScrapeUrls(url, ss.getStatus());
        System.out.println("Before closing browser " + ss.getCompany());
        closeBrowser();
    }

    public void generateAndScrapeUrls(String urlTop, Float startValue){
        int index = (int)Math.floor(startValue);
        int i = 0;
        for(i=index; i<95; i++){

            String url = urlTop.replaceAll("%qtr", i+"");
            scrape(url, new Float(i));
            if(i%4==0){
                url = urlTop.replaceAll("%qtr", (i+1.5)+"");
                scrape(url, new Float(i+1.5));
            }
        }

        ServiceDispatcher.getStockSymbolsService().setStatus(ss.getId(), new Float(i));

    }

    public void scrape(String url, Float index){
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.navigate().to(url);
        if(driver.findElements(By.id("ctl00_ContentPlaceHolder1_lnkDetailed")).size() > 0){
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_lnkDetailed")).click();
        }

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            Thread.sleep(1000);
            ServiceDispatcher.getStockSymbolsService().setStatus(ss.getId(), index);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(driver.findElements(By.id("ctl00_ContentPlaceHolder1_lnkDownload")).size() > 0) {
            WebElement excel = driver.findElement(By.id("ctl00_ContentPlaceHolder1_lnkDownload"));
            excel.click();
        }
        try {
            Thread.sleep(2000);
            ServiceDispatcher.getStockSymbolsService().setStatus(ss.getId(), index);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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


    //Not used in threadpool
    public void saveScreenshot() throws IOException {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("screenshot.png"));
    }

    // Not used in threadpool
    public void getAllCompaniesAndScrape(){
        List<StockSymbols> stockSymbolsList = ServiceDispatcher.getStockSymbolsService()
                .getStocks("Cement &amp; Cement Products");

        for(StockSymbols ss: stockSymbolsList){
            String url = urlTemplate.replaceFirst("%code",ss.getBseId());
            url = url.replaceFirst("%company", URLEncoder.encode(ss.getCompany()));
            generateAndScrapeUrls(url, new Float(50.0));
        }
    }

    @PostConstruct
    public static void main(String[] args) throws IOException {
        WebScrapper webSrcapper = new WebScrapper();
        //webSrcapper.setupFireFox();
        webSrcapper.getAllCompaniesAndScrape();

//		webSrcapper.login("admin", "12345");
//		webSrcapper.getText();
//		webSrcapper.saveScreenshot();
        webSrcapper.closeBrowser();
    }
}