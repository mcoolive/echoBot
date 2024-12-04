package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndPredicateTest {
    final Predicate<String> alwaysTrue = x -> true;
    final Predicate<String> alwaysFalse = x -> false;

    @Test
    void emptyAndReturnsTrue() {
        final Predicate<String> andPredicate = new AndPredicate.Builder<String>()
                .build();
        assertTrue(andPredicate.test("{}"));
    }

    @Test
    void trueReturnsTrue() {
        final Predicate<String> andPredicate = new AndPredicate.Builder<String>()
                .add(alwaysTrue)
                .build();
        assertTrue(andPredicate.test("{}"));
    }

    @Test
    void falseReturnsFalse() {
        final Predicate<String> andPredicate = new AndPredicate.Builder<String>()
                .add(alwaysFalse)
                .build();
        assertFalse(andPredicate.test("{}"));
    }

    @Test
    void trueAndTrueReturnsTrue() {
        final Predicate<String> andPredicate = new AndPredicate.Builder<String>()
                .add(alwaysTrue).add(alwaysTrue)
                .build();
        assertTrue(andPredicate.test("{}"));
    }

    @Test
    void trueAndFalseReturnsFalse() {
        final Predicate<String> andPredicate = new AndPredicate.Builder<String>()
                .add(alwaysTrue).add(alwaysFalse)
                .build();
        assertFalse(andPredicate.test("{}"));
    }

    @Test
    void falseAndTrueReturnsFalse() {
        final Predicate<String> andPredicate = new AndPredicate.Builder<String>()
                .add(alwaysFalse).add(alwaysTrue)
                .build();
        assertFalse(andPredicate.test("{}"));
    }

    @Test
    void falseAndFalseReturnsFalse() {
        final Predicate<String> andPredicate = new AndPredicate.Builder<String>()
                .add(alwaysFalse).add(alwaysFalse)
                .build();
        assertFalse(andPredicate.test("{}"));
    }
}
