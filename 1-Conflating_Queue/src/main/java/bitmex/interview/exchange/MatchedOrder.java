package bitmex.interview.exchange;

public class MatchedOrder {

    private final String aggressingOrderId;
    private final String restingOrderId;
    private final int price;
    private final int volume;
    public MatchedOrder(String aggressingOrderId, String restingOrderId, int price, int volume) {
        this.aggressingOrderId = aggressingOrderId;
        this.restingOrderId = restingOrderId;
        this.price = price;
        this.volume = volume;
    }

    public String getAggressingOrderId() { return aggressingOrderId; }
    public String getRestingOrderId() { return restingOrderId; }
    public int getPrice() { return price; }
    public int getVolume() { return volume; }

    public String getKey() { return "trade "+aggressingOrderId+","+restingOrderId+","+price; }

    public String toString() {
        return "trade "+aggressingOrderId+","+restingOrderId+","+price+","+volume;
    }
}
