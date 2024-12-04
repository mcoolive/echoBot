package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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
        final List<Rule<Object, String>> rules = loader.loadRules(yamlUrl.openStream(), yamlUrl.toString());

        assertNotNull(rules);
        assertEquals(5, rules.size());

        assertTrue(rules.get(0).test("{ \"tag1\": \"101\" }"));
        assertFalse(rules.get(0).test("{ \"tag1\": \"999\" }"));
        assertFalse(rules.get(0).test("{ \"tag2\": \"101\" }"));
        assertEquals("{\"match\":\"101\",\"res\":\"dynamic\"}", rules.get(0).getResult().getOutput());

        assertTrue(rules.get(1).test("{ \"tag1\": \"102\", \"tag2\": \"201\" }"));
        assertFalse(rules.get(1).test("{ \"tag1\": \"102\", \"tag2\": \"202\" }"));
        assertEquals("{\"match\":\"102-201\",\"res\":\"dynamic\"}", rules.get(1).getResult().getOutput());

        assertTrue(rules.get(2).test("{ \"tag1\": \"102\", \"tag2\": \"202\" }"));
        assertFalse(rules.get(2).test("{}"));
        assertEquals("{\"match\":\"102-202\",\"res\":\"dynamic\"}", rules.get(2).getResult().getOutput());

        assertTrue(rules.get(3).test("{ \"tag1\": \"103\" }"));
        assertFalse(rules.get(3).test("{}"));
        assertEquals(0, rules.get(3).getResult().getDelayInMs());
        assertEquals("{\"match\":\"103\",\"res\":\"dynamic\"}", rules.get(3).getResult().getOutput());

        assertTrue(rules.get(4).test("{}"));
        assertTrue(rules.get(4).getResult().getDelayInMs() < 0, "The parsed delays must be negative when it is not a number.");
        assertNull(rules.get(4).getResult().getOutput());
    }
}
