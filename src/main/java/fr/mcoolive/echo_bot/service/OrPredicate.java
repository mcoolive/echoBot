package fr.mcoolive.echo_bot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A combined predicate that represents a short-circuiting logical OR of its compound predicates.
 */
public final class OrPredicate<T> implements Predicate<T> {
    private final List<Predicate<? super T>> predicates;

    private OrPredicate(List<Predicate<? super T>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean test(T t) {
        for (Predicate<? super T> predicate : predicates) {
            if (predicate.test(t)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return predicates.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "Or[", "]"));
    }

    public static class Builder<T> {
        private final ArrayList<Predicate<? super T>> predicates = new ArrayList<>();

        public Builder<T> add(Predicate<? super T> predicate) {
            if (predicate != null) predicates.add(predicate);
            return this;
        }
        public OrPredicate<T> build() {
            return new OrPredicate<>(predicates);
        }
    }
}
