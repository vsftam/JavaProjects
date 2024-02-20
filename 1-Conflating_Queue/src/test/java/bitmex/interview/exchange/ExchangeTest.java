package bitmex.interview.exchange;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExchangeTest {

    private List<String> processOrders(ArrayList<String> input) {
        Exchange exchange = new Exchange();
        for (String value : input) {
            Order order = Order.fromString(value);
            if (BidAsk.BID == order.getSide())
                exchange.addBidOrder(order);
            else
                exchange.addAskOrder(order);
        }
        return exchange.getOrderBook();
    }

    @Test
    public void shouldShowCorrectOrderBookNoMatch() {

        ArrayList<String> input = new ArrayList<>();
        input.add("10000,B,98,25500");
        input.add("10006,S,103,20000");
        input.add("10001,S,100,500");
        input.add("10002,S,100,10000");
        input.add("10005,S,105,15000");
        input.add("10003,B,99,50000");
        input.add("10004,S,103,200");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 5);
        Assert.assertEquals(orderBook.get(0),"     50,000     99 |    100         500");
        Assert.assertEquals(orderBook.get(1),"     25,500     98 |    100      10,000");
        Assert.assertEquals(orderBook.get(2),"                   |    103      20,000");
        Assert.assertEquals(orderBook.get(3),"                   |    103         200");
        Assert.assertEquals(orderBook.get(4),"                   |    105      15,000");
    }

    @Test
    public void shouldShowCorrectOrderBookWithPartlyMatchedBid() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,B,98,25500");
        input.add("10005,S,105,20000");
        input.add("10001,S,100,500");
        input.add("10002,S,100,10000");
        input.add("10003,B,99,50000");
        input.add("10004,S,103,100");
        input.add("10006,B,105,16000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 6);
        Assert.assertEquals(orderBook.get(0),"trade 10006,10001,100,500");
        Assert.assertEquals(orderBook.get(1),"trade 10006,10002,100,10000");
        Assert.assertEquals(orderBook.get(2),"trade 10006,10004,103,100");
        Assert.assertEquals(orderBook.get(3),"trade 10006,10005,105,5400");
        Assert.assertEquals(orderBook.get(4),"     50,000     99 |    105      14,600");
        Assert.assertEquals(orderBook.get(5),"     25,500     98 |                   ");
    }

    @Test
    public void shouldShowCorrectOrderBookWithPartlyMatchedAsk() {

        ArrayList<String> input = new ArrayList<>();
        input.add("10000,B,98,25500");
        input.add("10005,S,105,20000");
        input.add("10001,S,100,500");
        input.add("10002,S,100,10000");
        input.add("10003,B,99,50000");
        input.add("10004,S,103,100");
        input.add("10006,S,97,60000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 6);
        Assert.assertEquals(orderBook.get(0),"trade 10006,10003,99,50000");
        Assert.assertEquals(orderBook.get(1),"trade 10006,10000,98,10000");
        Assert.assertEquals(orderBook.get(2),"     15,500     98 |    100         500");
        Assert.assertEquals(orderBook.get(3),"                   |    100      10,000");
        Assert.assertEquals(orderBook.get(4),"                   |    103         100");
        Assert.assertEquals(orderBook.get(5),"                   |    105      20,000");
    }

    @Test
    public void shouldShowCorrectOrderBookWithFullyMatchedBid() {

        ArrayList<String> input = new ArrayList<>();
        input.add("10000,B,98,25500");
        input.add("10005,S,105,20000");
        input.add("10001,S,100,500");
        input.add("10002,S,100,10000");
        input.add("10003,B,99,50000");
        input.add("10004,S,103,100");
        input.add("10006,B,103,10500");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 4);
        Assert.assertEquals(orderBook.get(0),"trade 10006,10001,100,500");
        Assert.assertEquals(orderBook.get(1),"trade 10006,10002,100,10000");
        Assert.assertEquals(orderBook.get(2),"     50,000     99 |    103         100");
        Assert.assertEquals(orderBook.get(3),"     25,500     98 |    105      20,000");
    }

    @Test
    public void shouldShowCorrectOrderBookWithEarlyMatches() {

        ArrayList<String> input = new ArrayList<>();
        input.add("10000,B,98,25500");
        input.add("10006,S,97,20000");
        input.add("10001,S,96,10500");
        input.add("10003,B,99,5000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 3);
        Assert.assertEquals(orderBook.get(0),"trade 10006,10000,98,20000");
        Assert.assertEquals(orderBook.get(1),"trade 10001,10000,98,5500");
        Assert.assertEquals(orderBook.get(2),"trade 10003,10001,96,5000");
    }

    @Test
    public void shouldShowCorrectOrderBookWithAggressiveIcebergBid() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,S,101,20000");
        input.add("10001,B,99,50000");
        input.add("10002,S,100,10000");
        input.add("10003,S,100,7500");
        input.add("10004,B,98,25500");
        input.add("ice1,B,100,100000,10000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 5);
        Assert.assertEquals(orderBook.get(0),"trade ice1,10002,100,10000");
        Assert.assertEquals(orderBook.get(1),"trade ice1,10003,100,7500");
        Assert.assertEquals(orderBook.get(2),"     10,000    100 |    101      20,000");
        Assert.assertEquals(orderBook.get(3),"     50,000     99 |                   ");
        Assert.assertEquals(orderBook.get(4),"     25,500     98 |                   ");
    }

    @Test
    public void shouldShowCorrectOrderBookWithPassiveIcebergBid() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,S,101,20000");
        input.add("10001,B,99,50000");
        input.add("10002,B,98,25500");
        input.add("ice1,B,100,82500,10000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 3);
        Assert.assertEquals(orderBook.get(0),"     10,000    100 |    101      20,000");
        Assert.assertEquals(orderBook.get(1),"     50,000     99 |                   ");
        Assert.assertEquals(orderBook.get(2),"     25,500     98 |                   ");
    }

    @Test
    public void shouldShowCorrectOrderBookWithPassiveIcebergAskJustMatched() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,B,100,20000");
        input.add("10001,S,102,50000");
        input.add("10002,S,103,25500");
        input.add("ice1,S,101,82500,10000");
        input.add("10003,B,101,10000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 4);
        Assert.assertEquals(orderBook.get(0),"trade 10003,ice1,101,10000");
        Assert.assertEquals(orderBook.get(1),"     20,000    100 |    101      10,000");
        Assert.assertEquals(orderBook.get(2),"                   |    102      50,000");
        Assert.assertEquals(orderBook.get(3),"                   |    103      25,500");
    }

    @Test
    public void shouldShowCorrectOrderBookWithPassiveIcebergAskOverMatched() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,B,100,20000");
        input.add("10001,S,102,50000");
        input.add("10002,S,103,25500");
        input.add("ice1,S,101,72500,10000");
        input.add("10004,B,101,11000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 4);
        Assert.assertEquals(orderBook.get(0),"trade 10004,ice1,101,11000");
        Assert.assertEquals(orderBook.get(1),"     20,000    100 |    101       9,000");
        Assert.assertEquals(orderBook.get(2),"                   |    102      50,000");
        Assert.assertEquals(orderBook.get(3),"                   |    103      25,500");
    }

    @Test
    public void shouldShowCorrectOrderBookWithTwoPassiveIcebergAsks() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,B,100,20000");
        input.add("10001,S,102,50000");
        input.add("10002,S,103,25500");
        input.add("ice1,S,101,61500,9000");
        input.add("ice2,S,101,50000,20000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 4);
        Assert.assertEquals(orderBook.get(0),"     20,000    100 |    101       9,000");
        Assert.assertEquals(orderBook.get(1),"                   |    101      20,000");
        Assert.assertEquals(orderBook.get(2),"                   |    102      50,000");
        Assert.assertEquals(orderBook.get(3),"                   |    103      25,500");
    }

    @Test
    public void shouldShowCorrectOrderBookWithTwoPassiveIcebergAsksFullyMatched() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,B,100,20000");
        input.add("10001,S,102,50000");
        input.add("10002,S,103,25500");
        input.add("ice1,S,101,61500,9000");
        input.add("ice2,S,101,50000,20000");
        input.add("10005,B,101,35000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 6);
        Assert.assertEquals(orderBook.get(0),"trade 10005,ice1,101,15000");
        Assert.assertEquals(orderBook.get(1),"trade 10005,ice2,101,20000");
        Assert.assertEquals(orderBook.get(2),"     20,000    100 |    101       3,000");
        Assert.assertEquals(orderBook.get(3),"                   |    101      20,000");
        Assert.assertEquals(orderBook.get(4),"                   |    102      50,000");
        Assert.assertEquals(orderBook.get(5),"                   |    103      25,500");
    }


    @Test
    public void shouldShowCorrectOrderBookWithTwoPassiveIcebergAskAndFullyMatched() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,B,100,20000");
        input.add("10001,S,102,50000");
        input.add("10002,S,103,25500");
        input.add("ice1,S,101,72500,10000");
        input.add("10004,B,101,11000");
        input.add("ice2,S,101,50000,20000");
        input.add("10005,B,101,35000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 7);
        Assert.assertEquals(orderBook.get(0),"trade 10004,ice1,101,11000");
        Assert.assertEquals(orderBook.get(1),"trade 10005,ice1,101,15000");
        Assert.assertEquals(orderBook.get(2),"trade 10005,ice2,101,20000");
        Assert.assertEquals(orderBook.get(3),"     20,000    100 |    101       4,000");
        Assert.assertEquals(orderBook.get(4),"                   |    101      20,000");
        Assert.assertEquals(orderBook.get(5),"                   |    102      50,000");
        Assert.assertEquals(orderBook.get(6),"                   |    103      25,500");
    }


    @Test
    public void shouldShowCorrectOrderBookWithAggressiveIcebergAsk() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,B,98,25500");
        input.add("10005,S,101,20000");
        input.add("10002,S,100,10000");
        input.add("10001,S,100,7500");
        input.add("10003,B,99,50000");
        input.add("ice1,B,100,100000,10000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 5);
        Assert.assertEquals(orderBook.get(0),"trade ice1,10002,100,10000");
        Assert.assertEquals(orderBook.get(1),"trade ice1,10001,100,7500");
        Assert.assertEquals(orderBook.get(2),"     10,000    100 |    101      20,000");
        Assert.assertEquals(orderBook.get(3),"     50,000     99 |                   ");
        Assert.assertEquals(orderBook.get(4),"     25,500     98 |                   ");
    }

    @Test
    public void shouldShowCorrectOrderBookWithTwoPassiveIcebergBidAndFullyMatched() {
        ArrayList<String> input = new ArrayList<>();

        input.add("10000,S,101,20000");
        input.add("10001,B,99,50000");
        input.add("10002,B,98,25500");
        input.add("ice1,B,100,72500,10000");
        input.add("10004,S,100,11000");
        input.add("ice2,B,100,50000,20000");
        input.add("10005,S,100,35000");

        List<String> orderBook = processOrders(input);

        Assert.assertEquals(orderBook.size(), 7);
        Assert.assertEquals(orderBook.get(0),"trade 10004,ice1,100,11000");
        Assert.assertEquals(orderBook.get(1),"trade 10005,ice1,100,15000");
        Assert.assertEquals(orderBook.get(2),"trade 10005,ice2,100,20000");
        Assert.assertEquals(orderBook.get(3),"      4,000    100 |    101      20,000");
        Assert.assertEquals(orderBook.get(4),"     20,000    100 |                   ");
        Assert.assertEquals(orderBook.get(5),"     50,000     99 |                   ");
        Assert.assertEquals(orderBook.get(6),"     25,500     98 |                   ");
    }
}
