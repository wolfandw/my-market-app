package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Реализация {@link EntityImageService}
 */
@Service("entityImageService")
public class EntityImageServiceImpl implements EntityImageService {
    private static final String JPG = "jpg";
    private static final Logger log = LoggerFactory.getLogger(EntityImageServiceImpl.class);
    private final ItemRepository entityRepository;
    private final FileStorageService fileStorageService;

    /**
     * Создает сервис для работы с картинками постов.
     *
     * @param entityRepository репозиторий для работы с картинками постов
     * @param fileStorageService  сервис работы с файлами
     */
    public EntityImageServiceImpl(ItemRepository entityRepository, FileStorageService fileStorageService) {
        this.entityRepository = entityRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    @Override
    public EntityImageDto getEntityImage(Long entityId) {
        Optional<Item> entity = entityRepository.findById(entityId);
        if (entity.isPresent())  {
            String imageName = entity.get().getImgPath();
            if (imageName != null) {
                try {
                    byte[] content = fileStorageService.readFile(imageName);
                    return new EntityImageDto(entityId, content, getMediaType(content));
                } catch (IOException e) {
                    log.error("Картинка {} сущности {} не загрузилась", imageName, entityId, e);
                }
            }
        };
        return new EntityImageDto(entityId, new byte[0], MediaType.APPLICATION_OCTET_STREAM);
    }

    @Transactional
    @Override
    public String getEntityImageBase64(Long entityId) {
        Optional<Item> entity = entityRepository.findById(entityId);
        if (entity.isPresent())  {
            String imageName = entity.get().getImgPath();
            if (imageName != null) {
                try {
                    byte[] content = fileStorageService.readFile(imageName);
                    String base64Encoded = new String(Base64.getEncoder().encode(content), StandardCharsets.UTF_8);
                    return "data:" + getMediaType(content).toString() + ";base64, " + base64Encoded;
                } catch (IOException e) {
                    log.error("Картинка {} сущности {} не загрузилась", imageName, entityId, e);
                }
            }
        };
        return "";
    }

    @Transactional
    @Override
    public void updateEntityImage(Long entityId, MultipartFile image) {
        if (image == null) {
            return;
        }
        String originName = image.getOriginalFilename();
        String extension = getImageExtension(originName);
        String imageName = entityId.toString() + "." + extension;
        try {
            fileStorageService.writeFile(imageName, image);
            entityRepository.findById(entityId).ifPresent(entity -> {
                entity.setImgPath(imageName);
                entityRepository.save(entity);
            });
        } catch (IOException e) {
            log.error("Картинка {} сущности {} не обновилась", imageName, entityId, e);
        }
    }

    @Transactional
    @Override
    public void deleteEntityImage(Long entityId) {
        entityRepository.findById(entityId).ifPresent(
            entity -> {
                String imageName = entity.getImgPath();
                if (imageName != null) {
                    try {
                        fileStorageService.deleteFile(imageName);
                            entity.setImgPath(null);
                            entityRepository.save(entity);
                    } catch (IOException e) {
                        log.error("Картинка {} поста {} не удалилась", imageName, entityId, e);
                    }
                }
        });
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
