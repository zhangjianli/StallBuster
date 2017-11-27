package com.github.zhangjianli.stallbuster.utils;

import android.content.Context;

import java.text.SimpleDateFormat;

/**
 * Created by jinlizhang on 19/11/2017.
 */

public final class Utils {

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private Utils() {

    }
}
