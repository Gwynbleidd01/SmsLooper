package app.sms.com.smslooper;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class StringUtils {

    private static CharsetEncoder asciiEncoder =
            Charset.forName("ASCII").newEncoder();

    public static boolean isPureAscii(String v) {
        return asciiEncoder.canEncode(v);
    }
}
