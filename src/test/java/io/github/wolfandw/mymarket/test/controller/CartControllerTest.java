//package io.github.wolfandw.mymarket.test.controller;
//
//import io.github.wolfandw.mymarket.controller.CartController;
//import io.github.wolfandw.mymarket.dto.CartDto;
//import io.github.wolfandw.mymarket.dto.ItemDto;
//import io.github.wolfandw.mymarket.model.Cart;
//import io.github.wolfandw.mymarket.model.CartItem;
//import io.github.wolfandw.mymarket.model.Item;
//import org.hamcrest.collection.IsCollectionWithSize;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.hamcrest.core.IsEqual.equalTo;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Модульный тест контроллера корзин.
// */
//@WebMvcTest(CartController.class)
//public class CartControllerTest extends AbstractControllerTest{
//    private static final String TEMPLATE_CART = "cart";
//
//    private static final String ATTRIBUTE_ITEMS = "items";
//    private static final String ATTRIBUTE_TOTAL = "total";
//
//    private static final String PARAMETER_ID = "id";
//    private static final String PARAMETER_ACTION = "action";
//
//    private static final String ACTION_PLUS = "PLUS";
//
//    @Test
//    void getCartTest() throws Exception {
//        Cart cart = CARTS.get(DEFAULT_CART_ID);
//        CartDto cartDto = new CartDto(mapCartItems(cart.getItems()), cart.getTotal().longValue());
//        when(cartService.getCart(DEFAULT_CART_ID)).thenReturn(cartDto);
//        when(entityImageService.getEntityImageBase64(Mockito.any(Long.class))).thenReturn("");
//
//        mockMvc.perform(get("/cart/items"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists(ATTRIBUTE_ITEMS))
//                .andExpect(model().attribute(ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(12)))
//                .andExpect(model().attributeExists(ATTRIBUTE_TOTAL))
//                .andExpect(model().attribute(ATTRIBUTE_TOTAL, equalTo(7700L)))
//                .andExpect(view().name(TEMPLATE_CART));
//    }
//
//    @Test
//    void changeChartItemCountTest() throws Exception {
//        Long itemId = 1L;
//
//        Cart cart = CARTS.get(DEFAULT_CART_ID);
//        Item item = ITEMS.get(itemId);
//
//        cart.getItems().add(new CartItem(13L, cart, item, 1));
//        cart.setTotal(cart.getTotal().add(item.getPrice()));
//
//        CartDto cartDto = new CartDto(mapCartItems(cart.getItems()), cart.getTotal().longValue());
//        when(cartService.getCart(DEFAULT_CART_ID)).thenReturn(cartDto);
//        when(entityImageService.getEntityImageBase64(Mockito.any(Long.class))).thenReturn("");
//
//        mockMvc.perform(post("/cart/items")
//                        .param(PARAMETER_ID, itemId.toString())
//                        .param(PARAMETER_ACTION, ACTION_PLUS))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists(ATTRIBUTE_ITEMS))
//                .andExpect(model().attribute(ATTRIBUTE_ITEMS, IsCollectionWithSize.<List<ItemDto>>hasSize(13)))
//                .andExpect(model().attributeExists(ATTRIBUTE_TOTAL))
//                .andExpect(model().attribute(ATTRIBUTE_TOTAL, equalTo(7707L)))
//                .andExpect(view().name(TEMPLATE_CART));
//
//        verify(cartService).changeItemCount(DEFAULT_CART_ID, itemId, ACTION_PLUS);
//    }
//
//    private List<ItemDto> mapCartItems(List<CartItem> cartItems) {
//        return cartItems.stream().map(ci -> mapItem(ci.getItem(),
//                ci.getCount())).collect(Collectors.toList());
//    }
//}
