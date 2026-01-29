package io.github.wolfandw.mymarket.itest;

import io.github.wolfandw.mymarket.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Абстрактный интеграционный тест.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {
    /**
     * Сервис товаров.
     */
    @Autowired
    protected ItemService itemService;

    /**
     * Сервис корзин.
     */
    @Autowired
    protected CartService cartService;

    /**
     * Сервис корзин.
     */
    @Autowired
    protected OrderService orderService;

    /**
     * Сервис работы с картинками.
     */
    @Autowired
    protected EntityImageService entityImageService;

    /**
     * Сервис работы с файлами.
     */
    @Autowired
    protected FileStorageService fileStorageService;

    /**
     * Папка с изображениями для тестов.
     */
    @Value("${mymarket.upload.images.dir}")
    protected String fileDir;

    /**
     * Папка с исходными изображениями для тестов.
     */
    @Value("${mymarket.upload.images.dir.test}")
    protected String fileDirTest;

    @Autowired
    protected MockMvc mockMvc;
}
