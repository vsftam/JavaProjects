package bitmex.interview.exchange;

import java.time.LocalDateTime;

enum BidAsk { BID, ASK }

public class Order {
    private final String id;
    private final BidAsk side;
    private final int price;
    private int volume;
    private final int visibleQuantity;
    private int currentVisibleQuantity;
    private LocalDateTime timestamp;
    public Order(String id, String side, int price, int volume) {
        this(id, side, price, volume, -1);
    }

    public Order(String id, String side, int price, int volume, int visibleQuantity) {
        this.id = id;
        this.side = "B".equals(side) ? BidAsk.BID : BidAsk.ASK; // assume side is either B or S
        this.price = price;
        this.volume = volume;
        this.visibleQuantity = visibleQuantity;
        this.currentVisibleQuantity = visibleQuantity;
    }

    public String getId() { return id; }
    public BidAsk getSide() { return side; }
    public int getPrice() { return price; }
    public int getVolume() { return volume; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setVolume(int v) { volume = v; }
    public int getVisibleQuantity() { return visibleQuantity; }
    public int getCurrentVisibleQuantity() { return currentVisibleQuantity; }
    public void setCurrentVisibleQuantity(int vq) { currentVisibleQuantity = vq; }
    public void setTimestamp(LocalDateTime t) { timestamp = t; }
    public static Order fromString(String line) throws IllegalArgumentException {
        String[] parts = line.split(",");
        if(!("S".equals(parts[1]) || "B".equals(parts[1])))
            throw new IllegalArgumentException("Invalid side "+ parts[1]);
        int price = Integer.parseInt(parts[2]);
        int volume = Integer.parseInt(parts[3]);
        if(parts.length == 5) {
            int visibleQuantity = Integer.parseInt(parts[4]);
            return new Order(parts[0], parts[1], price, volume, visibleQuantity);
        }
        else
            return new Order(parts[0], parts[1], price, volume);
    }

    public String toString() {
        int displayedQuantity = visibleQuantity == -1 ? volume : currentVisibleQuantity;
        return BidAsk.BID == side ?
                String.format("%1$,11d %2$6d", displayedQuantity, price) :
                String.format("%1$6d %2$,11d", price, displayedQuantity);
    }
}


