package fr.mcoolive.crud_png.controller;

import fr.mcoolive.crud_png.exception.ImageNotFound;
import fr.mcoolive.crud_png.model.ImageEntity;
import fr.mcoolive.crud_png.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@Controller
@RequestMapping(ImageController.REQUEST_MAPPING)
public class ImageController {
    public static final String REQUEST_MAPPING = "/api/images";
    private final ImageService service;

    public ImageController(final ImageService service) {
        this.service = service;
    }

    @PostMapping(value = "/{issuerId}", consumes = IMAGE_PNG_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> uploadPngImage(@PathVariable UUID issuerId, @RequestBody byte[] imageBytes) {
        final var imageId = service.uploadPngImage(UUID.randomUUID(), imageBytes);
        final var location = REQUEST_MAPPING + "/" + imageId;
        return ResponseEntity.created(URI.create(location)).build();
    }

    @GetMapping(value = "/{issuerId}/{imageId}", produces = IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPngImage(@PathVariable UUID issuerId, @PathVariable UUID imageId) {
        final var imageEntity = service.getPngImage(imageId)
                .map(ImageEntity::getContent)
                .orElseThrow(ImageNotFound::new);
        return ResponseEntity.ok().body(imageEntity);
    }
}
