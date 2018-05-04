package com.reedoei.eunomia.ast;

import java.util.Arrays;

public class AstUtil {
    public static String qualifiedToPath(final String qualifiedName) {
        final String[] temp = qualifiedName.split("\\."); // regex
        return String.join("/", Arrays.asList(temp).subList(0, temp.length - 1));
    }
}
