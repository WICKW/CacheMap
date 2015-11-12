package org.manuna;

public class CacheFactory {

    public static Cache getCache(Cache.DisplacementStrategy strategy) {
        if (strategy == Cache.DisplacementStrategy.REPLACE_OLDEST) {
            return new ReplaceOldestCache();
        }
        if (strategy == Cache.DisplacementStrategy.REPLACE_LEAST_FREQUENTLY_USED) {
            return new LFUCache();
        }
        return null;
    }

    public static Cache getCache(int capacity, long lifetime, Cache.DisplacementStrategy strategy) {
        if (strategy == Cache.DisplacementStrategy.REPLACE_OLDEST) {
            return new ReplaceOldestCache(capacity, lifetime);
        }
        if (strategy == Cache.DisplacementStrategy.REPLACE_LEAST_FREQUENTLY_USED) {
            return new LFUCache(capacity, lifetime);
        }
        return null;
    }
}
