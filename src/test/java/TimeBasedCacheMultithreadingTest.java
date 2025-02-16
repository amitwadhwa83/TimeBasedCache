import org.example.TimeBasedCache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBasedCacheMultithreadingTest {

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        final int threadCount = 10;
        final int operationsPerThread = 100;
        TimeBasedCache<String, String> cache = new TimeBasedCache<>(1000, 1000);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "key" + threadId + "_" + j;
                        String value = "value" + threadId + "_" + j;
                        cache.put(key, value, 5000);
                        assertEquals(value, cache.get(key));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Verify that all entries are present
        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < operationsPerThread; j++) {
                String key = "key" + i + "_" + j;
                String value = "value" + i + "_" + j;
                assertEquals(value, cache.get(key));
            }
        }
    }
}