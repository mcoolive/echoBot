package fr.mcoolive.echo_bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mcoolive.echo_bot.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.mcoolive.echo_bot.controller.MessageController.REQUEST_MAPPING;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_negative_delay() throws Exception {
        final Map<String, String> message = Stream.of(new Object[][]{
                {"PAN", "1234567812345678"},
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
        final String json = objectMapper.writeValueAsString(message);

        final MockHttpServletRequestBuilder postRequest = post(REQUEST_MAPPING)
                .contentType(APPLICATION_JSON_VALUE).content(json);

        mockMvc.perform(postRequest)
                .andExpect(jsonPath("$").doesNotExist())
                .andReturn();
    }

    @Test
    void should_match_the_first_rule() throws Exception {
        final Map<String, String> message = Stream.of(new Object[][]{
                {"PAN", "1234567812345678"},
                {"tag1", "101"},
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
        final String json = objectMapper.writeValueAsString(message);

        final MockHttpServletRequestBuilder postRequest = post(REQUEST_MAPPING)
                .contentType(APPLICATION_JSON_VALUE).content(json);

        final MvcResult mvcResult = mockMvc.perform(postRequest)
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.match").isString())
                .andExpect(jsonPath("$.match").value("101+NULL"))
                .andExpect(jsonPath("$.comment").isString())
                .andExpect(jsonPath("$.comment").value("rule_n_01"))
                .andReturn();
    }

    @Test
    void should_match_the_second_rule() throws Exception {
        final Map<String, String> message = Stream.of(new Object[][]{
                {"PAN", "1234567812345678"},
                {"tag1", "101"},
                {"tag2", "some"},
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));
        final String json = objectMapper.writeValueAsString(message);

        final MockHttpServletRequestBuilder postRequest = post(REQUEST_MAPPING)
                .contentType(APPLICATION_JSON_VALUE).content(json);

        final MvcResult mvcResult = mockMvc.perform(postRequest)
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.match").isString())
                .andExpect(jsonPath("$.match").value("101+STAR"))
                .andExpect(jsonPath("$.comment").isString())
                .andExpect(jsonPath("$.comment").value("rule_n_02"))
                .andReturn();
    }
}
