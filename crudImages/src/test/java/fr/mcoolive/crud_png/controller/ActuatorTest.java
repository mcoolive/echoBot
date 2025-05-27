package fr.mcoolive.crud_png.controller;

import fr.mcoolive.crud_png.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureObservability
@Import(TestSecurityConfig.class)
public class ActuatorTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_actuator_health() throws Exception {
        final MockHttpServletRequestBuilder getRequest = get("/actuator/health")
                .accept(APPLICATION_JSON_VALUE);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void test_kubernetes_liveness_probe() throws Exception {
        final MockHttpServletRequestBuilder getRequest = get("/actuator/health/liveness")
                .accept(APPLICATION_JSON_VALUE);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void test_kubernetes_readiness_probe() throws Exception {
        final MockHttpServletRequestBuilder getRequest = get("/actuator/health/readiness")
                .accept(APPLICATION_JSON_VALUE);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_actuator_info() throws Exception {
        final MockHttpServletRequestBuilder getRequest = get("/actuator/info")
                .accept(APPLICATION_JSON_VALUE);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.build").isMap())
                .andExpect(jsonPath("$.build.artifact").isString());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_prometheus_metrics() throws Exception {
        final MockHttpServletRequestBuilder getRequest = get("/actuator/prometheus");
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_PLAIN_VALUE));
    }
}
