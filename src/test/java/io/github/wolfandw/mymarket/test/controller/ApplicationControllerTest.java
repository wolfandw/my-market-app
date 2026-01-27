package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.ApplicationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {
    @Autowired
    MockMvc mockMvc;
}
