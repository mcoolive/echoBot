package fr.mcoolive.echo_bot.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FunctionalInterface
public interface RulesEngine<INPUT, OUTPUT> {
    /**
     * Returns the Result associated with the first Rule that match the given input.
     */
    Optional<Rule.Result<OUTPUT>> execute(INPUT input);

    static <INPUT, OUTPUT> RulesEngine<INPUT, OUTPUT> basic(List<Rule<INPUT, OUTPUT>> rules) {
        return new Basic<>(rules);
    }

    static <OUTPUT> RulesEngine<Object, OUTPUT> fallback(OUTPUT fallbackOutput) {
        return new Fallback<>(fallbackOutput);
    }

    /**
     * @throws NullPointerException if after is null
     */
    default RulesEngine<INPUT, OUTPUT> andThen(RulesEngine<? super INPUT, OUTPUT> after) {
        Objects.requireNonNull(after);
        return new Chain<>(this, after);
    }

    class Chain<INPUT, OUTPUT> implements RulesEngine<INPUT, OUTPUT> {
        private final RulesEngine<? super INPUT, OUTPUT> first;
        private final RulesEngine<? super INPUT, OUTPUT> second;
        public Chain(RulesEngine<? super INPUT, OUTPUT> first, RulesEngine<? super INPUT, OUTPUT> second) {
            Objects.requireNonNull(first);
            Objects.requireNonNull(second);
            this.first = first;
            this.second = second;
        }

        public Optional<Rule.Result<OUTPUT>> execute(INPUT input) {
            // With Java 9, we could write: return first.execute(input).or(() ->second.execute(input));
            final Optional<Rule.Result<OUTPUT>> result1 = first.execute(input);
            if (result1.isPresent()) return result1;
            else return second.execute(input);
        }
    }

    class Basic<INPUT, OUTPUT> implements RulesEngine<INPUT, OUTPUT> {
        private final List<Rule<INPUT, OUTPUT>> rules;

        public Basic(List<Rule<INPUT, OUTPUT>> rules) {
            Objects.requireNonNull(rules);
            this.rules = rules;
        }

        public Optional<Rule.Result<OUTPUT>> execute(INPUT input) {
            return rules
                    .stream()
                    .filter(rule -> rule.test(input))
                    .map(Rule::getResult)
                    .findFirst();
        }
    }

    class Fallback<OUTPUT> implements RulesEngine<Object, OUTPUT> {
        private final Rule.Result<OUTPUT> result;

        public Fallback(Rule.Result<OUTPUT> result) {
            Objects.requireNonNull(result);
            this.result = result;
        }

        public Fallback(OUTPUT output) {
            this(new Rule.Result<>(output, "Fallback result."));
        }

        public Optional<Rule.Result<OUTPUT>> execute(Object input) {
            return Optional.of(result);
        }
    }
}
