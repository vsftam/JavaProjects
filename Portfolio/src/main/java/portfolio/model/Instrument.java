package portfolio.model;

/**
 * Created by vincenttam on 3/18/17.
 */
public interface Instrument {

    InstrumentType getType();

    boolean sameUnderlying(Stock s);

    void setPrice(double price);

    void updateFromStockPrice(double stockPrice, double rate);

    double price();

    String reportString();

    String toString();
}
