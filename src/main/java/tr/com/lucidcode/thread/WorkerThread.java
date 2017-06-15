package tr.com.lucidcode.thread;

import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.scripts.WebScrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WorkerThread implements Runnable {
    private StockSymbols ss;


    public WorkerThread(StockSymbols ss){
        this.ss=ss;
    }


    public void run() {
        new WebScrapper().scrapeScrip(ss);
    }

}