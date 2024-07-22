package kr.go.me.egis.egsp.rsync.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String getCurrentTimestamp(){
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(now);
    }

    public static String getCurrentDate_yyyyMMdd(){
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(now);
    }

    public static String getCurrentDate_yyyyMMdd_HHmmss(){
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return df.format(now);
    }

    public static String getCurrentDate_yyyyMMddHHmmss(){
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(now);
    }

    public static String getCurrentDate_yyyyMMddHHmm(){
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        return df.format(now);
    }

}
