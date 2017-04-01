package portfolio.dao;

import portfolio.model.Position;

import java.util.List;

/**
 * Created by vincenttam on 3/18/17.
 */
public interface PortfolioProvider {

    // read the initial list of position from the portfolio
    List<Position> getPortfolio();
}
