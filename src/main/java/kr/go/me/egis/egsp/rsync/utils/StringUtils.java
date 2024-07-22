package kr.go.me.egis.egsp.rsync.utils;

public class StringUtils {
    public static String nullToEmpty(Object obj){
        return obj == null ? "" : obj.toString();
    }

    public static boolean isNullOrEmpty(String checkString){
        return checkString == null || checkString.isEmpty();
    }
}
