package io.github.wolfandw.mymarket;

import org.jspecify.annotations.NonNull;

/**
 * Утилитные методы и константы приложения.
 */
public interface MyMarketUtils {
    /**
     * Идентификатор корзины по-умолчанию.
     */
    Long DEFAULT_CART_ID = 1L;

    /**
     * Редирект.
     */
    String REDIRECT = "redirect:";

    /**
     * Шаблоны.
     */
    String TEMPLATE_ITEMS = "items";
    String TEMPLATE_ITEM = "item";
    String TEMPLATE_ITEM_NEW = "item_new";
    String TEMPLATE_CART = "cart";
    String TEMPLATE_ORDERS = "orders";
    String TEMPLATE_ORDER = "order";

    /**
     * Атрибуты.
     */
    String ATTRIBUTE_ITEMS = "items";
    String ATTRIBUTE_SEARCH = "search";
    String ATTRIBUTE_SORT = "sort";
    String ATTRIBUTE_PAGING = "paging";
    String ATTRIBUTE_ITEM = "item";
    String ATTRIBUTE_NEW_ITEM = "newItem";
    String ATTRIBUTE_TOTAL = "total";
    String ATTRIBUTE_ORDERS = "orders";
    String ATTRIBUTE_ORDER = "order";
    String ATTRIBUTE_TOTAL_SUM = "totalSum";
    String ATTRIBUTE_NEW_ORDER = "newOrder";
    String ATTRIBUTE_TITLE = "title";
    String ATTRIBUTE_COUNT = "count";

    /**
     * Параметры.
     */
    String PARAMETER_ID = "id";
    String PARAMETER_SEARCH = "search";
    String PARAMETER_SORT = "sort";
    String PARAMETER_PAGE_NUMBER = "pageNumber";
    String PARAMETER_PAGE_SIZE = "pageSize";
    String PARAMETER_NEW_ITEM = "newItem";
    String PARAMETER_ACTION = "action";
    String PARAMETER_TITLE = "title";
    String PARAMETER_DESCRIPTION = "description";
    String PARAMETER_PRICE = "price";
    String PARAMETER_IMAGE_FILE = "imageFile";
    String PARAMETER_NEW_ORDER = "newOrder";

    /**
     * Действие я товаром в корзине.
     */
    String ACTION_MINUS = "MINUS";
    String ACTION_PLUS = "PLUS";
    String ACTION_DELETE = "DELETE";

    /**
     * Строит адрес перехода на страницу товаров с отборами и поиском.
     *
     * @param search параметры поиска
     * @param sort параметры сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return адрес перехода на страницу товаров с отборами и поиском.
     */
    static @NonNull String buildRedirectUrlToItems(String search, String sort, Integer pageNumber, Integer pageSize) {

        StringBuilder redirect = new StringBuilder();
        redirect.append('/').append(MyMarketUtils.TEMPLATE_ITEMS);
        redirect.append('?');
        redirect.append(MyMarketUtils.PARAMETER_SEARCH).append('=');
        if (search != null) {
            redirect.append(search);
        }
        redirect.append('&').append(MyMarketUtils.PARAMETER_SORT).append('=');
        if (sort != null) {
            redirect.append(sort);
        }
        redirect.append('&').append(MyMarketUtils.PARAMETER_PAGE_NUMBER).append('=');
        if (pageNumber != null) {
            redirect.append(pageNumber);
        }
        redirect.append('&').append(MyMarketUtils.PARAMETER_PAGE_SIZE).append('=');
        if (pageSize != null) {
            redirect.append(pageSize);
        }
        return redirect.toString();
    }

    /**
     * Строит адрес перехода на страницу нового товара.
     *
     * @param newItemId идентификатор нового товара
     * @return адрес перехода на страницу нового товара
     */
    static @NonNull String buildRedirectUrlToNewItem(Long newItemId) {
        return '/' + MyMarketUtils.TEMPLATE_ITEMS +
                '/' +  newItemId +
                '?' + MyMarketUtils.PARAMETER_NEW_ITEM + '=' + Boolean.TRUE;
    }
}
