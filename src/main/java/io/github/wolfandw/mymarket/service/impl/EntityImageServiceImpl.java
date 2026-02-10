package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Реализация {@link EntityImageService}
 */
@Service
public class EntityImageServiceImpl implements EntityImageService {
    private static final String JPG = "jpg";
    private static final String BASE64_PREFIX = "data:";
    private static final String BASE_64_SUFFIX = ";base64, ";

    private final ItemRepository entityRepository;
    private final FileStorageService fileStorageService;

    /**
     * Создает сервис для работы с картинками постов.
     *
     * @param entityRepository   репозиторий для работы с картинками постов
     * @param fileStorageService сервис работы с файлами
     */
    public EntityImageServiceImpl(ItemRepository entityRepository, FileStorageService fileStorageService) {
        this.entityRepository = entityRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EntityImageDto> getEntityImage(Long entityId) {
        Mono<byte[]> contentMono = getEntityImageContent(entityId);
        return contentMono.map(content -> new EntityImageDto(entityId, content, getMediaType(content)));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<String> getEntityImageBase64(Long entityId) {
        Mono<byte[]> contentMono = getEntityImageContent(entityId);
        return contentMono.map(content ->
                BASE64_PREFIX + getMediaType(content).toString() + BASE_64_SUFFIX +
                        new String(Base64.getEncoder().encode(content), StandardCharsets.UTF_8));
    }

    @Override
    @Transactional
    public Mono<Void> setEntityImage(Long entityId, Mono<FilePart> imageFileMono) {
        return imageFileMono.flatMap(imageFile -> {
            String originName = imageFile.filename();
            String extension = getImageExtension(originName);
            String imageName = entityId.toString() + "." + extension;
            return fileStorageService.writeFile(imageName, imageFile);
        }).zipWith(entityRepository.findById(entityId)).flatMap(tuple -> {
            String imageName = tuple.getT1();
            Item entity = tuple.getT2();
            String oldImgPath = entity.getImgPath();
            entity.setImgPath(imageName);
            if (oldImgPath != null && !oldImgPath.isEmpty() && !oldImgPath.equals(imageName)) {
                return entityRepository.save(entity).then(fileStorageService.deleteFile(oldImgPath));
            }
            return entityRepository.save(entity);
        }).then();
    }

    private Mono<byte[]> getEntityImageContent(Long entityId) {
        return entityRepository.findById(entityId).map(Item::getImgPath).
                flatMap(fileStorageService::readFile);
    }

    private MediaType getMediaType(byte[] content) {
        if (content != null) {
            if (content.length >= 3 && content[0] == (byte) 0xFF && content[1] == (byte) 0xD8) {
                return MediaType.IMAGE_JPEG;
            }
            if (content.length >= 8 && content[0] == (byte) 0x89 && content[1] == 0x50) {
                return MediaType.IMAGE_PNG;
            }
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String getImageExtension(String originName) {
        if (originName != null && originName.lastIndexOf('.') != -1) {
            String extension = originName.substring(originName.lastIndexOf('.') + 1).toLowerCase();
            if (!extension.isEmpty()) {
                return extension;
            }
        }
        return JPG;
    }
}
