package portfolio.dao;

import portfolio.publisher.DataPublisher;


/**
 * Created by vincenttam on 3/18/17.
 */
public interface MarketDataProvider {

    void addPublisher(DataPublisher publisher);

    void sendUpdates(double durationInSeconds);
}
