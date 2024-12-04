package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RulesLoaderFromCsvTest {
    @Test
    void test_read_csv_configuration() throws IOException {
        final URL csvUrl = getClass().getResource("/dynamic-response.csv");
        assertNotNull(csvUrl);
        final RulesLoaderFromCsv loader = new RulesLoaderFromCsv();
        final List<Rule<Object, String>> rules = loader.loadRules(csvUrl.openStream(), csvUrl.toString());

        assertNotNull(rules);
        assertEquals(7, rules.size());

        assertTrue(rules.get(0).test("{ \"tag1\": \"101\" }"));
        assertFalse(rules.get(0).test("{ \"tag1\": \"999\" }"));
        assertFalse(rules.get(0).test("{ \"tag1\": \"101\", \"tag2\": \"any\" }"));
        assertEquals("{\"match\":\"101+NULL\",\"comment\":\"rule_n_01\"}", rules.get(0).getResult().getOutput());
        assertTrue(rules.get(0).getResult().getExplanation().contains("lineNr=2"), "Expected the explanation to contain the lineNr.");

        assertFalse(rules.get(1).test("{ \"tag1\": \"101\" }"));
        assertFalse(rules.get(1).test("{ \"tag1\": \"999\" }"));
        assertTrue(rules.get(1).test("{ \"tag1\": \"101\", \"tag2\": \"any\" }"));
        assertEquals("{\"match\":\"101+STAR\",\"comment\":\"rule_n_02\"}", rules.get(1).getResult().getOutput());
        assertTrue(rules.get(1).getResult().getExplanation().contains("lineNr=3"), "Expected the explanation to contain the lineNr.");

        assertTrue(rules.get(2).test("{ \"tag1\": \"101\" }"));
        assertFalse(rules.get(2).test("{ \"tag1\": \"999\" }"));
        assertFalse(rules.get(2).test("{ \"tag2\": \"101\" }"));
        assertEquals("{\"match\":\"101\",\"comment\":\"rule_n_03\"}", rules.get(2).getResult().getOutput());
        assertTrue(rules.get(2).getResult().getExplanation().contains("lineNr=4"), "Expected the explanation to contain the lineNr.");

        assertTrue(rules.get(3).test("{ \"tag1\": \"102\", \"tag2\": \"201\" }"));
        assertFalse(rules.get(3).test("{ \"tag1\": \"102\", \"tag2\": \"202\" }"));
        assertEquals("{\"match\":\"102-201\",\"comment\":\"rule_n_04\"}", rules.get(3).getResult().getOutput());
        assertTrue(rules.get(3).getResult().getExplanation().contains("lineNr=5"), "Expected the explanation to contain the lineNr.");

        assertTrue(rules.get(4).test("{ \"tag1\": \"102\", \"tag2\": \"202\" }"));
        assertFalse(rules.get(4).test("{}"));
        assertEquals(0, rules.get(4).getResult().getDelayInMs());
        assertEquals("{\"match\":\"102-202\",\"comment\":\"rule_n_05\"}", rules.get(4).getResult().getOutput());

        assertTrue(rules.get(5).test("{ \"tag1\": \"103\" }"));
        assertFalse(rules.get(5).test("{}"));
        assertEquals(1000, rules.get(5).getResult().getDelayInMs());
        assertEquals("{\"match\":\"103\",\"comment\":\"rule_n_06\"}", rules.get(5).getResult().getOutput());

        assertTrue(rules.get(6).test("{}"));
        assertTrue(rules.get(6).getResult().getDelayInMs() < 0, "The parsed delays must be negative when it is not a number.");
        assertEquals("{}", rules.get(6).getResult().getOutput());
    }
}
