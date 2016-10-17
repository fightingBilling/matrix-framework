package org.matrix.framework.core.util;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.matrix.framework.core.collection.converter.DecimalHexConvert;
import org.matrix.framework.core.platform.exception.MatrixException;

public final class StringUtils {

    public static final DecimalHexConvert HEXCONVERT = new DecimalHexConvert();
    public static final Pattern PATTERN = Pattern.compile("\\{:partition\\}");

    public static int compare(String text1, String text2) {
        if ((text1 == null) && (text2 == null))
            return 0;
        if ((text1 != null) && (text2 == null)) {
            return 1;
        }
        if (text1 == null) {
            return -1;
        }
        return text1.compareTo(text2);
    }

    public static boolean hasText(String text) {
        if (text == null)
            return false;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i)))
                return true;
        }
        return false;
    }

    public static boolean equals(String text1, String text2) {
        if (text1 == null) {
            return text2 == null;
        }
        return text1.equals(text2);
    }

    public static String truncate(String text, int maxLength) {
        if (text == null)
            return null;
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength);
    }

    public static String trim(String text) {
        if (text == null)
            return null;
        return text.trim();
    }

    public static int getAbsoluteLength(String text) {
        int charlength = text.length();
        return (text.getBytes(Charset.forName("UTF-8")).length - charlength) / 2 + charlength;
    }

    public static String format(String originalString, boolean multiPartition, Object[] partitions) {
        int arrLength = null == partitions ? 0 : partitions.length;
        Matcher matcher = PATTERN.matcher(originalString);
        StringBuffer buffer = new StringBuffer();
        int index = 0;
        while (matcher.find()) {
            if (arrLength == 0) {
                throw new MatrixException(new StringBuilder().append("original sql is [").append(originalString)
                        .append("] missing partition args").toString());
            }
            if (multiPartition) {
                if (index >= arrLength) {
                    throw new MatrixException(new StringBuilder().append("original sql is [").append(originalString)
                            .append("] missing partition args[").append(index).append("+]").toString());
                }

                matcher.appendReplacement(buffer, String.valueOf(partitions[index]));

                index++;
            } else {
                matcher.appendReplacement(buffer, String.valueOf(partitions[0]));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String getUniqueNo(long incrementNumber, String[] prefixs) {
        StringBuilder builder = new StringBuilder();
        long currentSeconds = System.currentTimeMillis() / 1000L;
        if ((null != prefixs) && (prefixs.length > 0)) {
            builder.append("[");
            for (String prefix : prefixs) {
                builder.append(prefix);
            }
            builder.append("]");
        }
        builder.append(HEXCONVERT.toDecimalHex(currentSeconds)).append("-").append(incrementNumber);
        return builder.toString();
    }
}
