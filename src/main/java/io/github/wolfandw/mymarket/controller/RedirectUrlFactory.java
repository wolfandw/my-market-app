package io.github.wolfandw.mymarket.controller;

import org.jspecify.annotations.NonNull;

/**
 * Фабрика адресов переходов.
 */
public class RedirectUrlFactory {
    private static final String REDIRECT = "redirect:";

    private static final String TEMPLATE_ITEMS = "items";
    private static final String TEMPLATE_ORDERS = "orders";
    private static final String TEMPLATE_CART = "cart/items";

    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_SORT = "sort";
    private static final String PARAMETER_PAGE_NUMBER = "pageNumber";
    private static final String PARAMETER_PAGE_SIZE = "pageSize";
    private static final String PARAMETER_NEW_ITEM = "newItem";
    private static final String PARAMETER_NEW_ORDER = "newOrder";

    private RedirectUrlFactory() {
        // Private
    }

    /**
     * Создает адрес перехода на страницу товаров с отборами и поиском.
     *
     * @param search параметры поиска
     * @param sort параметры сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return адрес перехода на страницу товаров с отборами и поиском.
     */
    public static @NonNull String createUrlToItems(String search, String sort, Integer pageNumber, Integer pageSize) {

        StringBuilder redirect = new StringBuilder();
        redirect.append('/').append(TEMPLATE_ITEMS);
        redirect.append('?');
        redirect.append(PARAMETER_SEARCH).append('=');
        if (search != null) {
            redirect.append(search);
        }
        redirect.append('&').append(PARAMETER_SORT).append('=');
        if (sort != null) {
            redirect.append(sort);
        }
        redirect.append('&').append(PARAMETER_PAGE_NUMBER).append('=');
        if (pageNumber != null) {
            redirect.append(pageNumber);
        }
        redirect.append('&').append(PARAMETER_PAGE_SIZE).append('=');
        if (pageSize != null) {
            redirect.append(pageSize);
        }
        return redirect.toString();
    }

    /**
     * Создает редирект на адрес перехода на страницу товаров с отборами и поиском.
     *
     * @param search параметры поиска
     * @param sort параметры сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return редирект на адрес перехода на страницу товаров с отборами и поиском.
     */
    public static @NonNull String createRedirectUrlToItems(String search, String sort, Integer pageNumber, Integer pageSize) {
        return REDIRECT + createUrlToItems(search, sort, pageNumber, pageSize);
    }

    /**
     * Создает адрес перехода на страницу нового товара.
     *
     * @param newItemId идентификатор нового товара
     * @return адрес перехода на страницу нового товара
     */
    public static @NonNull String createUrlToNewItem(Long newItemId) {
        return '/' + TEMPLATE_ITEMS +
                '/' +  newItemId +
                '?' + PARAMETER_NEW_ITEM + '=' + Boolean.TRUE;
    }

    /**
     * Создает редирект на адрес перехода на страницу нового товара.
     *
     * @param newItemId идентификатор нового товара
     * @return редирект на адрес перехода на страницу нового товара
     */
    public static @NonNull String createRedirectUrlToNewItem(Long newItemId) {
        return REDIRECT + createUrlToNewItem(newItemId);
    }

    /**
     * Создает адрес начальной страницы.
     *
     * @return адрес перехода на начальную страницу
     */
    public static @NonNull String createUrlToItems() {
        return '/' + TEMPLATE_ITEMS;
    }

    /**
     * Создает редирект на адрес начальной страницы.
     *
     * @return редирект на адрес перехода на начальную страницу
     */
    public static @NonNull String createRedirectUrlToItems() {
        return REDIRECT + createUrlToItems();
    }

    /**
     * Создает редирект на адрес страницы заказов.
     *
     * @return редирект на адрес перехода на страницу заказов
     */
    public static @NonNull String createRedirectUrlToOrders() {
        return REDIRECT + '/' + TEMPLATE_ORDERS;
    }

    /**
     * Создает адрес страницу нового заказа.
     *
     * @return адрес перехода на страницу нового заказа
     */
    public static @NonNull String createUrlToNewOrder(Long newOrderId) {
        return '/' + TEMPLATE_ORDERS +
               '/' + newOrderId + '?' + PARAMETER_NEW_ORDER + '=' + Boolean.TRUE;
    }

    /**
     * Создает редирект на адрес страницу нового заказа.
     *
     * @return редирект на адрес перехода на страницу нового заказа
     */
    public static @NonNull String createRedirectUrlToNewOrder(Long newOrderId) {
        return REDIRECT + createUrlToNewOrder(newOrderId);
    }

    /**
     * Создает адрес перехода на страницу товара.
     *
     * @param itemId идентификатор товара
     * @return адрес перехода на страницу товара
     */
    public static @NonNull String createUrlToItem(Long itemId) {
        return '/' + TEMPLATE_ITEMS + '/' +  itemId;
    }

    /**
     * Создает редирект на адрес перехода на страницу товара.
     *
     * @param itemId идентификатор товара
     * @return редирект на адрес перехода на страницу товара
     */
    public static @NonNull String createRedirectUrlToItem(Long itemId) {
        return REDIRECT + createUrlToItem(itemId);
    }

    /**
     * Создает адрес перехода на страницу корзины.
     *
     * @param cartId идентификатор корзины
     * @return адрес перехода на страницу корзины
     */
    public static @NonNull String createUrlToCart(Long cartId) {
        return '/' + TEMPLATE_CART;
    }

    /**
     * Создает редирект на адрес перехода на страницу корзины.
     *
     * @param cartId идентификатор корзины
     * @return редирект на адрес перехода на страницу корзины
     */
    public static @NonNull String createRedirectUrlToCart(Long cartId) {
        return REDIRECT + createUrlToCart(cartId);
    }
}
