package portfolio.dao;

import portfolio.model.CallOption;
import portfolio.model.Position;
import portfolio.model.PutOption;
import portfolio.model.Stock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincenttam on 3/18/17.
 */
public class MockPortfolioProvider implements PortfolioProvider {

    Stock applStock = new Stock("AAPL", 140, 0.1, 0.02);
    CallOption applCall = new CallOption(applStock.clone(), 5, 145);
    PutOption applPut = new PutOption(applStock.clone(), 3, 130);

    Stock tslaStock = new Stock("TSLA", 260, 0.15, 0.05);
    CallOption tslaCall = new CallOption(tslaStock.clone(), 5, 275);
    PutOption tslaPut = new PutOption(tslaStock.clone(), 3, 250);

    public List<Position> getPortfolio() {

        List<Position> portfolio = new ArrayList<>();
        portfolio.add(new Position(applStock, 10000, true));
        portfolio.add(new Position(applCall, 10000, true));
        portfolio.add(new Position(applPut, 10000, false));

        portfolio.add(new Position(tslaStock, 5000, true));
        portfolio.add(new Position(tslaCall, 5000, false));
        portfolio.add(new Position(tslaPut, 5000, true));

        return portfolio;
    }
}
