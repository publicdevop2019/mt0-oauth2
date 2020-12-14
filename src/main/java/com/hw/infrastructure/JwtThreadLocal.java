package com.hw.infrastructure;

public class JwtThreadLocal {
    public static final ThreadLocal<String> jwtThreadLocal = new ThreadLocal<>();

    public static void set(String user) {
        jwtThreadLocal.set(user);
    }

    public static void unset() {
        jwtThreadLocal.remove();
    }

    public static String get() {
        return jwtThreadLocal.get();
    }
}
