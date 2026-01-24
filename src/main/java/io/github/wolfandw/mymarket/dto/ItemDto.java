package io.github.wolfandw.mymarket.dto;

/**
 * DTO-описание товара.
 */
public class ItemDto {
    private final Long id;
    private final String title;
    private final String description;
    private final long price;
    private final int count;

    private String imgData;

    /**
     * DTO-описание товара.
     *
     * @param id          идентификатор товара
     * @param title       название товара
     * @param description описание товара
     * @param price       цена товара
     * @param count       число товаров в корзине
     */
    public ItemDto(Long id,
                   String title,
                   String description,
                   long price,
                   int count) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.count = count;
    }

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор товара
     */
    public Long id() {
        return id;
    }

    /**
     * Возвращает название товара.
     *
     * @return название товара
     */
    public String title() {
        return title;
    }

    /**
     * Возвращает описание товара.
     *
     * @return описание товара
     */
    public String description() {
        return description;
    }

    /**
     * Возвращает путь к изображению товара.
     *
     * @return путь к изображению товара
     */
    public String imgData() {
        return imgData;
    }

    /**
     * Устанавливает путь к изображению товара.
     *
     * @param imgData путь к изображению товара
     */
    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    /**
     * Возвращает цену товара.
     *
     * @return цена товара
     */
    public long price() {
        return price;
    }

    /**
     * Возвращает количество товара в корзине.
     *
     * @return количество товара
     */
    public int count() {
        return count;
    }
}
