package com.tapusd.poc.util;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.Map;

public class PatchUtil {
    private PatchUtil() {}

    public static <E> void applyPatch(E entity, Map<String, Object> patchRequest) {
        PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);

        patchRequest.forEach((key, value) -> {
            if (value == null || "null".equals(value) || "".equals(value)) {
                Class<?> propertyType = accessor.getPropertyType(key);
                Object defaultValue = getDefaultValueForType(propertyType);
                accessor.setPropertyValue(key, defaultValue);
            } else {
                accessor.setPropertyValue(key, value);
            }
        });
    }

    public static Object getDefaultValueForType(Class<?> type) {
        if (type == Byte.TYPE) {
            return 0;
        } else if (type == Short.TYPE) {
            return 0;
        } else if (type == Integer.TYPE) {
            return 0;
        } else if (type == Long.TYPE) {
            return 0L;
        } else if (type == Float.TYPE) {
            return 0F;
        } else if (type == Double.TYPE) {
            return 0D;
        } else if (type == Boolean.TYPE) {
            return false;
        } else if (type == Character.TYPE) {
            return '\u0000';
        } else {
            return null; // Default value for Object types
        }
    }
}
