package portfolio.subscriber;

import portfolio.publisher.DataPublisher;

/**
 * Created by vincenttam on 3/18/17.
 */
public interface DataSubscriber {
    void addPublisher(DataPublisher pub);

    void updateNAV(double nav);
}
