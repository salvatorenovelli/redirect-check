package com.github.salvatorenovelli.redirectcheck.http;

public class SafeStringEncoder {


    private final static char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Escape non-ascii characters prefixing % without decoding them.
     */
    static String encodeString(String s) {

        int n = s.length();
        if (n == 0)
            return s;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; ; ) {
            char c = s.charAt(i);
            if (c >= '\u0080') {
                appendEscape(sb, (byte) (c & 0xFF));
            } else {
                sb.append(c);
            }
            if (++i >= n)
                break;
        }

        return sb.toString();
    }

    private static void appendEscape(StringBuilder sb, byte b) {
        sb.append('%');
        sb.append(hexDigits[(b >> 4) & 0x0f]);
        sb.append(hexDigits[(b >> 0) & 0x0f]);
    }
}
