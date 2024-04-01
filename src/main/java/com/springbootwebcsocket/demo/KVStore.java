package com.springbootwebcsocket.demo;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KVStore<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();

    public V get(K key) {
        return map.get(key);
    }

    public void add(K key, V val) {
        map.put(key, val);
    }

    public Collection<String> all() {
        return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.join(", ", this.all());
    }
}
