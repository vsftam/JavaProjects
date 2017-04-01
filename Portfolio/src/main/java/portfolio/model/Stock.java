package portfolio.model;

import portfolio.pricing.GeometricBrownianMotion;

/**
 * Created by vincenttam on 3/18/17.
 */
public class Stock implements Instrument{

    private String _ticker;
    private double _price;
    private double _expectedReturn;
    private double _annualizedStdDev;

    public Stock(String ticker, double price, double expectedReturn, double annualizedStdDev) {
        _ticker = ticker;
        _price = price;
        _expectedReturn = expectedReturn;
        _annualizedStdDev = annualizedStdDev;
    }

    public Stock clone() {
        return new Stock(_ticker, _price, _expectedReturn, _annualizedStdDev);
    }

    public double price() {
        return _price;
    }

    public boolean sameUnderlying(Stock s) {
        return s.equals(this);
    }

    public void setPrice(double price) {
        _price = price;
    }

    public void updateFromStockPrice(double stockPrice, double rate) {
        setPrice(stockPrice);
    }

    public InstrumentType getType() { return InstrumentType.Stock; }

    public String getTicker() { return _ticker; }

    public double newPrice(double seconds) {
        return GeometricBrownianMotion.nextValue(_price, _expectedReturn, seconds, _annualizedStdDev);
    }

    public double getAnnualizedStdDev() {
        return _annualizedStdDev;
    }

    public String reportString()  {
        return String.format("Stock\t\t%s\tPrice  %.4f\t\t\t", _ticker, _price);
    }

    @Override
    public String toString() {
        return String.format("Stock %s with Price %.4f", _ticker, _price);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Stock)) {
            return false;
        }
        else if(other == this) {
            return true;
        }
        else {
            Stock otherStock = (Stock) other;
            return otherStock._ticker.equals(this._ticker);
        }
    }

    @Override
    public int hashCode() {
        return _ticker.hashCode();
    }
}
