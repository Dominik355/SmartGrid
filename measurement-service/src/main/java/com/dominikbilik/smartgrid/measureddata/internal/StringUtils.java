package com.dominikbilik.smartgrid.measureddata.internal;

import static java.lang.Math.max;

public final class StringUtils {

    public static String repeat(String str, String separator, int count) {
        StringBuilder sb = new StringBuilder((str.length() + separator.length()) * max(count, 0));

        for (int n = 0; n < count; n++) {
            if (n > 0) sb.append(separator);
            sb.append(str);
        }
        return sb.toString();
    }

    public static String valueOf(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

}
