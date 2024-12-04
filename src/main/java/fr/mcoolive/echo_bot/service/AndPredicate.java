package fr.mcoolive.echo_bot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A combined predicate that represents a short-circuiting logical AND of its compound predicates.
 */
public final class AndPredicate<T> implements Predicate<T> {
    private final List<Predicate<? super T>> predicates;

    private AndPredicate(List<Predicate<? super T>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(T t) {
        for (Predicate<? super T> predicate : predicates) {
            if (!predicate.test(t)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return predicates.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "And[", "]"));
    }

    public static class Builder<T> {
        private final ArrayList<Predicate<? super T>> predicates = new ArrayList<>();

        public Builder<T> add(Predicate<? super T> predicate) {
            if (predicate != null) predicates.add(predicate);
            return this;
        }

        public AndPredicate<T> build() {
            return new AndPredicate<>(predicates);
        }
    }
}
