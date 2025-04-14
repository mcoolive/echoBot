package fr.mcoolive.echo_bot.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.springframework.http.HttpStatus.OK;

public class RulesLoaderFromCsv {
    private static final char COLUMN_SEPARATOR = ';';
    private static final String EXPECTED_COLUMN_SUFFIX = "Expected";
    private static final String SCENARIO_ID_COLUMN_NAME = "scenarioId";
    private static final String DELAY_COLUMN_NAME = "delay";
    private static final String NULL_VALUE = "NULL";
    private static final String STAR_VALUE = "*";

    /**
     * This method parses the provided CSV input stream and creates a list of {@link Rule}.
     *
     * @param csv    the content of a CSV configuration file
     * @param source provides additional context about the source of the rules and is used in the explanation, which can aid in logging or debugging purposes
     * @return the parsed rules
     * @throws JacksonException if the file is not in the expected format
     * @throws IOException      if the file is not readable
     */
    public List<Rule<Map<String, Object>, Map<String, Object>>> loadRules(InputStream csv, String source, long defaultDelay) throws IOException {
        final CsvMapper csvMapper = new CsvMapper();

        final CsvSchema csvSchema = CsvSchema.emptySchema()
                .withHeader() // Use the first row as header
                .withColumnSeparator(COLUMN_SEPARATOR);
        final ObjectReader csvReader = csvMapper.readerFor(Map.class)
                .with(csvSchema)
                .with(CsvParser.Feature.SKIP_EMPTY_LINES)
                .with(CsvParser.Feature.TRIM_SPACES);

        try (final MappingIterator<Map<String, String>> rowIterator = csvReader.readValues(csv)) {
            final List<Rule<Map<String, Object>, Map<String, Object>>> rules = new ArrayList<>();
            while (rowIterator.hasNext()) {
                final int lineNr = rowIterator.getCurrentLocation().getLineNr();
                final Map<String, String> row = rowIterator.next();
                final AndPredicate.Builder<Object> pb = new AndPredicate.Builder<>();
                final Map<String, Object> outputMap = new HashMap<>();
                String scenarioId = null;
                String delayInMs = null;

                for (Map.Entry<String, String> entry : row.entrySet()) {
                    final String key = entry.getKey();
                    final String value = entry.getValue();
                    if (key == null || value == null) continue;

                    if (isSpecialColumn(key)) {
                        // The "scenarioId" column is processed as a comment.
                        if (SCENARIO_ID_COLUMN_NAME.equalsIgnoreCase(key)) {
                            scenarioId = value;
                        }
                        if (DELAY_COLUMN_NAME.equalsIgnoreCase(key)) {
                            delayInMs = value;
                        }

                    } else if (isExpectedColumn(key)) {
                        if (value.isEmpty()) continue;
                        pb.add(getPredicate(removeExpectedSuffix(key), value));

                    } else {
                        // Building the returned acknowledgement
                        if (!NULL_VALUE.equalsIgnoreCase(value)) {
                            outputMap.put(key, value);
                        }
                    }
                }

                final String explanation = String.format("Dynamic result picked from CSV %s [scenarioId=%s, lineNr=%d].", source, scenarioId, lineNr);
                rules.add(new Rule<>(pb.build(), outputMap, explanation, RulesLoader.parseDelay(delayInMs, defaultDelay), OK));
            }
            return rules;
        }
    }

    private static boolean isSpecialColumn(String value) {
        return value != null && (value.equalsIgnoreCase(SCENARIO_ID_COLUMN_NAME) || value.equalsIgnoreCase(DELAY_COLUMN_NAME));
    }

    private static boolean isExpectedColumn(String value) {
        return value != null && value.endsWith(EXPECTED_COLUMN_SUFFIX);
    }

    private static String removeExpectedSuffix(String value) {
        if (value != null && value.endsWith(EXPECTED_COLUMN_SUFFIX))
            return value.substring(0, value.length() - EXPECTED_COLUMN_SUFFIX.length());
        else
            return value;
    }

    /**
     * Build a predicate for the given cell.
     * <li> Expecting the "NULL" value means the eponymous field in the incoming message is missing or null.
     * <li> expecting "*" means the eponymous field in the incoming message is defined with a non-empty value.
     */
    private static Predicate<Object> getPredicate(String key, String value) {
        if (NULL_VALUE.equalsIgnoreCase(value)) {
            return new JsonPathPredicate.Equals(key, null);
        } else if (STAR_VALUE.equalsIgnoreCase(value)) {
            return new JsonPathPredicate.Match(key, ".+");
        } else {
            return new JsonPathPredicate.Equals(key, value);
        }
    }
}
