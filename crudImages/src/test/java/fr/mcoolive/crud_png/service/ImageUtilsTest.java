package fr.mcoolive.crud_png.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public class ImageUtilsTest {

    @Nested
    public class IsPng {
        @Test
        void pngIsNotPng() throws IOException {
            final URL imageUrl = getClass().getResource("/dollar.png");
            Assertions.assertNotNull(imageUrl);
            try (var is = imageUrl.openStream()) {
                final var imageBytes = is.readAllBytes();
                Assertions.assertTrue(ImageUtils.isPng(imageBytes), "dollar.png is a PNG file");
            }
        }

        @Test
        void gifIsNotPng() throws IOException {
            final URL imageUrl = getClass().getResource("/icon-gif.gif");
            Assertions.assertNotNull(imageUrl);
            try (var is = imageUrl.openStream()) {
                final var imageBytes = is.readAllBytes();
                Assertions.assertFalse(ImageUtils.isPng(imageBytes), "icon-gif.gif is not a PNG file");
            }
        }

        @Test
        void jpgIsNotPng() throws IOException {
            final URL imageUrl = getClass().getResource("/icon-jpg.jpg");
            Assertions.assertNotNull(imageUrl);
            try (var is = imageUrl.openStream()) {
                final var imageBytes = is.readAllBytes();
                Assertions.assertFalse(ImageUtils.isPng(imageBytes), "icon-jpg.jpg is not a PNG file");
            }
        }
    }
}
