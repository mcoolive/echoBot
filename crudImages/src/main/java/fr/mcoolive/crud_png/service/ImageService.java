package fr.mcoolive.crud_png.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import fr.mcoolive.crud_png.exception.InvalidImage;
import fr.mcoolive.crud_png.model.ImageEntity;
import fr.mcoolive.crud_png.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    private final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository repository;
    private final Cache<UUID, Optional<ImageEntity>> cache;

    public ImageService(ImageRepository repository) {
        this.repository = repository;
        this.cache = Caffeine.newBuilder()
                .maximumSize(100)               // Maximum of 100 entries in the cache
                .build();

        // Temporary code for tests
        Map<UUID, String> map = Map.of(
                new UUID(0, 1), "/loved-book.png",
                new UUID(0, 2), "/dollar.png",
                new UUID(0, 3), "/music-note.png"
        );
        for (Map.Entry<UUID, String> entry : map.entrySet()) {
            final var imageId = entry.getKey();
            final var imageUrl = getClass().getResource(entry.getValue());
            if (imageUrl == null) throw new RuntimeException(entry.getValue() + " not found");
            try (final var imageStream = imageUrl.openStream()) {
                final var imageBytes = imageStream.readAllBytes();
                final var imageEntity = new ImageEntity(imageId, imageId, imageBytes);
                cache.put(imageEntity.getId(), Optional.of(imageEntity));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public UUID uploadPngImage(final UUID issuerId, final byte[] imageBytes) {
        if (!ImageUtils.isPng(imageBytes)) {
            throw new InvalidImage("Only PNG images are supported.");
        }
        final var imageId = UUID.randomUUID();
        final var imageEntity = new ImageEntity(imageId, issuerId, imageBytes);
        repository.save(imageEntity);
        cache.put(imageEntity.getId(), Optional.of(imageEntity));
        return imageEntity.getId();
    }

    public Optional<ImageEntity> getPngImage(final UUID imageId) {
        return cache.get(imageId, repository::findById);
    }
}
