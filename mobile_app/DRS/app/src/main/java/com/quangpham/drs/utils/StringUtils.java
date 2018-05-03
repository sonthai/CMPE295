package com.quangpham.drs.utils;

/**
 * Created by quangpham on 5/3/18.
 */

public class StringUtils {

    public static String capitalizeAndShorten(String source) {
        String result = "";

        String[] strArr = source.split(" ");
        int len = strArr.length;
        for (int i = 0; i < len; i++) {
            String str = strArr[i];
            result += str + " ";
            if (result.length() > 30 && i < (len -1))
                return (result + strArr[len - 1]).toUpperCase().trim();
        }
        return result.toUpperCase().trim();
    }

    public static String removeSpecialCharacters(String source) {
        String specials[] = { "'", "\""};
        String result = source;

        for (String s : specials) {
            result = result.replaceAll(s, "");
        }
        return result;
    }
}
