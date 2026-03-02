package io.github.wolfandw.mymarket.dto;

import org.springframework.http.MediaType;

/**
 * Класс модели картинки.
 */
public class EntityImageDto {
    private final Long entityId;
    private byte[] data;
    private MediaType mediaType;

    /**
     * Создает картинку сущности.
     *
     * @param entityId  идентификатор сущности
     * @param data      данные картинки
     * @param mediaType тип данных картинки
     */
    public EntityImageDto(Long entityId, byte[] data, MediaType mediaType) {
        this.entityId = entityId;
        this.data = data;
        this.mediaType = mediaType;
    }

    /**
     * Устанавливает данные картинки.
     *
     * @param data данные картинки
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Возвращает данные картинки.
     *
     * @return данные картинки
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Устанавливает тип данных картинки.
     *
     * @param mediaType тип данных картинки
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Возвращает тип данных картинки.
     *
     * @return тип данных картинки
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Возвращает идентификатор сущности-владельца.
     *
     * @return идентификатор сущности-владельца
     */
    public Long getEntityId() {
        return entityId;
    }
}