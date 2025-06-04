package com.springbootkvstore.lol;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
class SpringBootInMemoryTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private KVStore<String, String> kvStore;


    @Test
    void testSetGetAndGetAll() throws Exception {

        performAdd("key", "value")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        performAdd("key1", "value1")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        mvc.perform(MockMvcRequestBuilders.post("/set")
                        .param("key", "key"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/set")
                        .param("val", "value"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());


        mvc.perform(MockMvcRequestBuilders.get("/get")
                        .param("key", "key"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("value"));


        mvc.perform(MockMvcRequestBuilders.get("/get-all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("key1=value1, key=value"));
    }

    @Test
    void testSetGetAndGetAllCacheFull() throws Exception {
        IntStream.range(1, 1001).forEach(i -> {
            try {
                performAdd(String.valueOf(i), String.valueOf(i));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        performAdd("key1", "value1")
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void testUpdateExistingWhenFull() throws Exception {
        IntStream.range(1, 1001).forEach(i -> {
            try {
                performAdd(String.valueOf(i), String.valueOf(i));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        performAdd("500", "new")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        mvc.perform(MockMvcRequestBuilders.get("/get")
                        .param("key", "500"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("new"));
    }

    @Test
    void testDelete() throws Exception {
        performAdd("key", "value")
                .andExpect(MockMvcResultMatchers.status().isOk());

        performDelete("key")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        performDelete("missing")
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private ResultActions performAdd(String key, String value) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/set")
                .param("key", key)
                .param("val", value));
    }

    private ResultActions performDelete(String key) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/delete")
                .param("key", key));
    }
}