//package io.github.wolfandw.mymarket.itest.controller;
//
//import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
//import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
//import org.junit.jupiter.api.Test;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * Интеграционные тесты контроллера приложения.
// */
//public class ApplicationControllerIntegrationTest extends AbstractIntegrationTest {
//    @Test
//    void redirectToItemsTest() throws Exception {
//        mockMvc.perform(get("/"))
//                .andExpect(status().isFound())
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(RedirectUrlFactory.createUrlToItems()));
//    }
//
//    @Test
//    @Transactional
//    void buyTest() throws Exception {
//        mockMvc.perform(post("/buy"))
//                .andExpect(status().isFound())
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(RedirectUrlFactory.createUrlToNewOrder(3L)));
//    }
//}
