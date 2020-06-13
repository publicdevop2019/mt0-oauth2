package com.hw.shared;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends RuntimeException> extends Function<T, R> {
    R apply(T var1);

    default Consumer<T> andThen(Consumer<? super R> after) {
        Objects.requireNonNull(after);
        return t -> after.accept(this.apply(t));
    }

    default <U> BiConsumer<T, U> andThen(BiConsumer<? super R, ? super U> after) {
        Objects.requireNonNull(after);
        return (t, u) -> after.accept(this.apply(t), u);
    }

    default <U> ThrowingBiConsumer<T, U, E> andThen(ThrowingBiConsumer<? super R, ? super U, ? extends E> after) {
        Objects.requireNonNull(after);
        return (t, u) -> after.accept(this.apply(t), u);
    }
}
