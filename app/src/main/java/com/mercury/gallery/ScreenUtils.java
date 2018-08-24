package com.mercury.gallery;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @author wang.zhonghao
 * @date 2018/3/6
 * @descript
 */

public class ScreenUtils {

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.widthPixels;

    }

    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.heightPixels;

    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
