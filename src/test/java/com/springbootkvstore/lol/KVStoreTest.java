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
    void get() {
        kvStore.add("key", "value");
        assertEquals("value", kvStore.get("key1"));
    }

    @Test
    void all() {
        kvStore.add("key", "value");
        kvStore.add("key1", "value1");
        assertEquals(2, kvStore.all().size());
    }
}
