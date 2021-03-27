package com.qiezitv.common;

import java.util.regex.Pattern;

public class CommonUtils {
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
