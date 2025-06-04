package com.springbootkvstore.lol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class KVStoreTest {

    private KVStore<String, String> kvStore;

    @BeforeEach
    void setUp() {
         kvStore = new KVStore<>();
    }

    @Test
    void testGet() {
        kvStore.add("key", "value");
        assertEquals("value", kvStore.get("key"));
    }

    @Test
    void testAll() {
        kvStore.add("key", "value");
        kvStore.add("key1", "value1");
        assertEquals(2, kvStore.all().size());
    }

    @Test
    void testSize(){
        kvStore = new KVStore<>(1);
        kvStore.add("key", "value");
        kvStore.add("key1", "value1");
        assertEquals(1, kvStore.all().size());
        assertEquals("value", kvStore.get("key"));
        assertNull(kvStore.get("key1"));
    }

    @Test
    void testUpdateWhenFull(){
        kvStore = new KVStore<>(1);
        assertTrue(kvStore.add("key", "value"));
        // store is full now but updating existing key should still succeed
        assertTrue(kvStore.add("key", "newValue"));
        assertEquals("newValue", kvStore.get("key"));
    }

    @Test
    void testDelete() {
        kvStore.add("key", "value");
        assertTrue(kvStore.delete("key"));
        assertNull(kvStore.get("key"));
        assertFalse(kvStore.delete("missing"));
    }

    @Test
    void testConcurrentAddDoesNotExceedMaxSize() throws InterruptedException {
        int max = 50;
        kvStore = new KVStore<>(max);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        IntStream.range(0, max * 2).forEach(i ->
                executor.submit(() -> kvStore.add("k" + i, "v" + i))
        );
        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

        assertTrue(kvStore.all().size() <= max);
    }
}
