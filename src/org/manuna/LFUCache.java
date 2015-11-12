package org.manuna;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LFUCache<K, V> extends Cache<K, V> {

    public LFUCache() {
        this(DEFAULT_CAPACITY, DEFAULT_TIME_TO_LIVE);
    }

    public LFUCache(int capacity, long timeToLive) {
        super(capacity, timeToLive);
        cacheMap = new HashMap<>();
    }

    public V get(K key) {
        synchronized (cacheMap) {
            ValueHolder vh = cacheMap.get(key);
            if (vh == null) {
                return null;
            }
            vh.accessedCount++;
            return vh.value;
        }
    }

    public V put(K key, V value) {
        V oldValue;
        synchronized (cacheMap) {
            ValueHolder previous = cacheMap.get(key);
            if (previous == null) {
                if (cacheMap.size() == capacity) {
                    evictOne();
                }
                cacheMap.put(key, new ValueHolder(value));
                return null;
            } else {
                oldValue = previous.value;
                previous.value = value;
            }
        }
        return oldValue;
    }

    /**
     * Removes least used entry;
     */
    private void evictOne() {
        int minAccessedCount = Integer.MAX_VALUE;
        K minAccessedCountKey = null;
        Map.Entry<K, ValueHolder> tmp;
        Iterator it = cacheMap.entrySet().iterator();
        while (it.hasNext()) {
            tmp = (Map.Entry<K, ValueHolder>) it.next();
            if (tmp.getValue().accessedCount < minAccessedCount) {
                minAccessedCount = tmp.getValue().accessedCount;
                minAccessedCountKey = tmp.getKey();
            }
        }
        this.remove(minAccessedCountKey);
    }

}
