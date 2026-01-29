package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.DtoConstants;
import org.jspecify.annotations.NonNull;

/**
 * Фабрика адресов переходов.
 */
public class RedirectUrlFactory {
    /*
     * Редирект.
     */
    private static final String REDIRECT = "redirect:";

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
        redirect.append('/').append(DtoConstants.TEMPLATE_ITEMS);
        redirect.append('?');
        redirect.append(DtoConstants.PARAMETER_SEARCH).append('=');
        if (search != null) {
            redirect.append(search);
        }
        redirect.append('&').append(DtoConstants.PARAMETER_SORT).append('=');
        if (sort != null) {
            redirect.append(sort);
        }
        redirect.append('&').append(DtoConstants.PARAMETER_PAGE_NUMBER).append('=');
        if (pageNumber != null) {
            redirect.append(pageNumber);
        }
        redirect.append('&').append(DtoConstants.PARAMETER_PAGE_SIZE).append('=');
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
        return '/' + DtoConstants.TEMPLATE_ITEMS +
                '/' +  newItemId +
                '?' + DtoConstants.PARAMETER_NEW_ITEM + '=' + Boolean.TRUE;
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
     * @return адрес перехода на страницу нового товара
     */
    public static @NonNull String createUrlToItems() {
        return REDIRECT + '/' + DtoConstants.TEMPLATE_ITEMS;
    }

    /**
     * Создает редирект на адрес начальной страницы.
     *
     * @return редирект на адрес перехода на страницу нового товара
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
        return REDIRECT + '/' + DtoConstants.TEMPLATE_ORDERS;
    }

    /**
     * Создает адрес страницу нового заказа.
     *
     * @return адрес перехода на страницу нового заказа
     */
    public static @NonNull String createUrlToNewOrder(Long newOrderId) {
        return REDIRECT + '/' + DtoConstants.TEMPLATE_ORDERS +
                '/' + newOrderId + '?' + DtoConstants.PARAMETER_NEW_ORDER + '=' + Boolean.TRUE;
    }

    /**
     * Создает редирект на адрес страницу нового заказа.
     *
     * @return редирект на адрес перехода на страницу нового заказа
     */
    public static @NonNull String createRedirectUrlToNewOrder(Long newOrderId) {
        return REDIRECT + createUrlToNewOrder(newOrderId);
    }
}
