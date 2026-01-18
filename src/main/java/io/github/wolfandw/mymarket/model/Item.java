package io.github.wolfandw.mymarket.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Класс модели товара.
 */
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column()
    private String description;

    @Column()
    private String imgPath;

    @Column(nullable = false)
    private BigDecimal price;

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
