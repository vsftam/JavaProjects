package bitmex.interview.conflatingqueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConflatingQueueImpl<K, V> implements ConflatingQueue<K, V> {

    private final LinkedBlockingQueue<K> queue;
    private final Map<K, V> lookup;
    private final Lock lock;

    public ConflatingQueueImpl() {
        queue = new LinkedBlockingQueue<>();  // unbounded queue
        lookup = new HashMap<>();
        lock = new ReentrantLock();
    }

    public boolean offer(KeyValue<K, V> keyValue) {
        lock.lock();
        try {
            K key = keyValue.getKey();
            if(key == null)
                return false;
            boolean succeed = lookup.containsKey(key) || queue.offer(key);
            if(succeed)
                lookup.put(key, keyValue.getValue());
            return succeed;
        }
        finally {
            lock.unlock();
        }
    }

    public KeyValue<K, V> take() throws InterruptedException {
        lock.lock();
        try {
            K key = queue.take(); // blocks consumer if queue is empty
            V value = lookup.remove(key);
            return new KeyValueImpl<>(key, value);
        }
        finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}