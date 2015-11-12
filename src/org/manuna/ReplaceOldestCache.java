package org.manuna;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReplaceOldestCache<K, V> extends Cache<K, V> {

    public ReplaceOldestCache() {
        this(DEFAULT_CAPACITY, DEFAULT_TIME_TO_LIVE);
    }

    public ReplaceOldestCache(int capacity, long timeToLive) {
        super(capacity, timeToLive);
        this.cacheMap = new LinkedHashMap<K, ValueHolder>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, ValueHolder> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public V get(K key) {
        synchronized (cacheMap) {
            ValueHolder vh = cacheMap.get(key);
            if (vh == null) {
                return null;
            }
            return vh.value;
        }
    }

    public V put(K key, V value) {
        synchronized (cacheMap) {
            ValueHolder previous = cacheMap.put(key, new ValueHolder(value));
            if (previous == null) {
                return null;
            }
            return previous.value;
        }
    }
}
