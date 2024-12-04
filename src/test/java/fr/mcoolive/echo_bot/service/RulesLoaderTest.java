package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RulesLoaderTest {
    @Test
    void test_isCsvFile() {
        assertTrue(RulesLoader.isCsvFile("file.csv"), "Expected the file type to be detected as CSV");
        assertTrue(RulesLoader.isCsvFile(" ile.Csv"), "Expected the file type to be detected as CSV");
        assertTrue(RulesLoader.isCsvFile("file.CSV"), "Expected the file type to be detected as CSV");
        assertTrue(RulesLoader.isCsvFile("/d/f.csv"), "Expected the file type to be detected as CSV");

        assertFalse(RulesLoader.isCsvFile("file.yaml"), "Expected the file type to not be detected as CSV");
        assertFalse(RulesLoader.isCsvFile("not-csv.yaml"), "Expected the file type to not be detected as CSV");
    }

    @Test
    void test_isYamlFile() {
        assertTrue(RulesLoader.isYamlFile("file.yaml"), "Expected the file type to be detected as YAML");
        assertTrue(RulesLoader.isYamlFile(" ile.YAML"), "Expected the file type to be detected as YAML");
        assertTrue(RulesLoader.isYamlFile("file.yml"), "Expected the file type to be detected as YAML");
        assertTrue(RulesLoader.isYamlFile("/d/f.YML"), "Expected the file type to be detected as YAML");

        assertFalse(RulesLoader.isYamlFile("file.csv"), "Expected the file type to not be detected as YAML");
        assertFalse(RulesLoader.isYamlFile("not-yaml.csv"), "Expected the file type to not be detected as YAML");
    }

    @Test
    void test_parseDelay() {
        assertEquals(0, RulesLoader.parseDelay(null));
        assertEquals(0, RulesLoader.parseDelay(""));
        assertEquals(0, RulesLoader.parseDelay("0"));
        assertEquals(1, RulesLoader.parseDelay("1"));
        assertEquals(999, RulesLoader.parseDelay("999"));
        assertTrue(RulesLoader.parseDelay("not-a-number") < 0, "Expect negative delay");
        assertTrue(RulesLoader.parseDelay("never") < 0, "Expect negative delay");
        assertTrue(RulesLoader.parseDelay("NO") < 0, "Expect negative delay");
    }
}
