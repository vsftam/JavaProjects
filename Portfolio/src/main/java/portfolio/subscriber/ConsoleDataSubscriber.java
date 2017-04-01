package portfolio.subscriber;

import portfolio.publisher.DataPublisher;

/**
 * Created by vincenttam on 3/18/17.
 */
public class ConsoleDataSubscriber implements DataSubscriber {

    public void addPublisher(DataPublisher pub) {
        // console doesn't need to know publisher
        return;
    }

    public void updateNAV(double nav) {
        System.out.format("Latest portfolio nav is %.4f%n", nav);
    }
}
