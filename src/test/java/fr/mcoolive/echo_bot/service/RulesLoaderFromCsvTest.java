package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.mcoolive.echo_bot.service.RulesEngineTest.mapOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RulesLoaderFromCsvTest {
    @Test
    void test_read_csv_configuration() throws IOException {
        final URL csvUrl = getClass().getResource("/dynamic-response.csv");
        assertNotNull(csvUrl);
        final RulesLoaderFromCsv loader = new RulesLoaderFromCsv();
        final List<Rule<Map<String, Object>, Map<String, Object>>> rules = loader.loadRules(csvUrl.openStream(), csvUrl.toString(), 0L);

        assertNotNull(rules);
        assertEquals(7, rules.size());

        assertTrue(rules.get(0).test(mapOf("tag1", "101")));
        assertFalse(rules.get(0).test(mapOf("tag1", "999")));
        assertFalse(rules.get(0).test(mapOf("tag1", "101", "tag2", "any")));
        final Map<String, Object> expected0 = new HashMap<String, Object>() {{
            put("match", "101+NULL");
            put("comment", "rule_n_01");
        }};
        assertEquals(expected0, rules.get(0).getResult().getOutput());
        assertTrue(rules.get(0).getResult().getExplanation().contains("lineNr=2"), "Expected the explanation to contain the lineNr.");

        assertFalse(rules.get(1).test(mapOf("tag1", "101")));
        assertFalse(rules.get(1).test(mapOf("tag1", "999")));
        assertTrue(rules.get(1).test(mapOf("tag1", "101", "tag2", "any")));
        final Map<String, Object> expected1 = new HashMap<String, Object>() {{
            put("match", "101+STAR");
            put("comment", "rule_n_02");
        }};
        assertEquals(expected1, rules.get(1).getResult().getOutput());
        assertTrue(rules.get(1).getResult().getExplanation().contains("lineNr=3"), "Expected the explanation to contain the lineNr.");

        assertTrue(rules.get(2).test(mapOf("tag1", "101")));
        assertFalse(rules.get(2).test(mapOf("tag1", "999")));
        assertFalse(rules.get(2).test(mapOf("tag2", "101")));
        final Map<String, Object> expected2 = new HashMap<String, Object>() {{
            put("match", "101");
            put("comment", "rule_n_03");
        }};
        assertEquals(expected2, rules.get(2).getResult().getOutput());
        assertTrue(rules.get(2).getResult().getExplanation().contains("lineNr=4"), "Expected the explanation to contain the lineNr.");

        assertTrue(rules.get(3).test(mapOf("tag1", "102", "tag2", "201")));
        assertFalse(rules.get(3).test(mapOf("tag1", "102", "tag2", "202")));
        final Map<String, Object> expected3 = new HashMap<String, Object>() {{
            put("match", "102-201");
            put("comment", "rule_n_04");
        }};
        assertEquals(expected3, rules.get(3).getResult().getOutput());
        assertTrue(rules.get(3).getResult().getExplanation().contains("lineNr=5"), "Expected the explanation to contain the lineNr.");

        assertTrue(rules.get(4).test(mapOf("tag1", "102", "tag2", "202")));
        assertFalse(rules.get(4).test(mapOf()));
        assertEquals(0, rules.get(4).getResult().getDelayInMs());
        final Map<String, Object> expected4 = new HashMap<String, Object>() {{
            put("match", "102-202");
            put("comment", "rule_n_05");
        }};
        assertEquals(expected4, rules.get(4).getResult().getOutput());

        assertTrue(rules.get(5).test(mapOf("tag1", "103")));
        assertFalse(rules.get(5).test(mapOf()));
        assertEquals(1000, rules.get(5).getResult().getDelayInMs());
        final Map<String, Object> expected5 = new HashMap<String, Object>() {{
            put("match", "103");
            put("comment", "rule_n_06");
        }};
        assertEquals(expected5, rules.get(5).getResult().getOutput());

        assertTrue(rules.get(6).test(mapOf()));
        assertTrue(rules.get(6).getResult().getDelayInMs() < 0, "The parsed delays must be negative when it is not a number.");
        assertEquals(new HashMap<>(), rules.get(6).getResult().getOutput());
    }
}
