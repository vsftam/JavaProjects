package portfolio.dao;

import portfolio.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by vincenttam on 3/19/17.
 */
public class H2RefDataSource implements RefDataSource {

    private Connection conn;

    private Set<Stock> stocks = new HashSet<>();
    private Set<Option> options = new HashSet<>();
    private Map<String, Stock> stockMap = new HashMap<>();

    public Connection getConnection() {
        try {
            Class.forName("org.h2.Driver");
        }
        catch( ClassNotFoundException e) {
            System.out.println("Cannot find h2 driver, exiting : " + e);
            e.printStackTrace();
            System.exit(1);
        }
        if(conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:h2:./Portfolio", "sa", "");
                initTables(conn);
            } catch (SQLException sqle) {
                System.out.println("Cannot get connection:" +  sqle);
                sqle.printStackTrace();
                System.exit(1);
            }
        }
        return conn;
    }

    private void initTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "DROP TABLE STOCK;";
        stmt.executeUpdate(sql);
        sql = "DROP TABLE OPTION;";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE STOCK(TICKER VARCHAR(15), PRICE DOUBLE, EXPECTED_RETURN DOUBLE, ANNUALIZED_STD_DEV DOUBLE);";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE OPTION(TICKER VARCHAR(15), CALLPUT CHAR(1), MATURITY DOUBLE, STRIKE DOUBLE);";
        stmt.executeUpdate(sql);

        sql = "INSERT INTO STOCK VALUES('AAPL',140,0.1,0.02);";
        stmt.executeUpdate(sql);
        sql = "INSERT INTO STOCK VALUES('TSLA',260,0.15,0.05);";
        stmt.executeUpdate(sql);
        sql = "INSERT INTO STOCK VALUES('AMZN',852,0.05,0.01);";
        stmt.executeUpdate(sql);

        sql = "INSERT INTO OPTION VALUES('AAPL','C',5,145);";
        stmt.executeUpdate(sql);
        sql = "INSERT INTO OPTION VALUES('AAPL','P',3,130);";
        stmt.executeUpdate(sql);
        sql = "INSERT INTO OPTION VALUES('TSLA','C',5,275);";
        stmt.executeUpdate(sql);
        sql = "INSERT INTO OPTION VALUES('TSLA','P',3,250);";
        stmt.executeUpdate(sql);
    }


    public Set<Stock> loadSupportedStocks(Connection conn) throws SQLException {
        // return cached data
        if(stocks.size() != 0)
            return stocks;

        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM STOCK;";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()) {
            Stock s = new Stock(rs.getString("TICKER"), rs.getDouble("PRICE"), rs.getDouble("EXPECTED_RETURN"), rs.getDouble("ANNUALIZED_STD_DEV"));
            stocks.add(s);
            stockMap.put(rs.getString("TICKER"), s);
        }
        System.out.println("STOCKS from H2 returns " + stocks.size() + " rows");
        return stocks;
    }

    public Set<Option> loadSupportedOptions(Connection conn) throws SQLException {
        if(options.size() != 0)
            return options;

        // refresh stockMap if necessary
        if(stockMap.size() == 0)
            loadSupportedStocks(conn);

        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM OPTION;";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()) {
            String ticker = rs.getString("TICKER");
            InstrumentType type = InstrumentType.from(rs.getString("CALLPUT"));
            Stock s = stockMap.get(ticker);
            if(s == null) {
                throw new NoSuchElementException("Cannot find stock for ticker " + ticker);
            }
            switch(type) {
                case CallOption:
                    CallOption c = new CallOption(s, rs.getDouble("MATURITY"), rs.getDouble("STRIKE"));
                    options.add(c);
                    break;
                case PutOption:
                    PutOption p = new PutOption(s, rs.getDouble("MATURITY"), rs.getDouble("STRIKE"));
                    options.add(p);
                    break;
                default:
                    throw new NoSuchElementException("Incorrect instrument type " + type + " for option");
            }
        }
        System.out.println("OPTIONS from H2 returns " + options.size() + " rows");
        return options;
    }
}
