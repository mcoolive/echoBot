package fr.mcoolive.echo_bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class JsonPathPredicateTest {
    private static String jsonString;
    private static Object jsonObject;

    @BeforeAll
    public static void loadJson() throws IOException, URISyntaxException {
        final URL url = JsonPathPredicateTest.class.getClassLoader().getResource("test-case.json");
        assertNotNull(url);
        jsonString = new String(Files.readAllBytes(Paths.get(url.toURI())));
        final ObjectMapper objectMapper = new ObjectMapper();
        jsonObject = objectMapper.readValue(jsonString, Object.class);
    }

    private static Object[] jsonObjectsToTest() {
        return new Object[]{jsonString, jsonObject};
    }

    @Nested
    public class Equals {

        @Test
        public void constructorShouldFailWhenTheJsonPathIsInvalid() {
            // The key name should be in quotes within square brackets
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Equals("$.testCase.levels[name]", "NA")
            );
            // Invalid nested square brackets
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Equals("$.testCase.levels[0[1]]", "NA")
            );
            // Trailing dot at the end is invalid
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Equals("$.testCase.levels.", "NA")
            );
            // Empty square brackets are invalid without a specific index or key
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Equals("$.testCase.levels[]", "NA")
            );
        }

        @Test
        public void testWithObviousValues() {
            assertTrue(new JsonPathPredicate.Equals("$", null).test(null));
            assertTrue(new JsonPathPredicate.Equals("$", "").test("\"\""));
            assertTrue(new JsonPathPredicate.Equals("$", "Hello World").test("\"Hello World\""));
            assertTrue(new JsonPathPredicate.Equals("$", 42).test(42));
            assertTrue(new JsonPathPredicate.Equals("$", 3.14).test(3.14));
            assertTrue(new JsonPathPredicate.Equals("$.key", null).test("{ \"key\": null }"));
            assertTrue(new JsonPathPredicate.Equals("$.key", "value").test("{ \"key\": \"value\" }"));
            assertTrue(new JsonPathPredicate.Equals("$[0]", 0).test("[0]"));
            assertTrue(new JsonPathPredicate.Equals("$[1]", 1).test("[0, 1]"));
            assertTrue(new JsonPathPredicate.Equals("$.undefinedField", null).test("{}"));
        }

        @Test
        public void testAgainstAnInvalidJsonShouldReturnFalse() {
            // json can not be empty
            assertFalse(new JsonPathPredicate.Equals("$", "").test(""));
            // Unexpected end of file
            assertFalse(new JsonPathPredicate.Equals("$", "").test("{"));
            assertFalse(new JsonPathPredicate.Equals("$", "").test("["));
            // Unexpected character
            assertFalse(new JsonPathPredicate.Equals("$", "").test("}"));
            assertFalse(new JsonPathPredicate.Equals("$", "").test("]"));
        }

        @ParameterizedTest
        @MethodSource("fr.mcoolive.echo_bot.service.JsonPathPredicateTest#jsonObjectsToTest")
        public void testWithAFullJson(Object json) {
            assertFalse(new JsonPathPredicate.Equals("$", "NA").test(json));
            assertFalse(new JsonPathPredicate.Equals("$.testCase", "NA").test(json));

            assertTrue(new JsonPathPredicate.Equals("$.testCase.name", "An arbitrary JSON to implement tests").test(json));
            assertFalse(new JsonPathPredicate.Equals("$.testCase.name", 42).test(json));

            assertTrue(new JsonPathPredicate.Equals("$.testCase.skills[0]", "Java").test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.skills[1]", "JUnit").test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.skills[2]", "Spring").test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.skills[3]", "Mockito").test(json));
            assertFalse(new JsonPathPredicate.Equals("$.testCase.skills[4]", "Kotlin").test(json));

            assertFalse(new JsonPathPredicate.Equals("$.testCase.levels", 0).test(json));
            for (int level = 0; level <= 6; level++)
                assertTrue(new JsonPathPredicate.Equals("$.testCase.levels[" + level + "]", level).test(json));

            for (int i = 0; i <= 10; i++)
                for (int j = 0; j <= 10; j++)
                    assertTrue(new JsonPathPredicate.Equals("$.testCase.multiplicationTable[" + i + "][" + j + "]", i * j).test(json));
            assertFalse(new JsonPathPredicate.Equals("$.testCase.multiplicationTable[0][0]", 0.0001).test(json));

            assertFalse(new JsonPathPredicate.Equals("$.testCase.books", "NA").test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[0].title", "Pride and Prejudice").test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[0].year", 1813).test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[0].rating", 4.3).test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[1].title", "Moby-Dick").test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[1].year", 1851).test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[1].rating", 4.0).test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[2].title", "Les Misérables").test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[2].year", 1862).test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[2].rating", 4.9).test(json));
            assertTrue(new JsonPathPredicate.Equals("$.testCase.books[5].publisher", null).test(json));

            assertFalse(new JsonPathPredicate.Equals("$.testCase.books[0].title", " Pride and Prejudice ").test(json));
            assertFalse(new JsonPathPredicate.Equals("$.testCase.books[0].title", "Orgueil et Préjugés").test(json));
        }
    }

    @Nested
    public class Regex {
        @Test
        public void constructorShouldFailWhenTheJsonPathIsInvalid() {
            // The key name should be in quotes within square brackets
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$.testCase.levels[name]", "NA")
            );
            // Invalid nested square brackets
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$.testCase.levels[0[1]]", "NA")
            );
            // Trailing dot at the end is invalid
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$.testCase.levels.", "NA")
            );
            // Empty square brackets are invalid without a specific index or key
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$.testCase.levels[]", "NA")
            );
        }

        @Test
        public void constructorShouldFailWhenThePatternIsInvalid() {
            // Regex can not be null
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$", null)
            );
            // Unmatched Parentheses, Brackets or Curly Braces
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$", "(a")
            );
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$", "[a")
            );
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$", "{2")
            );
            // Quantifiers Without Preceding Pattern
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$", "*a")
            );
            // Invalid Range
            assertThrows(RuntimeException.class, () ->
                    new JsonPathPredicate.Match("$", "[z-a]")
            );
        }

        @Test
        public void dotStartShouldMatchAnyString() {
            assertTrue(new JsonPathPredicate.Match("$", ".*").test("\"\""));
            assertTrue(new JsonPathPredicate.Match("$", ".*").test("\"abc\""));
            assertTrue(new JsonPathPredicate.Match("$", ".*").test("\"random\""));
        }

        @Test
        public void dotPlusShouldMatchAnyNonEmptyString() {
            assertFalse(new JsonPathPredicate.Match("$", ".+").test("\"\""));
            assertTrue(new JsonPathPredicate.Match("$", ".+").test("\"abc\""));
            assertTrue(new JsonPathPredicate.Match("$", ".+").test("\"random\""));
        }
    }
}
