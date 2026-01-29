package io.github.wolfandw.mymarket.dto;

/**
 * Константы.
 */
public interface DtoConstants {
    /**
     * Идентификатор корзины по-умолчанию.
     */
    Long DEFAULT_CART_ID = 1L;

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
}
