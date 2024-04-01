package com.springbootwebcsocket.demo;

import java.util.Arrays;

public class StringUtils {

    public static boolean isAnyNullOrBlank(String... args) {
        if (args == null || args.length == 0) return true;
        return Arrays.stream(args).anyMatch(s -> s == null || s.isBlank() || s.isEmpty());
    }
}
