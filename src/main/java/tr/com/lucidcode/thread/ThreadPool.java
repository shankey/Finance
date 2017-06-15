package tr.com.lucidcode.thread;

import tr.com.lucidcode.model.StockSymbols;
import tr.com.lucidcode.util.ServiceDispatcher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    public void queueThreads() {

        List<StockSymbols> stockSymbolsList = getAllCompanies("Cement &amp; Cement Products");

        ExecutorService executor = Executors.newFixedThreadPool(5);//creating a pool of 5 threads
        for (StockSymbols ss: stockSymbolsList) {
            Runnable worker = new WorkerThread(ss);
            executor.execute(worker);//calling execute method of ExecutorService
        }
        executor.shutdown();

    }

    public List<StockSymbols> getAllCompanies(String industry){
        List<StockSymbols> stockSymbolsList = ServiceDispatcher.getStockSymbolsService()
                .getStocks(industry);

        return stockSymbolsList;

    }
}