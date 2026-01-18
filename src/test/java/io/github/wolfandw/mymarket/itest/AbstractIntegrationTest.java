package io.github.wolfandw.mymarket.itest;

import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public abstract class AbstractIntegrationTest {
    /**
     * Репозиторий товаров.
     */
    @Autowired
    protected ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        createItemAndSave("02", "This is a test item", "test", new BigDecimal(1));
        createItemAndSave("03", "This is a test", "test", new BigDecimal(2));
        createItemAndSave("01 Item", "This is a test", "test", new BigDecimal(3));
        createItemAndSave("04", "This is a test", "test", new BigDecimal(4));
    }

    protected Item createItemAndSave(String title, String description, String imgPath, BigDecimal price) {
        Item newItem = new Item();
        newItem.setTitle(title);
        newItem.setDescription(description);
        newItem.setImgPath(imgPath);
        newItem.setPrice(price);
        return itemRepository.save(newItem);
    }


}
