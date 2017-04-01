package portfolio.publisher;

import portfolio.subscriber.DataSubscriber;
import portfolio.core.Market;
import portfolio.dao.PortfolioProvider;
import portfolio.model.Instrument;
import portfolio.model.Position;
import portfolio.model.Stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vincenttam on 3/18/17.
 */
public class RealTimeDataPublisher implements DataPublisher {

    private PortfolioProvider _portfolioProvider;
    private List<Position> _portfolio = new ArrayList<>();
    private List<DataSubscriber> _subscribers = new ArrayList<>();

    public RealTimeDataPublisher(PortfolioProvider portfolioProvider) {
        _portfolioProvider = portfolioProvider;
        _portfolio = _portfolioProvider.getPortfolio();
    }

    // hold list of subscriber with call back
    public void addSubscriber(DataSubscriber sub) {
        _subscribers.add(sub);
        sub.addPublisher(this);
    }

    public void updateStock(Stock s) {
        for(Position p: _portfolio) {
            Instrument inst = p.getInstrument();
            if(inst.sameUnderlying(s) ) {
                System.out.println(inst.toString() + " got update from " + s.toString());
                inst.updateFromStockPrice(s.price(), Market.riskFreeRate);
                updateSubscriber();
            }
        }
    }

    // public api for subscribes
    public double portfolioNAV() {
        double value = 0.0;
        for(Position position: _portfolio) {
            value += position.marketValue();
        }
        return value;
    }

    public Map<Position, Double> positionMarketValues() {
        Map<Position, Double> result = new HashMap<>();
        for(Position position : _portfolio) {
            result.put(position, position.marketValue());
        }
        return result;
    }

    private void updateSubscriber() {
        for(DataSubscriber subscriber : _subscribers) {
            subscriber.updateNAV( portfolioNAV() );
        }
    }
}
