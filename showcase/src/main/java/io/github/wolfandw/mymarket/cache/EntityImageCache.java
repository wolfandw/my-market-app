package io.github.wolfandw.mymarket.cache;

import reactor.core.publisher.Mono;

/**
 * Кэш картинок товаров.
 */
public interface EntityImageCache {
    /**
     * Префикс ключа.
     */
    String KEY_PREFIX = "mymarket:items:image";

    /**
     * Получает данные картинки из кэша.
     *
     * @param entityId идентификатор сущности
     * @return данные картинки из кэша
     */
    Mono<byte[]> getEntityImage(Long entityId);

    /**
     * Помещает данные картинки в кэш.
     *
     * @param entityId идентификатор сущности
     * @param databaseContent данные картинки из файла
     * @return данные картинки из файла
     */
    Mono<byte[]> cache(Long entityId, Mono<byte[]> databaseContent);

    /**
     * Удаляет картинку сущности из кэша.
     *
     * @param entityId идентификатор сущности
     * @return количество удаленных записей
     */
    Mono<Long> delete(Long entityId);

    /**
     * Очищает кэш картинок сущностей.
     *
     * @return количество удаленных записей
     */
    Mono<Long> clear();
}
