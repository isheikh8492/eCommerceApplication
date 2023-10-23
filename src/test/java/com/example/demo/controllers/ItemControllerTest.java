package com.example.demo.controllers;

import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.net.URI;
import static org.aspectj.bridge.MessageUtil.fail;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@Transactional
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);

    private String token = "";

    @Before
    public void login() throws Exception {
        token = doLoginWithBearer();
    }


    @Autowired
    private JacksonTester<CreateUserRequest> json;


    public void createUser() throws Exception {
        String username = "isheikh8492";
        String password = "YoP@sswd123";
        String confirmPassword = "YoP@sswd123";

        CreateUserRequest testUserRequest = new CreateUserRequest(username, password, confirmPassword);
        mvc.perform(post(new URI("/api/user/create"))
                        .content(json.write(testUserRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").exists());
    }
    @Test
    public void getItemList() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        mvc.perform(get(new URI("/api/item"))
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void getItemById() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        mvc.perform(get(new URI("/api/item/1"))
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    public void getItemByName() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        String itemName = "Round Widget";

        mvc.perform(get(new URI("/api/item/name"))
                        .param("name", itemName)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name").value(itemName));
    }

    public String doLoginWithBearer() throws Exception {
        createUser();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("username", "isheikh8492");
        requestBody.put("password", "YoP@sswd123");
        MvcResult result = mvc.perform(post(new URI("/login"))
                        .content(requestBody.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn();

        String bearerToken = "";
        String authorizationHeader = result.getResponse().getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            bearerToken = authorizationHeader.substring(7);
        }
        return bearerToken;
    }
}
