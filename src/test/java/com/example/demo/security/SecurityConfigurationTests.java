package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class SecurityConfigurationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateUserRequest> createJson;

    @Autowired
    private JacksonTester<LoginRequest> loginJson;

    @Autowired
    private JacksonTester<User> userJson;

    @Autowired
    UserController userController;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void loginValidateJWT() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("BarakObama");
        userRequest.setPassword("84Ttdpnend#");
        userRequest.setConfirmPassword("84Ttdpnend#");

        LoginRequest loginRequest = new LoginRequest();
        BeanUtils.copyProperties(userRequest, loginRequest);

        mvc.perform(post(new URI("/api/user/create"))
                        .content(createJson.write(userRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        String token = mvc.perform(post(new URI("/login"))
                        .content(loginJson.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                .getSubject();

        assertEquals(userRequest.getUsername(), user);

    }

    @Test
    public void attemptLoginBadPassword() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("VaclavHavel");
        userRequest.setPassword("84Ttdpnend#");
        userRequest.setConfirmPassword("84Ttdpnend#");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(userRequest.getUsername());
        loginRequest.setPassword("OOPSThisIsn*tRight");

        mvc.perform(post(new URI("/api/user/create"))
                        .content(createJson.write(userRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        mvc.perform(post(new URI("/login"))
                        .content(loginJson.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void unauthorizedRequest() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("OneWhoNeedsInfo");
        userRequest.setPassword("&*(ASDasnd323");
        userRequest.setConfirmPassword("&*(ASDasnd323");

        LoginRequest loginRequest = new LoginRequest();
        BeanUtils.copyProperties(userRequest, loginRequest);

        mvc.perform(post(new URI("/api/user/create"))
                        .content(createJson.write(userRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // attempt to access the api without a token
        mvc.perform(
                        get(new URI("/api/user/" + userRequest.getUsername()))
                                .content(createJson.write(userRequest).getJson())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    @Test
    public void authorizedRequest() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("AnotherInfoSeeker");
        userRequest.setPassword("(*&&7898adlkfBEND)");
        userRequest.setConfirmPassword("(*&&7898adlkfBEND)");

        LoginRequest loginRequest = new LoginRequest();
        BeanUtils.copyProperties(userRequest, loginRequest);

        mvc.perform(post(new URI("/api/user/create"))
                        .content(createJson.write(userRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        String token = mvc.perform(post(new URI("/login"))
                        .content(loginJson.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        String responseContent = mvc.perform(
                        get(new URI("/api/user/" + userRequest.getUsername()))
                                .content(createJson.write(userRequest).getJson())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .header("Authorization", token)
                                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = userJson.parse(responseContent).getObject();
        assertEquals(userRequest.getUsername(), user.getUsername());
    }

    @Test
    public void unauthorizedRequestBadToken() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("LostMyTokenGuy");
        userRequest.setPassword("(*&&7898adlkfBEND)");
        userRequest.setConfirmPassword("(*&&7898adlkfBEND)");

        LoginRequest loginRequest = new LoginRequest();
        BeanUtils.copyProperties(userRequest, loginRequest);

        mvc.perform(post(new URI("/api/user/create"))
                        .content(createJson.write(userRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        String token = mvc.perform(post(new URI("/login"))
                        .content(loginJson.write(loginRequest).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        assert token != null;
        char first = token.charAt(10);
        char replacement = (first != '0') ? '0' : '1';
        String badToken = token.replace(first, replacement);

        mvc.perform(
                        get(new URI("/api/user/" + userRequest.getUsername()))
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .header("Authorization", badToken)
                                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }
}