package tr.com.lucidcode.scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import tr.com.lucidcode.model.MoneyControlScrips;
import tr.com.lucidcode.util.FileUtils;
import tr.com.lucidcode.util.ServiceDispatcher;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MoneyControlScrapper2 {

    ProfilesIni profile = new ProfilesIni();
    FirefoxProfile ffprofile = profile.getProfile("Selenium");
    WebDriver driver = new FirefoxDriver(ffprofile);


    public static String CONS_QUARTERLY = "cons_quarterly";
    public static String CONS_YEARLY = "cons_yearly";
    public static String CONS_PROFIT = "profit_cons";
    public static String CONS_BALANCE = "balance_cons";
    public static String CONS_CASHFLOW = "cashflow_VI";
    public static String CONS_RATIOS = "cons_keyfinratio";

    public static String SA_QUARTERLY = "quarterly";
    public static String SA_YEARLY = "yearly";
    public static String SA_PROFIT = "profit";
    public static String SA_BALANCE = "balance";
    public static String SA_CASHFLOW = "cashflow";
    public static String SA_RATIOS = "keyfinratio";


    public String financialsTemplate = "http://www.moneycontrol.com/financials/oclindia/profit-lossVI/OCL#OCL";

    String stockFindTemplate = "http://www.moneycontrol.com/india/stockpricequote/%character";

    String baseFolder = "/Users/adinema/Documents/MoneyControl/";

    public void getAllScrips(){

//        Capabilities caps = new DesiredCapabilities();
//
//        ((DesiredCapabilities) caps).setJavascriptEnabled(true);
//
//
//        ((DesiredCapabilities) caps).setCapability(
//                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
//                "/Users/adinema/Documents/Finance/phantomjs/bin/phantomjs"
//
//        );
//
//        driver = new PhantomJSDriver(caps);



        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);


        driver.navigate().to(financialsTemplate);
        sleep(500);

        if(driver.findElements(By.cssSelector(".boxBg")).size()<=0){
            return;
        }
        WebElement element = driver.findElement(By.cssSelector(".boxBg"));
        System.out.println(element.getAttribute("innerHTML"));



//        List<MoneyControlScrips> list = ServiceDispatcher.getMoneyControlScripService().getAllByIndustry("financehousing");
//        System.out.println(list);


//        for(MoneyControlScrips mcs: list){
//            if(mcs.getStatus() != null && mcs.getStatus()==1){
//                continue;
//            }
//            System.out.println("-----NEXT SCRIP------ " + mcs.getName());
//            scrapeScrip(mcs);
//        }
    }

    public void scrapeScrip(MoneyControlScrips mcs){
        String scripFolder = baseFolder+mcs.getName();
        FileUtils.createFolder(scripFolder);
        getScripIds(mcs);

        String[] splits = mcs.getUrl().split("/");
        String scrip = splits[7];

        String templateWithScrip = financialsTemplate.replaceAll("%scrip", scrip);

        scrapeHTML(templateWithScrip, CONS_YEARLY, scrip, scripFolder);
        scrapeHTML(templateWithScrip, CONS_PROFIT, scrip, scripFolder);
        scrapeHTML(templateWithScrip, CONS_BALANCE, scrip, scripFolder);
        scrapeHTML(templateWithScrip, CONS_CASHFLOW, scrip, scripFolder);
        scrapeHTML(templateWithScrip, CONS_RATIOS, scrip, scripFolder);

        scrapeHTML(templateWithScrip, SA_YEARLY, scrip, scripFolder);
        scrapeHTML(templateWithScrip, SA_PROFIT, scrip, scripFolder);
        scrapeHTML(templateWithScrip, SA_BALANCE, scrip, scripFolder);
        scrapeHTML(templateWithScrip, SA_CASHFLOW, scrip, scripFolder);
        scrapeHTML(templateWithScrip, SA_RATIOS, scrip, scripFolder);

        scrapeQuarterlyHTML(templateWithScrip, CONS_QUARTERLY, scrip, scripFolder);
        scrapeQuarterlyHTML(templateWithScrip, SA_QUARTERLY, scrip, scripFolder);

        ServiceDispatcher.getMoneyControlScripService().setStatus(mcs.getId(), 1);

    }

    public void scrapeHTML(String template , String reportType, String scrip, String scripFolder){
        String url = template.replaceAll("%financial_type", reportType);
        driver.navigate().to(url);
        sleep(500);

        if(driver.findElements(By.cssSelector(".boxBg")).size()<=0){
            return;
        }
        WebElement element = driver.findElement(By.cssSelector(".boxBg"));
        System.out.println(element.getAttribute("innerHTML"));

        FileUtils.createFile(scripFolder + "/" +reportType + ".txt", element.getAttribute("innerHTML"));

    }

    public void scrapeQuarterlyHTML(String template , String reportType, String scrip, String scripFolder){
        String url = template.replaceAll("%financial_type", reportType);
        driver.navigate().to(url);

        WebElement qtr1 = null;
        if(driver.findElements(By.cssSelector(".boxBg")).size() <= 0){
            return;
        }

        qtr1 = driver.findElement(By.cssSelector(".boxBg"));
        System.out.println(qtr1.getAttribute("innerHTML"));


        FileUtils.createFile(scripFolder + "/" + reportType + ".txt", qtr1.getAttribute("innerHTML"));

        for(int i=2; i<=5; i++){
            System.out.println("Quarter "+i);
            if(driver.findElements(By.xpath("//a[@class='prevnext']/b[contains(text(), 'Previous')]")).size() > 0) {
                WebElement prevYear = driver.findElement(By.xpath("//a[@class='prevnext']/b[contains(text(), 'Previous')]"));
                prevYear.click();
                sleep(500);

                if(driver.findElements(By.cssSelector(".boxBg")).size()<=0){
                    return;
                }
                WebElement qtr = driver.findElement(By.cssSelector(".boxBg"));
                System.out.println(qtr.getAttribute("innerHTML"));

                FileUtils.createFile(scripFolder + "/" +reportType + i + ".txt", qtr.getAttribute("innerHTML"));
            }
        }

    }

    private void getScripIds(MoneyControlScrips mcs){
        driver.navigate().to(mcs.getUrl());
        WebElement scripDetailsElement=null;
        if(driver.findElements(By.cssSelector(".PB10 > .FL.gry10")).size() > 0){
            scripDetailsElement = driver.findElement(By.cssSelector(".PB10 > .FL.gry10"));
        }else {
            return;
        }

        String scripDetailsString = scripDetailsElement.getText();
        System.out.println("scrip details element"+ scripDetailsElement.getText());

        String[] scripDetails = scripDetailsString.split("\\|");


        for(int i=0; i<scripDetails.length; i++){
            System.out.println(scripDetails[i]);
        }

        try{
            Integer bseId = Integer.parseInt(scripDetails[0].split(":")[1].trim());
            mcs.setBseId(bseId);
        }catch (Exception e){
            e.printStackTrace();
        }
            String nseId = scripDetails[1].split(":")[1].trim();
            String sector = scripDetails[3].split(":")[1].trim();


            mcs.setNseId(nseId);
            mcs.setSector(sector);



        ServiceDispatcher.getMoneyControlScripService().insert(mcs);

        //save details in DB here
        //BSE: 500410 | NSE: ACC | ISIN: INE012A01025 | SECTOR: CEMENT - MAJOR |

    }

    private void sleep(Integer sleepTime){
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            System.out.println("Sleep Interrupted");
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




    @PostConstruct
    public static void main(String[] args) throws IOException {
        MoneyControlScrapper2 stockPriceScrapper = new MoneyControlScrapper2();

        stockPriceScrapper.getAllScrips();


        stockPriceScrapper.closeBrowser();

    }
}