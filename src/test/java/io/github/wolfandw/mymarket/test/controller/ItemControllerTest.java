package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.ItemController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;
}
