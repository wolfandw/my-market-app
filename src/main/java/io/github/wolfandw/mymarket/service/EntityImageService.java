package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Сервис для работы с картинками.
 */
public interface EntityImageService {
    /**
     * Возвращает картинку.
     *
     * @param entityId идентификатор сущности
     * @return картинка сущности
     */
    Mono<EntityImageDto> getEntityImage(Long entityId);

    /**
     * Возвращает картинку Base64.
     *
     * @param entityId идентификатор сущности
     * @return Base64-картинка сущности
     */
    Mono<String> getEntityImageBase64(Long entityId);

    /**
     * Устанавливает (обновляет) картинку сущности.
     *
     * @param entityId идентификатор сущности
     * @param imageFile  файл картинки
     */
    Mono<Void> setEntityImage(Long entityId, Mono<FilePart> imageFile);
}
