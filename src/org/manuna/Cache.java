package org.manuna;

import java.util.*;

public abstract class Cache<K, V> {

    protected static final int DEFAULT_CAPACITY = 16;

    protected static final long DEFAULT_TIME_TO_LIVE = 60 * 1000; // 1 minute

    protected static final long MIN_CAPACITY = 1;

    protected static final long MIN_TIME_TO_LIVE = 5; // miliseconds

    private static final long DEFAULT_INTERVAL = MIN_TIME_TO_LIVE + 1;



    protected int capacity;

    protected long timeToLive;

    protected Map<K, ValueHolder> cacheMap;



    public Cache() {
        this(DEFAULT_CAPACITY, DEFAULT_TIME_TO_LIVE);
    }

    public Cache(int capacity, long timeToLive) {
        if (capacity < MIN_CAPACITY) {
            throw new IllegalArgumentException("capacity=" + capacity + " less than MIN_CAPACITY=" + MIN_CAPACITY);
        }
        if (timeToLive < MIN_TIME_TO_LIVE) {
            throw new IllegalArgumentException("timeToLive=" + timeToLive + " less than MIN_TIME_TO_LIVE=" + MIN_TIME_TO_LIVE);
        }

        this.capacity = capacity;
        this.timeToLive = timeToLive;

        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(DEFAULT_INTERVAL);
                    } catch (InterruptedException ex) {
                    }
                    doEviction();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }


    abstract V get(K key);

    abstract V put(K key, V value);

    public V remove(Object key) {
        synchronized (cacheMap) {
            return cacheMap.remove(key).value;
        }
    }

    public Set<Map.Entry<K, ValueHolder>> entrySet() {
        synchronized (cacheMap) {
            return cacheMap.entrySet();
        }
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    /**
     * This method removes entries which have passed their expiration date;
     */
    private void doEviction() {
        long currentTime = System.currentTimeMillis();
        synchronized (cacheMap) {
            for (Iterator<Map.Entry<K, ValueHolder>> it = cacheMap.entrySet().iterator(); it.hasNext();) {
                ValueHolder vh = it.next().getValue();
                if (vh != null && currentTime > (timeToLive + vh.created)) {
                    it.remove();
                }
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int newCapacity) {
        if (newCapacity > cacheMap.size()) {
            this.capacity = newCapacity;
        }
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    private void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * Inner class which holds V - value, created - createdTime in milliseconds
     * and accessedCount - how many times ValueHolder object was called;
     */
    protected class ValueHolder {
        public long created = System.currentTimeMillis();
        public int accessedCount;
        public V value;

        protected ValueHolder(V value) {
            this.value = value;
            this.accessedCount = 1;
        }
    }

    public enum DisplacementStrategy {
        /**
         *When cache is full, oldest entry is replaced by new;
         */
        REPLACE_OLDEST,

        /**
         *When cacheis full, least frequently accessed entry is replaced by new;
         */
        REPLACE_LEAST_FREQUENTLY_USED
    }

}
