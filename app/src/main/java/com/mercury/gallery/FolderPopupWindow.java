package com.mercury.gallery;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * @author wang.zhonghao
 * @date 2018/3/12
 * @descript
 */

public class FolderPopupWindow extends PopupWindow {

    public FolderPopupWindow(Context context) {
        this(context, null);
    }

    public FolderPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FolderPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_folder, null);
        setContentView(view);
        setFocusable(true);
        setWidth(ScreenUtils.getScreenWidth(context));
        setHeight(ScreenUtils.getScreenHeight(context) / 4 * 3);
        setAnimationStyle(R.style.popup_anim);
        ColorDrawable drawable = new ColorDrawable(0x66000000);
        setBackgroundDrawable(drawable);

    }
}
