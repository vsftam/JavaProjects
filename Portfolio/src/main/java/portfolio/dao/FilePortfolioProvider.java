package portfolio.dao;

import portfolio.model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by vincenttam on 3/19/17.
 */
public class FilePortfolioProvider implements PortfolioProvider {

    private final String filename = "/portfolio.csv";
    private Set<Option> options = new HashSet<>();
    private Set<Stock> stocks = new HashSet<>();
    private RefDataSource _refDataSource = null;

    public FilePortfolioProvider(RefDataSource refDataSource) {
        _refDataSource = refDataSource;
        getReferenceData();
    }

    private void getReferenceData() {
        Connection conn = _refDataSource.getConnection();
        try {
            options = _refDataSource.loadSupportedOptions(conn);
            stocks = _refDataSource.loadSupportedStocks(conn);
        }
        catch(SQLException e) {
            System.out.println("Cannot get instrument data from H2: " + e);
            e.printStackTrace();
            System.exit(1);
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch(SQLException sqle) {
                    System.out.println("Error closing connection: " );
                }
            }
        }
    }

    public List<Position> getPortfolio() {

        List<Position> portfolio = new ArrayList<>();

        InputStream is = getClass().getResourceAsStream(filename);
        System.out.println("Reading resouce for " + filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            // skip header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line " + line);
                portfolio.add(parsePosition(line));
            }
        }
        catch(Exception e) {
            System.out.println("Cannot generate portfolio from " + filename + " :" +e);
            e.printStackTrace();
            System.exit(1);
        }

        return portfolio;
    }

    private Position parsePosition(String line) {
        String[] tokens = line.split(",");
        String ticker = tokens[0];
        InstrumentType type = InstrumentType.from(tokens[1]);
        Instrument instrument = getInstrument(ticker, type);

        int quantity = Integer.parseInt(tokens[2]);
        boolean longPosition = quantity >= 0 ? true : false;
        quantity = Math.abs(quantity);

        return new Position(instrument, quantity, longPosition);
    }

    private Instrument getInstrument(String ticker, InstrumentType type) {
        Instrument instrument = null;
        switch(type) {
            case Stock:
                for(Stock stock: stocks) {
                    if(stock.getTicker().equals(ticker))
                        instrument = stock;
                }
            case PutOption:
            case CallOption:
                for(Option option : options) {
                    if( option.getType().equals(type) && option.getTicker().equals(ticker) )
                        instrument = option;
                }
        }
        if(instrument == null)
            throw new NoSuchElementException("No support for instrument with ticker " + ticker + " and type " + type);
        else
            return instrument;
    }
}
