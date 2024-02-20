package bitmex.interview.conflatingqueue;

import org.junit.Assert;
import org.junit.Test;

public class ConflatingQueueTest {
    @Test
    public void shouldShowCorrectSingleOrder() throws InterruptedException {
        ConflatingQueue<String, Integer> queue = new ConflatingQueueImpl<>();
        KeyValue<String, Integer> k1 = new KeyValueImpl<>("AAPL", 400);
        queue.offer(k1);
        Assert.assertFalse(queue.isEmpty());
        KeyValue<String, Integer> r1 = queue.take();
        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(r1.getKey(), "AAPL");
        Assert.assertEquals(r1.getValue(), Integer.valueOf(400));
    }

    @Test
    public void shouldShowCorrectReplacedOrder() throws InterruptedException {
        ConflatingQueue<String, Integer> queue = new ConflatingQueueImpl<>();
        KeyValue<String, Integer> k1 = new KeyValueImpl<>("AAPL", 400);
        KeyValue<String, Integer> k1b = new KeyValueImpl<>("AAPL", 425);
        queue.offer(k1);
        queue.offer(k1b);
        KeyValue<String, Integer> r1 = queue.take();
        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(r1.getKey(), "AAPL");
        Assert.assertEquals(r1.getValue(), Integer.valueOf(425));
    }

    @Test
    public void shouldShowCorrectOrderWithReplacedValue() throws InterruptedException {
        ConflatingQueue<String, Integer> queue = new ConflatingQueueImpl<>();
        KeyValue<String, Integer> k1 = new KeyValueImpl<>("AAPL", 400);
        KeyValue<String, Integer> k2 = new KeyValueImpl<>("TSLA", 500);
        KeyValue<String, Integer> k3 = new KeyValueImpl<>("MSFT", 250);
        KeyValue<String, Integer> k1b = new KeyValueImpl<>("AAPL", 425);

        queue.offer(k1);
        queue.offer(k2);
        queue.offer(k3);
        queue.offer(k1b);

        KeyValue<String, Integer> r1 = queue.take();
        KeyValue<String, Integer> r2 = queue.take();
        KeyValue<String, Integer> r3 = queue.take();

        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(r1.getKey(), "AAPL");
        Assert.assertEquals(r1.getValue(), Integer.valueOf(425));
        Assert.assertEquals(r2.getKey(), "TSLA");
        Assert.assertEquals(r2.getValue(), Integer.valueOf(500));
        Assert.assertEquals(r3.getKey(), "MSFT");
        Assert.assertEquals(r3.getValue(), Integer.valueOf(250));
    }

    @Test
    public void shouldShowCorrectOrderWithReplacedValues() throws InterruptedException {
        ConflatingQueue<String, Integer> queue = new ConflatingQueueImpl<>();
        KeyValue<String, Integer> k1 = new KeyValueImpl<>("AAPL", 400);
        KeyValue<String, Integer> k2 = new KeyValueImpl<>("TSLA", 500);
        KeyValue<String, Integer> k3 = new KeyValueImpl<>("MSFT", 250);
        KeyValue<String, Integer> k2b = new KeyValueImpl<>("TSLA", 480);
        KeyValue<String, Integer> k3b = new KeyValueImpl<>("MSFT", 257);
        KeyValue<String, Integer> k1b = new KeyValueImpl<>("AAPL", 425);

        queue.offer(k1);
        queue.offer(k2);
        queue.offer(k3);
        queue.offer(k2b);
        queue.offer(k3b);
        queue.offer(k1b);

        KeyValue<String, Integer> r1 = queue.take();
        KeyValue<String, Integer> r2 = queue.take();
        KeyValue<String, Integer> r3 = queue.take();

        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(r1.getKey(), "AAPL");
        Assert.assertEquals(r1.getValue(), Integer.valueOf(425));
        Assert.assertEquals(r2.getKey(), "TSLA");
        Assert.assertEquals(r2.getValue(), Integer.valueOf(480));
        Assert.assertEquals(r3.getKey(), "MSFT");
        Assert.assertEquals(r3.getValue(), Integer.valueOf(257));
    }
}
