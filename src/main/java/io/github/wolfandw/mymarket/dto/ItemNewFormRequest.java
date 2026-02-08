package io.github.wolfandw.mymarket.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * Параметры запроса формы нового товара.
 */
public class ItemNewFormRequest {
    private String title;
    private String description;
    private Long price;
    private MultipartFile imageFile;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
