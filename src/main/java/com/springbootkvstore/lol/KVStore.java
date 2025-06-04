package com.springbootkvstore.lol;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KVStore<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private int maxSize;

    public KVStore(int maxSize) {
        this.maxSize = maxSize;
    }

    public KVStore() {
        this.maxSize = 1000;
    }

    public V get(K key) {
        return map.get(key);
    }

    public synchronized boolean add(K key, V val) {
        // allow updates to existing keys even when the store reached its max size
        if (!map.containsKey(key) && map.size() >= maxSize) {
            return false;
        }
        map.put(key, val);
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
