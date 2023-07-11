package com.example.demo.controllers;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static com.example.demo.TestUtils.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setup(){
        User user = createUser();
        Item item = createItem(1);
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

    }

    @Test
    public void testAddToCart(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1);
        request.setQuantity(3);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();

        Cart generatedCart = createCart(createUser());

        assertNotNull(actualCart);

        Item item = createItem(request.getItemId());
        BigDecimal itemPrice = item.getPrice();

        BigDecimal expectedTotal = itemPrice.multiply(BigDecimal.valueOf(request.getQuantity())).add(generatedCart.getTotal());

        assertEquals("testuser", actualCart.getUser().getUsername());
        assertEquals(generatedCart.getItems().size() + request.getQuantity(), actualCart.getItems().size());
        assertEquals(createItem(1), actualCart.getItems().get(0));
        assertEquals(expectedTotal, actualCart.getTotal());

        verify(cartRepository, times(1)).save(actualCart);

    }

    @Test
    public void testRemoveFromCart(){

        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("testuser");

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();

        Cart generatedCart = createCart(createUser());

        assertNotNull(actualCart);

        Item item = createItem(request.getItemId());
        BigDecimal itemPrice = item.getPrice();

        BigDecimal expectedTotal = generatedCart.getTotal().subtract(itemPrice.multiply(BigDecimal.valueOf(request.getQuantity())));

        assertEquals("testuser", actualCart.getUser().getUsername());
        assertEquals(generatedCart.getItems().size() - request.getQuantity(), actualCart.getItems().size());
        assertEquals(createItem(2), actualCart.getItems().get(0));
        assertEquals(expectedTotal, actualCart.getTotal());

        verify(cartRepository, times(1)).save(actualCart);

    }

}