package com.springbootkvstore.lol;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SpringBootInMemoryTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private KVStore<String, String> kvStore;


    @Test
    void testSetGetAndGetAll() throws Exception {
        String key = "testKey";
        String value = "testValue";


        performGet(key, value)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        mvc.perform(MockMvcRequestBuilders.get("/set")
                        .param("key", key))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/set")
                        .param("val", value))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());


        mvc.perform(MockMvcRequestBuilders.get("/get")
                        .param("key", key))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(value));

        mvc.perform(MockMvcRequestBuilders.get("/get-all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Size = "+1));
    }

    private ResultActions performGet(String key, String value) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/set")
                .param("key", key)
                .param("val", value));
    }
}