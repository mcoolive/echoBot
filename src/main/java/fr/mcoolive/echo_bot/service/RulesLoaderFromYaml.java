package fr.mcoolive.echo_bot.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

public class RulesLoaderFromYaml {
    /**
     * This method parses the provided YAML input stream and creates a list of {@link Rule}.
     *
     * @param yaml   the content of a YAML configuration file
     * @param source provides additional context about the source of the rules and is used in the explanation, which can aid in logging or debugging purposes
     * @return the parsed rules
     * @throws JacksonException if the file is not in the expected format
     * @throws IOException      if the file is not readable
     */
    public List<Rule<Map<String, Object>, Map<String, Object>>> loadRules(InputStream yaml, String source, long defaultDelay) throws IOException {
        // TODO: it could be useful to check each rule against a JSON schema and display warnings if something is inconsistent.
        final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        List<RuleConfig> ruleConfigs = objectMapper.readValue(yaml, objectMapper.getTypeFactory().constructCollectionType(List.class, RuleConfig.class));

        return ruleConfigs.stream()
                .map(ruleCfg -> ruleCfg.toRule(source, defaultDelay))
                .collect(Collectors.toList());
    }

    private static class RuleConfig {
        public Map<String, String> matchers;

        @JsonProperty("scenario-id")
        public String scenarioId;

        @JsonProperty("delay-in-ms")
        public String delayInMs;

        @JsonProperty("acknowledgment")
        public Map<String, Object> outputMap;

        public Rule<Map<String, Object>, Map<String, Object>> toRule(String source, long defaultDelay) {
            final AndPredicate.Builder<Map<String, Object>> pb = new AndPredicate.Builder<>();
            if (matchers != null) matchers.forEach((k, v) -> pb.add(new JsonPathPredicate.Equals(k, v)));

            final String explanation = String.format("Dynamic result picked from YAML %s [scenarioId=%s].", source, scenarioId);
            return new Rule<>(pb.build(), outputMap, explanation, RulesLoader.parseDelay(delayInMs, defaultDelay), OK);
        }
    }
}
