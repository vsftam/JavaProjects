package portfolio.subscriber;

import portfolio.model.Position;
import portfolio.publisher.DataPublisher;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by vincenttam on 3/18/17.
 */
public class TextFilePrinterSubscriber implements DataSubscriber {

    DecimalFormat formatter = new DecimalFormat("#,##0.00");

    private final String outputFile = "portfolio-report.txt";

    private DataPublisher _publisher;

    public void addPublisher(DataPublisher pub) {
        _publisher = pub;
    }

    public void updateNAV(double nav) {
        // text file printer doesn't provide real time update
        return;
    }

    public void printReport() {
        Map<Position, Double> portfolio = _publisher.positionMarketValues();
        System.out.println("------------------------------------------");
        try{
            PrintWriter writer = new PrintWriter(outputFile);
            String title = "Portfolio report\n";
            System.out.println(title);
            writer.println(title);
            for(Position p : portfolio.keySet() ) {
                double v = portfolio.get(p);
                String line = p.reportString() + "\t\tmarket value: " + formatter.format(v);
                System.out.println(line);
                writer.println(line);
            }
            double nav = _publisher.portfolioNAV();
            String total = "\nTotal portfolio net asset value is " + formatter.format(nav);
            System.out.println(total);
            writer.println(total);
            System.out.println("------------------------------------------");

            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file " + outputFile + ":" + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
