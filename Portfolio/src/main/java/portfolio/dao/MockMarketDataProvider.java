package portfolio.dao;

import portfolio.core.Market;
import portfolio.publisher.DataPublisher;
import portfolio.model.Stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


/**
 * Created by vincenttam on 3/18/17.
 */
public class MockMarketDataProvider implements MarketDataProvider {

    private RefDataSource _refDataSource = null;

    Random ran = new Random();

    public Set<Stock> stocks = new HashSet<>();
    Stock[] stockArray = null;
    /*
    Stock applStock = new Stock("AAPL", 140, 0.1, 0.02);
    Stock tslaStock = new Stock("TSLA", 260, 0.15, 0.05);
    Stock amznStock = new Stock("AMZN", 852, 0.05, 0.01);
    */
    private List<DataPublisher> publishers = new ArrayList<>();

    public MockMarketDataProvider(RefDataSource refDataSource) {
        //stocks.add(applStock);
        //stocks.add(tslaStock);
        //stocks.add(amznStock);
        _refDataSource = refDataSource;
        getReferenceData();
    }

    public void getReferenceData() {
        Connection conn = _refDataSource.getConnection();
        try {
            stocks = _refDataSource.loadSupportedStocks(conn);
            stockArray = stocks.toArray(new Stock[stocks.size()]);
        }
        catch(SQLException e) {
            System.out.println("Cannot get instrument data from H2: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void addPublisher(DataPublisher publisher) {
        publishers.add(publisher);
    }

    // process to keep sending stock updates until duration has passed
    public void sendUpdates(double durationInSeconds) {
        double t = 0.0;
        while(t < durationInSeconds) {
            double elapsed = elapsedTime();
            Stock s = updateStockPrice(elapsed);

            System.out.println("After " + elapsed + " seconds, got " + s.toString() );

            notifyPublishers(s);
            t += elapsed;
        }
        System.out.format("Finish sendUpdates after %.1f seconds%n", durationInSeconds);
    }


    private void notifyPublishers(Stock s) {
        for(DataPublisher pub : publishers) {
            pub.updateStock(s);
        }
    }

    private double elapsedTime() {
        return Market.stockUpdateTimeLowerBound + (Market.stockUpdateTimeUpperBound - Market.stockUpdateTimeLowerBound) * ran.nextDouble();
    }

    private Stock pickStock() {
        int next = ran.nextInt(stocks.size());
        return stockArray[next];
    }

    private Stock updateStockPrice(double t) {
        Stock s = pickStock();
        double v = s.newPrice(t);
        s.setPrice(v);
        return s;
    }
}
