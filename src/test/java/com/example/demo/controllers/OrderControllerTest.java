package com.example.demo.controllers;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import static com.example.demo.TestUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    
    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Before
    public void setup(){
        User user = createUser();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.findByUser(any())).thenReturn(createOrders());
    }

    @Test
    public void testSubmit(){

        ResponseEntity<UserOrder> response = orderController.submit("testuser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();

        assertEquals(createItems(), order.getItems());
        assertEquals(createUser().getId(), order.getUser().getId());


        verify(orderRepository, times(1)).save(order);

    }

    @Test
    public void verify_submit_invalid(){

        ResponseEntity<UserOrder> response = orderController.submit("invalid username");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        assertNull( response.getBody());

        verify(userRepository, times(1)).findByUsername("invalid username");
    }

    @Test
    public void testGetOrdersForUser(){

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();


        assertEquals(createOrders().size(), orders.size());

    }
}
