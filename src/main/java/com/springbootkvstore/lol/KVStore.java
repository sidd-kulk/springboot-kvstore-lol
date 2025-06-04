package com.springbootkvstore.lol;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class KVStore<K, V> {
    private static final Logger log = LoggerFactory.getLogger(KVStore.class);
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private int maxSize;
    private static final String DEFAULT_FILE = "kvstore.ser";
    private final Path persistencePath;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("kvstore-persistence");
        return t;
    });
    private final Object persistLock = new Object();
    private ScheduledFuture<?> scheduledPersist;

    public KVStore(int maxSize, String persistenceFilePath) {
        this.maxSize = maxSize;
        this.persistencePath = Path.of(persistenceFilePath);
        loadFromDisk();
    }

    public KVStore(int maxSize) {
        this(maxSize, DEFAULT_FILE);
    }

    public KVStore() {
        this(1000, DEFAULT_FILE);
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

    public synchronized boolean add(K key, V val) {
        // allow updates to existing keys even when the store reached its max size
        if (!map.containsKey(key) && map.size() >= maxSize) {
            log.warn("Store is full. Failed to add key {}", key);
            return false;
        }
        map.put(key, val);
        log.debug("Added {}={}", key, val);
        schedulePersist();
        return true;
    }

    public synchronized boolean delete(K key) {
        if (map.containsKey(key)) {
            map.remove(key);
            log.debug("Deleted key {}", key);
            schedulePersist();
            return true;
        }
        log.warn("Key {} not found for deletion", key);
        return false;
    }

    public Collection<String> all() {
        return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
    }

    public Map<K, V> asMap() {
        return Map.copyOf(map);
    }

    private void loadFromDisk() {
        if (persistencePath != null && Files.exists(persistencePath)) {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(persistencePath))) {
                Object obj = in.readObject();
                if (obj instanceof Map<?, ?> m) {
                    //noinspection unchecked
                    map.putAll((Map<K, V>) m);
                }
            } catch (IOException | ClassNotFoundException e) {
                log.warn("Failed to load persisted data", e);
            }
        }
    }

    private void persistToDisk() {
        if (persistencePath != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(persistencePath))) {
                out.writeObject(map);
            } catch (IOException e) {
                log.warn("Failed to persist data", e);
            }
        }
    }

    private void schedulePersist() {
        synchronized (persistLock) {
            if (scheduledPersist != null && !scheduledPersist.isDone()) {
                scheduledPersist.cancel(false);
            }
            scheduledPersist = scheduler.schedule(this::persistToDisk, 100, TimeUnit.MILLISECONDS);
        }
    }

    public void flush() {
        synchronized (persistLock) {
            if (scheduledPersist != null && !scheduledPersist.isDone()) {
                scheduledPersist.cancel(false);
            }
        }
        persistToDisk();
    }

    public void close() {
        flush();
        scheduler.shutdown();
    }

    @Override
    public String toString() {
        return String.join(", ", this.all());
    }
}
