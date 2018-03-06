package com.mercury.gallery;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author wang.zhonghao
 * @date 2018/3/6
 * @descript
 */

public class PhotoItemDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public PhotoItemDecoration(int margin) {
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        outRect.left = margin;
        outRect.top = margin;
        outRect.right = margin;
        outRect.bottom = margin;

    }
}
