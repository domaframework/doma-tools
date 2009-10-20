package org.seasar.doma.extension.domax.util;

public final class ClassUtil {

    public static String getPackageName(String qualifiedName) {
        AssertionUtil.assertNotNull(qualifiedName);
        int pos = qualifiedName.lastIndexOf('.');
        if (pos < 0) {
            return "";
        }
        return qualifiedName.substring(0, pos);
    }

    public static String getSimpleName(String qualifiedName) {
        AssertionUtil.assertNotNull(qualifiedName);
        int pos = qualifiedName.lastIndexOf('.');
        if (pos < 0) {
            return qualifiedName;
        }
        return qualifiedName.substring(pos + 1);
    }
}
