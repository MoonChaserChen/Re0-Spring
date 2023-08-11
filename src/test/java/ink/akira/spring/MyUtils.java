package ink.akira.spring;

import java.util.StringJoiner;

public class MyUtils {
    public static String concat(String... strs) {
        StringJoiner sj = new StringJoiner("");
        for (String str : strs) {
            sj.add(str);
        }
        return sj.toString();
    }
}
