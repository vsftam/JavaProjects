package portfolio.pricing;

import java.util.Random;

/**
 * Created by vincenttam on 3/18/17.
 */
public class GeometricBrownianMotion {

    static Random ran = new Random();

    static public double nextValue(double s, double mui, double seconds, double sigma) {
        assert(s >= 0);

        double eta = ran.nextGaussian();

        double ds = s * (mui * seconds / 7257600 + sigma * eta * Math.sqrt( seconds / 7257600 ) );

        return (s + ds) > 0 ? s + ds : 0;
    }
}
