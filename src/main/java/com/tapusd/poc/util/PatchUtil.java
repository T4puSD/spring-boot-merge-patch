package com.tapusd.poc.util;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.math.BigDecimal;
import java.util.Map;

public class PatchUtil {
    private PatchUtil() {}

    public static <E> void applyPatch(E entity, Map<String, Object> patchRequest) {
        PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);

        patchRequest.forEach((key, value) -> {
            if ("null".equals(value) || "".equals(value)) {
                Class<?> propertyType = accessor.getPropertyType(key);
                Object defaultValue = getDefaultValueForType(propertyType);
                accessor.setPropertyValue(key, defaultValue);
            } else {
                accessor.setPropertyValue(key, value);
            }
        });
    }

    private static Object getDefaultValueForType(Class<?> type) {
        if (type == Integer.TYPE || type == Integer.class) {
            return 0;   // Default value for int
        } else if (type == BigDecimal.class) {
            return BigDecimal.ZERO; // Default value for boolean
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            return false; // Default value for boolean
        } else if (type.isPrimitive()) {
            return 0;   // Default value for other primitives
        } else {
            return null; // Default value for Object types
        }
    }
}
