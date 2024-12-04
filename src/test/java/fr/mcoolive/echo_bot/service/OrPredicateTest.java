package fr.mcoolive.echo_bot.service;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrPredicateTest {
    final Predicate<String> alwaysTrue = x -> true;
    final Predicate<String> alwaysFalse = x -> false;

    @Test
    void emptyOrReturnsFalse() {
        final Predicate<String> orPredicate = new OrPredicate.Builder<String>()
                .build();
        assertFalse(orPredicate.test("{}"));
    }

    @Test
    void trueReturnsTrue() {
        final Predicate<String> orPredicate = new OrPredicate.Builder<String>()
                .add(alwaysTrue)
                .build();
        assertTrue(orPredicate.test("{}"));
    }

    @Test
    void falseReturnsFalse() {
        final Predicate<String> orPredicate = new OrPredicate.Builder<String>()
                .add(alwaysFalse)
                .build();
        assertFalse(orPredicate.test("{}"));
    }

    @Test
    void trueOrTrueReturnsTrue() {
        final Predicate<String> orPredicate = new OrPredicate.Builder<String>()
                .add(alwaysTrue).add(alwaysTrue)
                .build();
        assertTrue(orPredicate.test("{}"));
    }
    @Test
    void trueOrFalseReturnsTrue() {
        final Predicate<String> orPredicate = new OrPredicate.Builder<String>()
                .add(alwaysTrue).add(alwaysFalse)
                .build();
        assertTrue(orPredicate.test("{}"));
    }
    @Test
    void falseOrTrueReturnsTrue() {
        final Predicate<String> orPredicate = new OrPredicate.Builder<String>()
                .add(alwaysFalse).add(alwaysTrue)
                .build();
        assertTrue(orPredicate.test("{}"));
    }
    @Test
    void falseOrFalseReturnsTrue() {
        final Predicate<String> orPredicate = new OrPredicate.Builder<String>()
                .add(alwaysFalse).add(alwaysFalse)
                .build();
        assertFalse(orPredicate.test("{}"));
    }
}
