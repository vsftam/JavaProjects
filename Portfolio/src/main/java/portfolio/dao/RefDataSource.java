package portfolio.dao;

import portfolio.model.Option;
import portfolio.model.Stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by vincenttam on 3/19/17.
 */
public interface RefDataSource {
    Connection getConnection();
    Set<Stock> loadSupportedStocks(Connection conn) throws SQLException;
    Set<Option> loadSupportedOptions(Connection conn) throws SQLException;
}
