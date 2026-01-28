package io.github.wolfandw.mymarket.itest;

import io.github.wolfandw.mymarket.service.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

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

    @Autowired
    protected MockMvc mockMvc;

    @AfterEach
    void cleanUp() {
        Path pathDir = Paths.get(fileDir);
        if (Files.exists(pathDir)) {
            Iterator<Path> it = pathDir.iterator();
            try {
                while (it.hasNext()) {
                    Path filePath = it.next();
                    Files.deleteIfExists(filePath);
                }
                Files.deleteIfExists(pathDir);
            } catch (IOException e) {
                // Do nothing;
            }
        }
    }
}
