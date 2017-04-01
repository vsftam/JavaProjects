package portfolio.model;

import portfolio.pricing.EuropeanOptionPricing;

/**
 * Created by vincenttam on 3/18/17.
 */
public class PutOption extends Option {

    public PutOption(Stock stock, double maturityInYears, double strike) {
        super(stock, maturityInYears, strike);
    }

    public InstrumentType getType() { return InstrumentType.PutOption; }

    public void initOptionPrice(double rate) {
        _price = EuropeanOptionPricing.putOptionPrice(_stock.price(), rate, _maturityInYears, _strike, _stock.getAnnualizedStdDev());
    }

    public void updateFromStockPrice(double stockPrice, double rate) {
        _stock.setPrice(stockPrice);
        _price = EuropeanOptionPricing.putOptionPrice(stockPrice, rate, _maturityInYears, _strike, _stock.getAnnualizedStdDev());
    }

    public String reportString() {
        return String.format("Put Option\t%s\tStrike %.2f\tMaturity %.1f\t", _stock.getTicker(), _strike, _maturityInYears);
    }

    @Override
    public String toString() {
        return String.format("Put Option on %s with Strike %.2f and Maturity %.1f", _stock.getTicker(), _strike, _maturityInYears);
    }
}
