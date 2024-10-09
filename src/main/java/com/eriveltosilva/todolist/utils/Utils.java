package com.eriveltosilva.todolist.utils;

import java.util.Set;
import java.beans.PropertyDescriptor;
import java.util.HashSet;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapperImpl srcWrapper = new BeanWrapperImpl(source);

        PropertyDescriptor[] pds = srcWrapper.getPropertyDescriptors();

        Set<String> emptyPropertyNames = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            Object srcValue = srcWrapper.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyPropertyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyPropertyNames.size()];
        return emptyPropertyNames.toArray(result);
    }

}
