import org.example.TimeBasedCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBasedCacheRegressionTest {

    @Test
    public void testPutAndGet() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        cache.put("key1", "value1", 2000);
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    public void testExpiry() throws InterruptedException {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        cache.put("key1", "value1", 1000);
        Thread.sleep(1500);
        assertNull(cache.get("key1"));
    }

    @Test
    public void testEvictionFIFO() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 2);
        cache.put("key1", "value1", 5000);
        cache.put("key2", "value2", 5000);
        cache.put("key3", "value3", 5000); // Evicts "key1"

        assertNull(cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
    }

    @Test
    public void testRemove() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        cache.put("key1", "value1", 2000);
        cache.remove("key1");
        assertNull(cache.get("key1"));
    }

    @Test
    public void testClear() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        cache.put("key1", "value1", 2000);
        cache.put("key2", "value2", 2000);
        cache.clear();
        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
    }

    @Test
    public void testMultipleKeys() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        cache.put("key1", "value1", 5000);
        cache.put("key2", "value2", 5000);
        assertEquals("value1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
    }

    @Test
    public void testUpdateExistingKey() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        cache.put("key1", "value1", 2000);
        cache.put("key1", "value2", 2000); // Update the value
        assertEquals("value2", cache.get("key1"));
    }

    @Test
    public void testCacheSizeLimit() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 2);
        cache.put("key1", "value1", 5000);
        cache.put("key2", "value2", 5000);
        cache.put("key3", "value3", 5000); // Should evict "key1"
        assertNull(cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
    }

    @Test
    public void testCleanupInterval() throws InterruptedException {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(500, 3);
        cache.put("key1", "value1", 1000);
        Thread.sleep(1500); // Wait for the entry to expire and be cleaned up
        assertNull(cache.get("key1"));
    }

    @Test
    public void testNonExistentKey() {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        assertNull(cache.get("nonExistentKey"));
    }

    @Test
    public void testExpiryTimeUpdate() throws InterruptedException {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);
        cache.put("key1", "value1", 1000);
        cache.put("key1", "value1", 3000); // Update the expiry time
        Thread.sleep(1500);
        assertEquals("value1", cache.get("key1")); // Should not be expired
    }
}