package portfolio.pricing;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Created by vincenttam on 3/18/17.
 */
public class EuropeanOptionPricing {

    static NormalDistribution normal = new NormalDistribution();

    static public double callOptionPrice(double S, double r, double t, double K, double sigma) {

        return S * N( d1(S, r, t, K, sigma) ) - K * Math.exp(-1 * r * t) * N( d2(S, r, t, K, sigma) );
    }

    static public double putOptionPrice(double S, double r, double t, double K, double sigma) {

        return K * Math.exp(-1 * r * t) * N( -1 * d2(S, r, t, K, sigma) ) - S * N( -1 * d1(S, r, t, K, sigma) );
    }

    static private double N(double x) {
        return normal.cumulativeProbability(x);
    }

    static private double d1(double S, double r, double t, double K, double sigma) {

        return ( Math.log(S / K) + (r + sigma * sigma / 2) * t ) / ( sigma * Math.sqrt(t) ) ;
    }

    static private double d2(double S, double r, double t, double K, double sigma) {
        return d1(S, r, t, K, sigma) - sigma * Math.sqrt(t);
    }
}
