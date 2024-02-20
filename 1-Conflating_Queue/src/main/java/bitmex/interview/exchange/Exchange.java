package bitmex.interview.exchange;

import java.time.LocalDateTime;
import java.util.*;

public class Exchange {

    PriorityQueue<Order> asks = new PriorityQueue<>(new AskOrderComparator());
    PriorityQueue<Order> bids = new PriorityQueue<>(new BidOrderComparator());

    LinkedHashMap<String,Integer> matchedOrders = new LinkedHashMap<>();
    static class AskOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if(o1.getPrice() != o2.getPrice())
                return Integer.compare(o1.getPrice(), o2.getPrice());
            else
                return o1.getTimestamp().compareTo( o2.getTimestamp() );
        }
    }

    static class BidOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if(o1.getPrice() != o2.getPrice() )
                return Integer.compare(o2.getPrice(), o1.getPrice());
            else
                return o1.getTimestamp().compareTo( o2.getTimestamp() );
        }
    }

    private void addMatchedOrders(MatchedOrder m) {
        String key = m.getKey();
        int volume = (matchedOrders.getOrDefault(key, 0)) + m.getVolume();
        matchedOrders.put(key,  + volume);
    }

    private boolean reAddOrder(PriorityQueue<Order> orderQueue, int remainingVolume) {
        Order order = orderQueue.poll();
        order.setVolume(remainingVolume);
        order.setCurrentVisibleQuantity( order.getVisibleQuantity() );
        order.setTimestamp(LocalDateTime.now());
        return orderQueue.offer(order);
    }

    public void addBidOrder(Order aggressingBidOrder) {
        aggressingBidOrder.setTimestamp(LocalDateTime.now());
        Order passiveAskOrder = asks.peek();
        if(passiveAskOrder == null || aggressingBidOrder.getPrice() < passiveAskOrder.getPrice()) {
            boolean bidOrderAdded = bids.offer(aggressingBidOrder);
            if(!bidOrderAdded)
                System.out.println("Cannot add bid order "+ aggressingBidOrder + " to exchange");
            return;
        }
        while(passiveAskOrder != null && aggressingBidOrder.getPrice() >= passiveAskOrder.getPrice()) {
            boolean isPassiveIcebergAskOrder = passiveAskOrder.getVisibleQuantity() != -1;
            int aggressingOrderVolume = aggressingBidOrder.getVolume();
            int passiveOrderVolume = isPassiveIcebergAskOrder ? passiveAskOrder.getCurrentVisibleQuantity() : passiveAskOrder.getVolume();
            int matchedVolume = Math.min(aggressingOrderVolume, passiveOrderVolume);
            MatchedOrder match = new MatchedOrder(aggressingBidOrder.getId(), passiveAskOrder.getId(), passiveAskOrder.getPrice(), matchedVolume);
            addMatchedOrders(match);

            if(aggressingOrderVolume < passiveOrderVolume) {
                int unmatchedAskVolume = passiveOrderVolume - aggressingOrderVolume;
                if(isPassiveIcebergAskOrder) {
                    passiveAskOrder.setVolume(passiveAskOrder.getVolume() - matchedVolume);
                    passiveAskOrder.setCurrentVisibleQuantity(unmatchedAskVolume);
                }
                else
                    passiveAskOrder.setVolume(unmatchedAskVolume);
                aggressingBidOrder.setVolume(0);
                break;
            }
            else {
                int remainingVolume = passiveAskOrder.getVolume() - matchedVolume;
                if(isPassiveIcebergAskOrder && remainingVolume != 0) {
                    boolean orderReadded = reAddOrder(asks, remainingVolume);
                    if(!orderReadded)
                        System.out.println("Cannot re-add iceberg ask order "+ passiveAskOrder + " to exchange");
                }
                else
                    asks.poll();

                if( aggressingOrderVolume == passiveOrderVolume ) {
                    aggressingBidOrder.setVolume(0);
                    break;
                }
                else {
                    int unmatchedBidVolume = aggressingOrderVolume - passiveOrderVolume;
                    aggressingBidOrder.setVolume(unmatchedBidVolume);
                    passiveAskOrder = asks.peek();
                }
            }
        }
        if(aggressingBidOrder.getVolume() > 0) {
            boolean orderAdded = bids.offer(aggressingBidOrder);
            if(!orderAdded) 
                System.out.println("Cannot add bid order "+ aggressingBidOrder + " to exchange");
        }
    }

    public void addAskOrder(Order aggressingAskOrder) {
        aggressingAskOrder.setTimestamp(LocalDateTime.now());
        Order passiveBidOrder = bids.peek();
        if(passiveBidOrder == null || passiveBidOrder.getPrice() < aggressingAskOrder.getPrice() ) {
            boolean askOrderAdded = asks.offer(aggressingAskOrder);
            if(!askOrderAdded)
                System.out.println("Cannot add ask order "+ aggressingAskOrder + " to exchange");
            return;
        }
        while(passiveBidOrder != null && passiveBidOrder.getPrice() >= aggressingAskOrder.getPrice()) {
            boolean isPassiveIcebergBidOrder = passiveBidOrder.getVisibleQuantity() != -1;
            int aggressingOrderVolume = aggressingAskOrder.getVolume();
            int passiveOrderVolume = isPassiveIcebergBidOrder ? passiveBidOrder.getCurrentVisibleQuantity() : passiveBidOrder.getVolume();
            int matchedVolume = Math.min(aggressingOrderVolume, passiveOrderVolume);
            MatchedOrder match = new MatchedOrder(aggressingAskOrder.getId(), passiveBidOrder.getId(), passiveBidOrder.getPrice(), matchedVolume);
            addMatchedOrders(match);

            if(aggressingOrderVolume < passiveOrderVolume) {
                int unmatchedAskVolume = passiveOrderVolume - aggressingOrderVolume;
                if(isPassiveIcebergBidOrder) {
                    passiveBidOrder.setVolume(passiveBidOrder.getVolume() - matchedVolume);
                    passiveBidOrder.setCurrentVisibleQuantity(unmatchedAskVolume);
                }
                else
                    passiveBidOrder.setVolume(unmatchedAskVolume);
                aggressingAskOrder.setVolume(0);
                break;
            }
            else {
                int remainingVolume = passiveBidOrder.getVolume() - matchedVolume;
                if(isPassiveIcebergBidOrder && remainingVolume != 0) {
                    boolean orderReadded = reAddOrder(bids, remainingVolume);
                    if(!orderReadded)
                        System.out.println("Cannot re-add iceberg bid order "+ passiveBidOrder + " to exchange");
                }
                else
                    bids.poll();

                if( aggressingOrderVolume == passiveOrderVolume ) {
                    aggressingAskOrder.setVolume(0);
                    break;
                }
                else {
                    int unmatchedAskVolume = aggressingOrderVolume - passiveOrderVolume;
                    aggressingAskOrder.setVolume(unmatchedAskVolume);
                    passiveBidOrder = bids.peek();
                }
            }
        }
        if(aggressingAskOrder.getVolume() > 0) {
            boolean orderAdded = asks.offer(aggressingAskOrder);
            if(!orderAdded)
                System.out.println("Cannot add ask order "+ aggressingAskOrder + " to exchange");
        }
    }

    public List<String> getOrderBook() {
        List<String> orderBook = new ArrayList<>();

        Set<String> keys = matchedOrders.keySet();
        for (String key : keys) {
            orderBook.add( key + "," + matchedOrders.get(key) );
        }
        while(!bids.isEmpty() || !asks.isEmpty()) {
            Order bid = bids.peek();
            Order ask = asks.peek();
            if(bid != null && ask != null) {
                orderBook.add( bid + " | " + ask );
                bids.poll();
                asks.poll();
            }
            else if(bid != null) {
                orderBook.add( bid + " |                   ");
                bids.poll();
            }
            else {
                orderBook.add( "                   | " + ask );
                asks.poll();
            }
        }
        return orderBook;
    }
    public void printOrderBook() {
        for(String item: getOrderBook() ) {
            System.out.println( item );
        }
    }

    public static void main(String[] args) {
        Exchange exchange = new Exchange();
        Scanner s = new Scanner(System.in);
        while(s.hasNext()) {
            Order order = Order.fromString(s.nextLine());
            if (BidAsk.BID == order.getSide())
                exchange.addBidOrder(order);
            else
                exchange.addAskOrder(order);
        }
        exchange.printOrderBook();
    }
}


