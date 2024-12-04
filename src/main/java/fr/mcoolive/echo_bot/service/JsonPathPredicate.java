package fr.mcoolive.echo_bot.service;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A predicate that applies a JSONPath expression to an object and compares the result
 * with a specified policy.
 * </p>
 * <p>
 * This implementation supports applying the JSONPath to both String-based JSON objects
 * and general Object-based JSON objects.
 * It is very convenient, but it may cause some confusion when dealing with a simple JSON string.
 * <ul>
 * <li> The String should contain a JSON document (i.e., a JSON text representation).
 * <li> The Object could be of various types, the method interprets it as JSON in its native format (e.g., a Map could represent a JSON object, or a List could represent a JSON array).
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     JsonPathPredicate predicate = new JsonPathPredicate.Equals("$.user.id", 123);
 *     boolean result = predicate.test("{\"user\": {\"id\": 123}}");
 * </pre>
 * </p>
 */
public abstract class JsonPathPredicate implements Predicate<Object> {

    protected final JsonPath jsonPath;

    JsonPathPredicate(String jsonPath) throws IllegalArgumentException {
        try {
            this.jsonPath = JsonPath.compile(jsonPath);
        } catch (InvalidPathException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Applies this JsonPath to the provided object.
     */
    protected Object readJsonPath(Object json) {
        if (json == null) return null;
        try {
            if (json instanceof String)
                return jsonPath.read((String) json);
            else
                return jsonPath.read(json);
        } catch (PathNotFoundException ex) {
            return null;
        }
    }

    @Override
    abstract public boolean test(Object json);

    public static final class Equals extends JsonPathPredicate {
        private final Object value;

        public Equals(String jsonPath, Object value) throws IllegalArgumentException {
            super(jsonPath);
            this.value = value;
        }

        @Override
        public boolean test(Object json) {
            try {
                final Object valueToTest = readJsonPath(json);
                // When Rules are loaded from a CSV file, typing may be confused. So we allow different types to be equal.
                if (value == null || valueToTest == null || (valueToTest.getClass() == value.getClass())) {
                    return Objects.equals(valueToTest, value);
                } else {
                    return Objects.equals(valueToTest.toString(), value.toString());
                }
            } catch (RuntimeException ex) {
                return false;
            }
        }

        @Override
        public String toString() {
            return "Predicate[" + jsonPath.getPath() + "=" + value + "]";
        }
    }

    public static final class Match extends JsonPathPredicate {
        private final Pattern pattern;

        public Match(String jsonPath, String regex) throws IllegalArgumentException {
            super(jsonPath);
            if (regex == null) {
                throw new IllegalArgumentException("regex can not be null");
            }
            this.pattern = Pattern.compile(regex);
        }

        @Override
        public boolean test(Object json) {
            try {
                final Object valueToTest = readJsonPath(json);
                // When Rules are loaded from a CSV file, typing may be confused. So we allow different types to be equal.
                return pattern.matcher(valueToTest.toString()).matches();
            } catch (RuntimeException ex) {
                return false;
            }
        }

        @Override
        public String toString() {
            return "Predicate[" + jsonPath.getPath() + "=~" + pattern + "]";
        }
    }
}
