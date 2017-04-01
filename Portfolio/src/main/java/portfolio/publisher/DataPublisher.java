package portfolio.publisher;

import portfolio.subscriber.DataSubscriber;
import portfolio.model.Position;
import portfolio.model.Stock;

import java.util.Map;

/**
 * Created by vincenttam on 3/18/17.
 */
public interface DataPublisher {

    void addSubscriber(DataSubscriber sub);

    // callback for data provider
    void updateStock(Stock stock);

    Map<Position, Double> positionMarketValues();

    double portfolioNAV();
}
