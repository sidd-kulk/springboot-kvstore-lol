package com.springbootkvstore.lol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void testConcurrentAddRespectMaxSize() throws InterruptedException {
        kvStore = new KVStore<>(1);

        Runnable addTask = () -> kvStore.add("key1", "value1");

        Thread t1 = new Thread(addTask);
        Thread t2 = new Thread(addTask);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(1, kvStore.all().size());
    }
}
