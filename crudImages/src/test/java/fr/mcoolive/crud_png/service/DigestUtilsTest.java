package fr.mcoolive.crud_png.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public class DigestUtilsTest {

    @Test
    void md5sum_dollar_png() throws IOException {
        final URL imageUrl = getClass().getResource("/dollar.png");
        Assertions.assertNotNull(imageUrl);
        try (var is = imageUrl.openStream()) {
            final var imageBytes = is.readAllBytes();
            Assertions.assertEquals(DigestUtils.md5sum(imageBytes), "5cb10315d8197509d70bee952033aca7");
        }
    }

    @Test
    void md5sum_icon_gif() throws IOException {
        final URL imageUrl = getClass().getResource("/icon-gif.gif");
        Assertions.assertNotNull(imageUrl);
        try (var is = imageUrl.openStream()) {
            final var imageBytes = is.readAllBytes();
            Assertions.assertEquals(DigestUtils.md5sum(imageBytes), "daf715043a0c421cc9e953603590aece");
        }
    }
}
