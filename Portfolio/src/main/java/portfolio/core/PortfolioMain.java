package portfolio.core;

import portfolio.dao.*;
import portfolio.publisher.DataPublisher;
import portfolio.publisher.RealTimeDataPublisher;
import portfolio.subscriber.ConsoleDataSubscriber;
import portfolio.subscriber.DataSubscriber;
import portfolio.subscriber.TextFilePrinterSubscriber;

/**
 * Created by vincenttam on 3/18/17.
 */
public class PortfolioMain {

    public static void main(String[] args) {

        // reference data on supported instruments from H2
        RefDataSource refDataSource = new H2RefDataSource();

        MarketDataProvider dataProvider = new MockMarketDataProvider(refDataSource);

        // PortfolioProvider portfolioProvider = new MockPortfolioProvider();
        PortfolioProvider portfolioProvider = new FilePortfolioProvider(refDataSource);

        DataPublisher publisher = new RealTimeDataPublisher(portfolioProvider);

        dataProvider.addPublisher(publisher);

        DataSubscriber consoleSub = new ConsoleDataSubscriber();
        TextFilePrinterSubscriber textFilePrinterSub = new TextFilePrinterSubscriber();

        publisher.addSubscriber(consoleSub);
        publisher.addSubscriber(textFilePrinterSub);

        textFilePrinterSub.printReport();

        // simulate update market data updates for 30 seconds
        dataProvider.sendUpdates(30);

        textFilePrinterSub.printReport();
    }
}
