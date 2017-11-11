package com.project.brainbot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.SizeF;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.text.format.DateUtils.isToday;

public class Utils {

    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatDateTime(long timeInMillis) {
        if(isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    public static String getImagePath(String userName){


        return null;
    }

}
