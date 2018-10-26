package com.mercury.gallery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import java.util.List;

/**
 * @author wang.zhonghao
 * @date 2018/3/12
 * @descript
 */

public class FolderPopupWindow extends PopupWindow {

    private AlbumAdapter mAdapter;

    public FolderPopupWindow(Context context) {
        this(context, null);
    }

    public FolderPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FolderPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_folder, null);
        RecyclerView rvAlbum = view.findViewById(R.id.rv_album);
        setContentView(view);
        setFocusable(true);
        setWidth(ScreenUtils.getScreenWidth(context));
        setHeight(ScreenUtils.getScreenHeight(context) / 4 * 3);
        setAnimationStyle(R.style.popup_anim);
        ColorDrawable drawable = new ColorDrawable(Color.BLACK);
        setBackgroundDrawable(drawable);

        setAdapter(rvAlbum, context);

    }

    public void setAdapter(RecyclerView recyclerView, Context context) {
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView is not found in the view hierarchy");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new AlbumAdapter(context);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration
                .VERTICAL));
        recyclerView.setAdapter(mAdapter);

    }

    public void setData(List<AlbumBucket> list) {
        if (mAdapter == null) {
            throw new IllegalStateException("The adapter has not been initialized");
        }
        if (list != null) {
            mAdapter.setData(list);
        }
    }

    public AlbumAdapter getAdapter() {
        return mAdapter;
    }

}
