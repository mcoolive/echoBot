package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.mcoolive.echo_bot.service.RulesEngineTest.mapOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RulesLoaderFromYamlTest {
    @Test
    void test_read_yaml_configuration() throws IOException {
        final URL yamlUrl = getClass().getResource("/dynamic-response.yaml");
        assertNotNull(yamlUrl);
        final RulesLoaderFromYaml loader = new RulesLoaderFromYaml();
        final List<Rule<Map<String, Object>, Map<String, Object>>> rules = loader.loadRules(yamlUrl.openStream(), yamlUrl.toString(), 0L);

        assertNotNull(rules);
        assertEquals(5, rules.size());

        assertTrue(rules.get(0).test(mapOf("tag1", "101")));
        assertFalse(rules.get(0).test(mapOf("tag1", "999")));
        assertFalse(rules.get(0).test(mapOf("tag2", "101")));
        final Map<String, Object> expected0 = new HashMap<String, Object>() {{
            put("match", "101");
            put("res", "dynamic");
        }};
        assertEquals(expected0, rules.get(0).getResult().getOutput());

        assertTrue(rules.get(1).test(mapOf("tag1", "102", "tag2", "201")));
        assertFalse(rules.get(1).test(mapOf("tag1", "102", "tag2", "202")));
        final Map<String, Object> expected1 = new HashMap<String, Object>() {{
            put("match", "102-201");
            put("res", "dynamic");
        }};
        assertEquals(expected1, rules.get(1).getResult().getOutput());

        assertTrue(rules.get(2).test(mapOf("tag1", "102", "tag2", "202")));
        assertFalse(rules.get(2).test(mapOf()));
        final Map<String, Object> expected2 = new HashMap<String, Object>() {{
            put("match", "102-202");
            put("res", "dynamic");
        }};
        assertEquals(expected2, rules.get(2).getResult().getOutput());

        assertTrue(rules.get(3).test(mapOf("tag1", "103")));
        assertFalse(rules.get(3).test(mapOf()));
        assertEquals(0, rules.get(3).getResult().getDelayInMs());
        final Map<String, Object> expected3 = new HashMap<String, Object>() {{
            put("match", "103");
            put("res", "dynamic");
        }};
        assertEquals(expected3, rules.get(3).getResult().getOutput());

        assertTrue(rules.get(4).test(mapOf()));
        assertTrue(rules.get(4).getResult().getDelayInMs() < 0, "The parsed delays must be negative when it is not a number.");
        assertNull(rules.get(4).getResult().getOutput());
    }
}
