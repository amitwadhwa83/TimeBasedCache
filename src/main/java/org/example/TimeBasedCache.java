package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.util.*;

public class TimeBasedCache<K, V> {
    private final Map<K, CacheEntry<V>> cache;
    private final Queue<K> keyQueue;
    private final long cleanupIntervalMs;
    private final int maxSize;

    public TimeBasedCache(long cleanupIntervalMs, int maxSize) {
        this.cache = new HashMap<>();
        this.keyQueue = new LinkedList<>();
        this.cleanupIntervalMs = cleanupIntervalMs;
        this.maxSize = maxSize;
        startCleaner();
    }

    public void put(K key, V value, long ttlMs) {
        if (cache.size() >= maxSize) {
            evictOldest();
        }
        long expiryTime = System.currentTimeMillis() + ttlMs;
        cache.put(key, new CacheEntry<>(value, expiryTime));
        keyQueue.offer(key);
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.getValue();
        }
        cache.remove(key); // Remove expired entry
        keyQueue.remove(key);
        return null;
    }

    public void remove(K key) {
        if (cache.containsKey(key)) {
            cache.remove(key);
            keyQueue.remove(key);
        }
    }

    public void clear() {
        cache.clear();
        keyQueue.clear();
    }

    private void evictOldest() {
        K oldestKey = keyQueue.poll();
        if (oldestKey != null) {
            cache.remove(oldestKey);
        }
    }

    private void startCleaner() {
        Thread cleanerThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(cleanupIntervalMs);
                    removeExpiredEntries();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }

    private void removeExpiredEntries() {
        long now = System.currentTimeMillis();
        Iterator<K> iterator = keyQueue.iterator();
        while (iterator.hasNext()) {
            K key = iterator.next();
            CacheEntry<V> entry = cache.get(key);
            if (entry != null && entry.getExpiryTime() <= now) {
                iterator.remove();
                cache.remove(key);
            }
        }
    }

    private static class CacheEntry<V> {
        private final V value;
        private final long expiryTime;

        CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public V getValue() {
            return value;
        }

        public long getExpiryTime() {
            return expiryTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(5000, 3);

        cache.put("key1", "value1", 2000);
        cache.put("key2", "value2", 2000);
        cache.put("key3", "value3", 2000);

        System.out.println("Get key1: " + cache.get("key1")); // Should print "value1"

        cache.put("key4", "value4", 2000); // Evicts "key1" as the cache size exceeds maxSize

        System.out.println("Get key1 after eviction: " + cache.get("key1")); // Should print "null"

        Thread.sleep(3000);
        System.out.println("Get key2 after expiry: " + cache.get("key2")); // Should print "null"
    }
}
