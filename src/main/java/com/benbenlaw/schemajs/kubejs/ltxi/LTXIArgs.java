package com.benbenlaw.schemajs.kubejs.ltxi;

import java.util.List;
import java.util.Map;

/** Small helpers for reading the loosely typed values scripts pass to the LTXI components. */
final class LTXIArgs {

    private LTXIArgs() {
    }

    static float toFloat(Object value, float fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number n) {
            return n.floatValue();
        }
        return Float.parseFloat(String.valueOf(value));
    }

    static int toInt(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    static boolean toBool(Object value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    static Object get(List<?> list, int index) {
        return index < list.size() ? list.get(index) : null;
    }

    static Object get(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
