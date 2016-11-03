package com.linkx.wallpaper.utils;

import android.text.format.DateFormat;
import com.google.common.base.Strings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ulyx.yang on 2016/9/16.
 */
public class TextUtil {
    public static String formatSeconds(long seconds) {
        long sec = seconds % 60;
        long min = seconds / 60;
        long hour = 0;
        if (min >= 60) {
            hour = min / 60;
            min = min % 60;
        }
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    public static String formatTime(long millis) {
        return DateFormat.format("HH:mm", millis).toString();
    }

    public static String formatDay(long millis) {
        return DateFormat.format("yyyyMMdd", millis).toString();
    }

    public static long parseTime(String day, String time) {
        return parseTime(day + " " + time);
    }

    public static long parseTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.US);
        try {
            Date date = dateFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static float asFloat(String text, float defVal) {
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {

        }
        return defVal;
    }

    public static String strip(String input, String substr) {
        if (Strings.isNullOrEmpty(input) || Strings.isNullOrEmpty(substr)) {
            return input;
        }
        if (input.startsWith(substr)) {
            return input.substring(substr.length());
        }
        return input;
    }

}
