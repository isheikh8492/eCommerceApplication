package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.UsernameNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @BeforeEach
    public void beforeEach() {
        // inject needed Repositories.
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController,"orderRepository", orderRepository);
    }

    @Test
    public void SubmitOrder() {

        String username = "MPhelps";
        User user = new User();

        BigDecimal itemPrice = new BigDecimal(100D);
        String itemName = "Fancy Microwave";

        Item item = new Item();
        item.setPrice(itemPrice);
        item.setName(itemName);
        item.setDescription(item.getDescription());
        item.setId(1L);

        List<Item> cartItems = new ArrayList<>();
        cartItems.add(item);
        BigDecimal cartTotal = new BigDecimal(100D);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotal(cartTotal);
        cart.setItems(cartItems);

        user.setCart(cart);

        when(userRepository.findByUsername(username)).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit(username);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder returned = response.getBody();
        assertNotNull(returned);

        assertEquals(cartItems, returned.getItems());
        assertEquals(cartTotal, returned.getTotal());
        assertEquals(user, returned.getUser());
    }

    @Test
    public void getOrderListByUsername() {
        Cart cart = new Cart();
        String username = "JujuBean";
        User user = new User();
        UserOrder order = new UserOrder();
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> returned = response.getBody();
        assertNotNull(returned);
    }

    @ParameterizedTest
    @NullSource
    public void FailToCreateOrderBadUsername(String username) {
        when(userRepository.findByUsername(username)).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {orderController.submit(username);});
    }

    @ParameterizedTest
    @NullSource
    public void FailToGetOrderBadUsername(String username) {
        when(userRepository.findByUsername(username)).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {orderController.getOrdersForUser(username);});
    }
}
