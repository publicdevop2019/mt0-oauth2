package com.hw.shared;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends RuntimeException> extends BiFunction<T, U, R> {
    R apply(T var1, U var2) throws E;

}
