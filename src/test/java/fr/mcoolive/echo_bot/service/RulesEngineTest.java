package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

public class RulesEngineTest {

    @Nested
    public class Basic {
        private final List<Rule<String, String>> rules = Arrays.asList(
                new Rule<>(new JsonPathPredicate.Equals("Tag1", 1), "{ \"res\":\"1\" }", "rule n°01", 0, OK),
                new Rule<>(new JsonPathPredicate.Equals("Tag1", 2), "{ \"res\":\"2\" }", "rule n°02", 0, OK),
                new Rule<>(new JsonPathPredicate.Equals("Tag1", 3), "{ \"res\":\"3\" }", "rule n°03", 0, OK),
                new Rule<>(new JsonPathPredicate.Equals("Tag1", 4), "{ \"res\":\"4\" }", "rule n°04", 0, OK)
        );

        private final RulesEngine.Basic<String, String> rulesEngine = new RulesEngine.Basic<>(rules);

        @Test
        void test_rules_engine() {
            Optional<Rule.Result<String>> result;

            result = rulesEngine.execute("{ \"Tag1\": 1 }");
            assertTrue(result.isPresent());
            assertEquals("{ \"res\":\"1\" }", result.get().getOutput());
            assertEquals("rule n°01", result.get().getExplanation());

            result = rulesEngine.execute("{ \"Tag1\": 2 }");
            assertTrue(result.isPresent());
            assertEquals("{ \"res\":\"2\" }", result.get().getOutput());
            assertEquals("rule n°02", result.get().getExplanation());

            result = rulesEngine.execute("{ \"Tag1\": 3 }");
            assertTrue(result.isPresent());
            assertEquals("{ \"res\":\"3\" }", result.get().getOutput());
            assertEquals("rule n°03", result.get().getExplanation());

            result = rulesEngine.execute("{ \"Tag1\": 909 }");
            assertFalse(result.isPresent());

            result = rulesEngine.execute("{ \"Tag999\": 1 }");
            assertFalse(result.isPresent());
        }
    }

    @Nested
    public class Fallback {
        private final String fallback = "fallback";
        private final RulesEngine.Fallback<String> rulesEngine = new RulesEngine.Fallback<>(fallback);

        @Test
        void test_rules_engine() {
            Optional<Rule.Result<String>> result;

            result = rulesEngine.execute("{ \"Tag1\": 1 }");
            assertTrue(result.isPresent());
            assertEquals(fallback, result.get().getOutput());

            result = rulesEngine.execute("{ \"Tag1\": null }");
            assertTrue(result.isPresent());
            assertEquals(fallback, result.get().getOutput());

            result = rulesEngine.execute("{ \"Tag999\": 1 }");
            assertTrue(result.isPresent());
            assertEquals(fallback, result.get().getOutput());
        }
    }
}
