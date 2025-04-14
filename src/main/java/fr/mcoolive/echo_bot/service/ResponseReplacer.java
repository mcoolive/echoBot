package fr.mcoolive.echo_bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface ResponseReplacer<RESPONSE> {

    RESPONSE replace(RESPONSE response, Map<String, String> variables);

    static boolean isJoker(String value) {
        return "?".equals(value) || "ECHO".equals(value);
    }

    static boolean isJoker(JsonNode node) {
        return node != null && node.isTextual() && isJoker(node.asText());
    }

    class JsonNodeReplacer implements ResponseReplacer<JsonNode> {

        private final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * Replace "?" with the eponymous value from the incoming message.
         */
        @Override
        public JsonNode replace(JsonNode jsonNode, Map<String, String> variables) {
            ObjectNode result = null;
            final Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                final String fieldName = field.getKey();
                final JsonNode fieldValue = field.getValue();
                if (isJoker(fieldValue)) {
                    if (result == null) result = shallowCopy(jsonNode);
                    result.put(fieldName, variables.get(fieldName));
                }
            }

            return result != null ? result : jsonNode;
        }

        private ObjectNode shallowCopy(JsonNode jsonNode) {
            ObjectNode shallowCopy = objectMapper.createObjectNode();
            final Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                shallowCopy.set(field.getKey(), field.getValue());
            }
            return shallowCopy;
        }
    }

    class JsonStringReplacer implements ResponseReplacer<String> {

        private final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * Replace "?" with the eponymous value from the incoming message.
         */
        @Override
        public String replace(String json, Map<String, String> variables)  {
            final ObjectNode jsonNode;
            try {
                jsonNode = (ObjectNode) objectMapper.readTree(json);
            } catch (JsonProcessingException ex) {
                return json;
            }
            final Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                final String fieldName = field.getKey();
                final JsonNode fieldValue = field.getValue();
                if (isJoker(fieldValue)) {
                    jsonNode.put(fieldName, variables.get(fieldName));
                }
            }
            return jsonNode.toString();
        }
    }

    class JsonStringSpelReplacer implements ResponseReplacer<String> {

        private final ObjectMapper objectMapper = new ObjectMapper();
        private final ExpressionParser parser = new SpelExpressionParser();

        /**
         * Replace "?" with the eponymous value from the incoming message.
         */
        @Override
        public String replace(String json, Map<String, String> variables)  {
            final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            variables.forEach(evaluationContext::setVariable);

            final ObjectNode jsonNode;
            try {
                jsonNode = (ObjectNode) objectMapper.readTree(json);
            } catch (JsonProcessingException ex) {
                return json;
            }
            final Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> field = fields.next();
                final String fieldName = field.getKey();
                final JsonNode fieldValue = field.getValue();
                if (fieldValue != null && fieldValue.isTextual()) {
                    parser.parseExpression(fieldValue.asText()).getValue(evaluationContext);
                    jsonNode.put(fieldName, variables.get(fieldName));
                }
            }
            return jsonNode.toString();
        }
    }

}
