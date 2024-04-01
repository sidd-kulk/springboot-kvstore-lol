package com.springbootkvstore.lol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@SpringBootApplication
@RestController
public class SpringBootInMemory {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootInMemory.class, args);
    }

    @Autowired
    private KVStore<String, String> kvStore;

    @GetMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key, @RequestParam String val) {
        if (StringUtils.isAnyNullOrBlank(key, val)) {
            return ResponseEntity.badRequest().build();
        }

        kvStore.add(key, val);
        return new ResponseEntity<>(
                "Success",
                HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<String> get(@RequestParam String key) {
        if (key == null) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(
                kvStore.get(key),
                HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<String> getAll() {
        Collection<String> all = kvStore.all();
        return new ResponseEntity<>(
                String.join(", ", all),
                HttpStatus.OK);
    }


}