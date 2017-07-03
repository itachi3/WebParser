package com.scout24.utils;

import java.util.List;

public class Utils {
    public static boolean isEmptyorNull(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmptyorNull(List str) {
        return str == null || str.isEmpty();
    }
}
