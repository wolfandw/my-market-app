package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.OrderDto;
import io.github.wolfandw.mymarket.dto.UserInfoDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.model.User;
import io.github.wolfandw.mymarket.service.*;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Абстрактный модельный тест контроллеров.
 */
public class AbstractControllerTest extends AbstractTest {
    @Autowired
    protected WebTestClient webTestClient;

    @MockitoBean
    protected ItemToDtoMapper itemToDtoMapper;

    @MockitoBean
    protected ItemService itemService;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected FileStorageService fileStorageService;

    @MockitoBean
    protected EntityImageService entityImageService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected BuyService buyService;

    @MockitoBean
    protected PaymentsService paymentsService;

    @MockitoBean
    protected UserService userService;

    /**
     * Маппит модельный товар на его DTO-представление.
     *
     * @param item модельный товар
     * @param count количество в корзине
     * @return DTO-представление товара
     */
    protected ItemDto mapItem(Item item, int count) {
        return new ItemDto(item.getId(), item.getTitle(), item.getDescription(), item.getPrice().longValue(), count);
    }

    /**
     * Маппит модельный заказ на DTO-представление заказа
     * @param order заказ
     * @return DTO-представление заказа
     */
    protected OrderDto mapOrder(Order order) {
        return new OrderDto(order.getId(), mapOrderItems(ORDER_ITEMS.get(order.getId()).values().stream().toList()), order.getTotalSum().longValue());
    }

    protected List<ItemDto> mapOrderItems(List<OrderItem> orderItems) {
        return orderItems.stream().map(orderItem -> mapItem(ITEMS.get(orderItem.getItemId()),
                orderItem.getCount())).toList();
    }

    protected User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");
        user.setRoles("USER");
        return user;
    }

    protected Mono<UserInfoDto> getUserInfo() {
        return Mono.just(new UserInfoDto(1L, "user", true, false));
    }

    protected User getAdmin() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("password");
        user.setRoles("ADMIN");
        return user;
    }

    protected Mono<UserInfoDto> getAdminInfo() {
        return Mono.just(new UserInfoDto(1L, "admin", true, true));
    }

    protected Mono<UserInfoDto> getGuestInfo() {
        return Mono.just(new UserInfoDto(-1L, "guest", false, false));
    }
}
