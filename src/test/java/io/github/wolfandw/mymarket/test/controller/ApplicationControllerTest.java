package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.ApplicationController;
import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Модульный тест контроллера приложения.
 */
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest extends AbstractControllerTest {
    @Test
    void redirectToItemsTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RedirectUrlFactory.createUrlToItems()));
    }

    @Test
    void buyTest() throws Exception {
        Long orderId = 2L;

        Cart cart = CARTS.get(DEFAULT_CART_ID);

        Order order = new Order(orderId);
        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem ->
                new OrderItem(order, cartItem.getItem(), cartItem.getCount())).toList();
        order.setItems(orderItems);
        order.setTotalSum(cart.getTotal());

        Optional<OrderDto> orderDto = Optional.of(mapOrder(order));
        when(buyService.buy(DEFAULT_CART_ID)).thenReturn(orderDto);

        mockMvc.perform(post("/buy"))
                .andExpect(status().isFound())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(RedirectUrlFactory.createUrlToNewOrder(2L)));
    }
}
