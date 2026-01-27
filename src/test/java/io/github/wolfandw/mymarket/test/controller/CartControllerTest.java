package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.CartController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
public class CartControllerTest {
    @Autowired
    MockMvc mockMvc;
}
