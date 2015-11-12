package org.manuna;

import java.util.Iterator;
import java.util.Map;

public class CacheDemo {

    /*
    * Simple demostration of Cache below
    */
    public static void main(String[] args) throws InterruptedException{

        try {
            Cache<Integer, String> cache = CacheFactory.getCache(3, 2, Cache.DisplacementStrategy.REPLACE_OLDEST);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        System.out.print("\n\n\nREMOVE OLDEST CACHE\n");

        Cache<Integer, String> cache = CacheFactory.getCache(3, 5, Cache.DisplacementStrategy.REPLACE_OLDEST);

        cache.put(0, "A"); print(cache);

        cache.put(1, "B"); print(cache);

        cache.put(2, "C"); print(cache);

        Thread.sleep(4);

        cache.put(3, "D"); print(cache);

        cache.get(3); // In ReplaceOldestCache method get(K key) didn't increase accessedCount

        Thread.sleep(6);

        cache.put(4, "E");

        cache.get(4);

        print(cache);



        System.out.print("\n\n\nLEAST FREQUENTLY USED CACHE:\n");




        cache = CacheFactory.getCache(3, 5, Cache.DisplacementStrategy.REPLACE_LEAST_FREQUENTLY_USED);

        cache.put(0, "A"); print(cache); cache.get(0); cache.get(0);

        cache.put(1, "B"); print(cache);

        cache.put(2, "C"); print(cache); cache.get(2); // In LFUCache accessedCount field is increase when get(K key) method is called

        Thread.sleep(4);

        cache.put(3, "D"); print(cache);

        cache.get(3);

        Thread.sleep(6);

        cache.put(4, "E");

        cache.get(4);

        print(cache);
    }

    public static void print(Cache cache) {
        synchronized (cache.cacheMap) {
            Iterator entries = cache.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                StringBuilder sb = new StringBuilder("Key: ")
                        .append(entry.getKey())
                        .append(" Value: ")
                        .append(((Cache.ValueHolder) entry.getValue()).value)
                        .append(" Accessed counter: ")
                        .append(((Cache.ValueHolder) entry.getValue()).accessedCount);
                System.out.println(sb.toString());
            }
        }
        System.out.println();
    }
}
