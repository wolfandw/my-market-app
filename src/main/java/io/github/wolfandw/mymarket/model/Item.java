package io.github.wolfandw.mymarket.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * Класс модели товара.
 */
@Table("ITEMS")
public class Item {
    @Id
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String imgPath;

    @Column
    private BigDecimal price;

    /**
     * Создает модель товара.
     *
     * @param id идентификатор товара
     * @param title название товара
     * @param description описание товара
     * @param imgPath путь к картинке
     * @param price цена
     */
    public Item(Long id, String title, String description, String imgPath, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
    }

    /**
     * Создает модель товара.
     */
     public Item() {
     }

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор товара
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор товара.
     *
     * @param id идентификатор товара
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название товара.
     *
     * @return название товара
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название товара.
     *
     * @param title название товара
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Возвращает описание товара.
     *
     * @return описание товара
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание товара.
     *
     * @param description описание товара
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает путь к изображению товара.
     *
     * @return путь к изображению товара
     */
    public String getImgPath() {
        return imgPath;
    }

    /**
     * Устанавливает путь к изображению товара.
     *
     * @param imgPath путь к изображению товара
     */
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    /**
     * Возвращает цену товара.
     *
     * @return цена товара
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Устанавливает цену товара.
     *
     * @param price цена товара
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
