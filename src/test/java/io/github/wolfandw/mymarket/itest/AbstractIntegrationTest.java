package io.github.wolfandw.mymarket.itest;

import io.github.wolfandw.mymarket.itest.configuration.TrxStepVerifier;
import io.github.wolfandw.mymarket.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Абстрактный интеграционный тест.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {
    /**
     * Идентификатор корзины по-умолчанию.
     */
    protected static final Long DEFAULT_CART_ID = 1L;

    private static final int BUFFER_SIZE = 4096;

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
     * Сервис покупок.
     */
    @Autowired
    protected BuyService buyService;

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
    protected WebTestClient webTestClient;

    @Autowired
    protected TrxStepVerifier trxStepVerifier;

    /**
     * Установка картинок.
     */
    protected void setupImages() {
        try {
            Path dest = Paths.get(fileDir);
            Path src = new ClassPathResource(fileDirTest).getFilePath();
            if (Files.exists(src) && Files.exists(dest)) {
                Stream<Path> files = Files.walk(src);
                files.forEach(file -> {
                    try {
                        Files.copy(file, dest.resolve(src.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ignored) {
                    }
                });
                files.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * Очистка картинок.
     */
    protected void cleanUpImages() {
        try {
            Path dest = Paths.get(fileDir);
            if (Files.exists(dest)) {
                Stream<Path> files = Files.walk(dest);
                files.forEach(file -> {
                    try {
                        Files.deleteIfExists(file);
                    } catch (IOException ignored) {
                    }
                });
                files.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * Возвращает {@link FilePart} по имени файла картинки.
     *
     * @param imageName имя файла картинки
     * @return {@link FilePart} по имени файла картинки
     */
    protected Mono<FilePart> getFilePart(String imageName) {
        return fileStorageService.readFile(imageName).map(expectedImageData -> new FilePart() {
            @Override
            public String name() {
                return "name";
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.EMPTY;
            }

            @Override
            public Flux<DataBuffer> content() {
                return DataBufferUtils.read(new ByteArrayResource(expectedImageData), new DefaultDataBufferFactory(), BUFFER_SIZE);
            }

            @Override
            public String filename() {
                return imageName;
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return DataBufferUtils.write(this.content(), dest);
            }
        });
    }
}
