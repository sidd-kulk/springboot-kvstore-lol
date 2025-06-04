package com.springbootkvstore.lol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@SpringBootApplication
@RestController
public class SpringBootInMemory {

    private static final Logger log = LoggerFactory.getLogger(SpringBootInMemory.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootInMemory.class, args);
        log.info("SpringBootInMemory application started");
    }

    @Autowired
    private KVStore<String, String> kvStore;

    @GetMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key, @RequestParam String val) {
        if (StringUtils.isAnyNullOrBlank(key, val)) {
            log.warn("/set called with invalid parameters key='{}' val='{}'", key, val);
            return ResponseEntity.badRequest().build();
        }

        if (kvStore.add(key, val)) {
            log.info("Stored {}={}", key, val);
            return new ResponseEntity<>(
                    "Success",
                    HttpStatus.OK);
        } else {
            log.warn("Cache is full. Failed to store {}={}", key, val);
            return new ResponseEntity<>("Cache is full", HttpStatus.FAILED_DEPENDENCY);
        }

    }

    @GetMapping("/get")
    public ResponseEntity<String> get(@RequestParam String key) {
        if (key == null) {
            log.warn("/get called with null key");
            return ResponseEntity.badRequest().build();
        }

        String val = kvStore.get(key);
        log.info("Returning value for key {}: {}", key, val);
        return new ResponseEntity<>(
                val,
                HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<String> getAll() {
        Collection<String> all = kvStore.all();
        log.info("Returning all entries, count={}", all.size());
        return new ResponseEntity<>(
                String.join(", ", all),
                HttpStatus.OK);
    }


}
