package fr.mcoolive.echo_bot.service;

import org.springframework.http.HttpStatus;

import java.util.function.Predicate;

/**
 * Represents a rule to pick up a response to the incoming message.
 */
public class Rule<INPUT, OUTPUT> {
    private final Predicate<? super INPUT> predicate;
    private final Result<OUTPUT> result;

    public Rule(Predicate<? super INPUT> predicate, Result<OUTPUT> result) {
        this.predicate = predicate;
        this.result = result;
    }

    public Rule(Predicate<? super INPUT> predicate, OUTPUT output, String explanation, int delayInMs, HttpStatus httpStatus) {
        this(predicate, new Result<>(output, explanation, delayInMs, httpStatus));
    }

    @Override
    public String toString() {
        return "Rule[" + predicate + "==>" + result.getOutput() + "]";
    }

    public boolean test(INPUT input) {
        return predicate.test(input);
    }

    public Result<OUTPUT> getResult() {
        return result;
    }

    public static class Result<OUTPUT> {
        private final OUTPUT output;
        private final String explanation;
        private final int delayInMs;
        private final HttpStatus httpStatus;

        public Result(OUTPUT output, String explanation, int delayInMs, HttpStatus httpStatus) {
            this.output = output;
            this.explanation = explanation;
            this.delayInMs = delayInMs;
            this.httpStatus = httpStatus;
        }

        public Result(OUTPUT output, String explanation) {
            this(output, explanation, 0, HttpStatus.OK);
        }

        public OUTPUT getOutput() {
            return output;
        }

        public String getExplanation() {
            return explanation;
        }

        public int getDelayInMs() {
            return delayInMs;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }
    }
}
