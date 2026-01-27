package io.github.wolfandw.mymarket.itest;

import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.service.FileStorageService;
import io.github.wolfandw.mymarket.service.ItemService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

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
    protected ItemRepository itemRepository;

    /**
     * Сервис товаров.
     */
    @Autowired
    protected ItemService itemService;

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
