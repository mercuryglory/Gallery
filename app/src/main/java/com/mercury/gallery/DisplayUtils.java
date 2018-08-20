package com.mercury.gallery;

import android.content.Context;

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

}
