package com.springbootkvstore.lol;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KVStore<K, V> {
    private static final Logger log = LoggerFactory.getLogger(KVStore.class);
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private int maxSize;

    public KVStore(int maxSize) {
        this.maxSize = maxSize;
    }

    public KVStore() {
        this.maxSize = 1000;
    }

    public V get(K key) {
        V val = map.get(key);
        if (val == null) {
            log.warn("Key {} not found", key);
        } else {
            log.debug("Retrieved {}={}", key, val);
        }
        return val;
    }

    public boolean add(K key, V val) {
        // allow updates to existing keys even when the store reached its max size
        if (!map.containsKey(key) && map.size() >= maxSize) {
            log.warn("Store is full. Failed to add key {}", key);
            return false;
        }
        map.put(key, val);
        log.debug("Added {}={}", key, val);
        return true;
    }

    public Collection<String> all() {
        return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.join(", ", this.all());
    }
}
