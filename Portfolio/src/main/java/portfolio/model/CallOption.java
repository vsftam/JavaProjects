package portfolio.model;

import portfolio.core.Market;
import portfolio.pricing.EuropeanOptionPricing;

/**
 * Created by vincenttam on 3/18/17.
 */
public class CallOption extends Option {

    public CallOption(Stock stock, double maturityInYears, double strike) {
        super(stock, maturityInYears, strike);
    }

    public InstrumentType getType() { return InstrumentType.CallOption; }

    public void initOptionPrice(double rate) {
        _price = EuropeanOptionPricing.callOptionPrice(_stock.price(), rate, _maturityInYears, _strike, _stock.getAnnualizedStdDev());
    }

    public void updateFromStockPrice(double stockPrice, double rate) {
        _stock.setPrice(stockPrice);
        _price = EuropeanOptionPricing.callOptionPrice(stockPrice, rate, _maturityInYears, _strike, _stock.getAnnualizedStdDev());
    }

    public String reportString() {
        return String.format("Call Option\t%s\tStrike %.2f\tMaturity %.1f\t", _stock.getTicker(), _strike, _maturityInYears);
    }

    @Override
    public String toString() {
        return String.format("Call Option on %s with Strike: %.2f and Maturity: %.1f", _stock.getTicker(), _strike, _maturityInYears);
    }
}
