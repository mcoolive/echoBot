package fr.mcoolive.crud_png.controller;

import fr.mcoolive.crud_png.TestSecurityConfig;
import fr.mcoolive.crud_png.service.ImageUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static fr.mcoolive.crud_png.controller.ImageController.REQUEST_MAPPING;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    final String getUriTemplate = REQUEST_MAPPING + "/{imageId}";

    @Test
    @WithMockUser
    void fetch_image_in_JPEG_should_fail() throws Exception {
        final UUID imageId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final MockHttpServletRequestBuilder getRequest = get(getUriTemplate, imageId)
                .accept(IMAGE_JPEG_VALUE);

        mockMvc.perform(getRequest)
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void fetch_image_in_PNG_should_succeed() throws Exception {
        final UUID imageId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final MockHttpServletRequestBuilder getRequest = get(getUriTemplate, imageId)
                .accept(IMAGE_PNG_VALUE);

        final byte[] imageBytes = mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(IMAGE_PNG_VALUE))
                .andReturn().getResponse().getContentAsByteArray();

        assertNotNull(imageBytes);
        assertTrue(ImageUtils.isPng(imageBytes), "The image " + imageId + " is returned in PNG format.");
    }

    @Test
    @WithMockUser
    void fetch_image_should_succeed() throws Exception {
        final UUID imageId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final MockHttpServletRequestBuilder getRequest = get(getUriTemplate, imageId);

        final byte[] imageBytes = mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(IMAGE_PNG_VALUE))
                .andReturn().getResponse().getContentAsByteArray();

        assertNotNull(imageBytes);
        assertTrue(ImageUtils.isPng(imageBytes), "The image " + imageId + " is returned in PNG format.");
    }
}
