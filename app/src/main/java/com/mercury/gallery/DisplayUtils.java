package com.mercury.gallery;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * @author wang.zhonghao
 * @date 2018/8/20
 * @descript
 */

public class DisplayUtils {

    public static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int
            reqHeight) {
        //源图片的宽高
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            //自身的宽高和在布局中显示宽高的比例
            while (width / inSampleSize > reqWidth || height / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
