package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import org.springframework.web.multipart.MultipartFile;

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
    EntityImageDto getEntityImage(Long entityId);

    /**
     * Возвращает картинку Base64.
     *
     * @param entityId идентификатор сущности
     * @return Base64-картинка сущности
     */
    String getEntityImageBase64(Long entityId);

    /**
     * Обновляет картинку сущности.
     *
     * @param entityId идентификатор сущности
     * @param image  файл картинки
     */
    void updateEntityImage(Long entityId, MultipartFile image);

    /**
     * Удаляет картинку сущности.
     *
     * @param entityId идентификатор поста
     */
    void deleteEntityImage(Long entityId);
}
