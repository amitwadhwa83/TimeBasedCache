import org.example.TimeBasedCache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TimeBasedCacheTest {

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
    public void testCacheIntegration() throws InterruptedException {
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 3);

        // Simulate adding entries
        cache.put("key1", "value1", 2000);
        cache.put("key2", "value2", 3000);
        cache.put("key3", "value3", 4000);

        // Verify entries are added
        assertEquals("value1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));

        // Wait for some entries to expire
        Thread.sleep(2500);

        // Verify expired entries are removed
        assertNull(cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));

        // Add another entry to trigger eviction
        cache.put("key4", "value4", 2000);

        // Verify eviction of the oldest entry
        assertNull(cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
        assertEquals("value4", cache.get("key4"));
    }


}
