package portfolio.model;

import portfolio.core.Market;

/**
 * Created by vincenttam on 3/18/17.
 */
public abstract class Option implements Instrument {

    protected Stock _stock;
    protected double _price;
    protected double _maturityInYears;
    protected double _strike;

    public Option(Stock stock, double maturityInYears, double strike) {
        _stock = stock;
        _maturityInYears = maturityInYears;
        _strike = strike;
        initOptionPrice(Market.riskFreeRate);
    }

    abstract public void initOptionPrice(double rate);

    public boolean sameUnderlying(Stock s) {
        return s.equals(this._stock);
    }

    public String getTicker() { return _stock.getTicker(); }

    public double price() {
        return _price;
    }

    public void setPrice(double price) {
        _price = price;
    }
}

