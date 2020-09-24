package com.hw.shared;

public class UserThreadLocal {
    public static final ThreadLocal<String> userThreadLocal = new ThreadLocal<>();

    public static void set(String user) {
        userThreadLocal.set(user);
    }

    public static void unset() {
        userThreadLocal.remove();
    }

    public static String get() {
        return userThreadLocal.get();
    }
}
