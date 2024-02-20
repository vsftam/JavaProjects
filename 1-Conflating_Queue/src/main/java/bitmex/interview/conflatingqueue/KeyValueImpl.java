package bitmex.interview.conflatingqueue;

public class KeyValueImpl<K, V> implements KeyValue<K, V> {

    K key;
    V value;

    public KeyValueImpl(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public String toString() {
        return "k="+key+":v="+value;
    }
}