package com.springbootkvstore.lol;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KVStoreConcurrencyTest {

    @Test
    void testConcurrentAddDoesNotExceedMaxSize() throws InterruptedException {
        int maxSize = 100;
        KVStore<String, String> kvStore = new KVStore<>(maxSize);

        int threads = 20;
        int addsPerThread = 10; // 200 total attempts > maxSize
        ExecutorService service = Executors.newFixedThreadPool(threads);

        for (int t = 0; t < threads; t++) {
            int threadIndex = t;
            service.execute(() -> {
                for (int i = 0; i < addsPerThread; i++) {
                    String key = "t" + threadIndex + "k" + i;
                    kvStore.add(key, "val" + i);
                }
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);

        assertTrue(kvStore.all().size() <= maxSize, "Store exceeded max size under concurrency");
    }
}
