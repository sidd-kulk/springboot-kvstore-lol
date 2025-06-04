package com.springbootkvstore.lol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class KVStorePersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void testPersistence() {
        Path file = tempDir.resolve("store.ser");
        KVStore<String, String> kv = new KVStore<>(10, file.toString());
        kv.add("a", "b");
        kv.add("c", "d");
        kv.flush();

        KVStore<String, String> kv2 = new KVStore<>(10, file.toString());
        assertEquals("b", kv2.get("a"));
        assertEquals("d", kv2.get("c"));

        kv2.delete("a");
        kv2.flush();

        KVStore<String, String> kv3 = new KVStore<>(10, file.toString());
        assertNull(kv3.get("a"));
        assertEquals("d", kv3.get("c"));
    }
}
