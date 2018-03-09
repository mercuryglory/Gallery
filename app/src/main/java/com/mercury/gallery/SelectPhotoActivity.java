package com.mercury.gallery;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang.zhonghao
 * @date 2018/3/5
 * @descript
 */

public class SelectPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SelectPhotoActivity";

    private CursorCallback mCursorLoader = new CursorCallback();

    private ImageAdapter mImageAdapter;
    private RecyclerView rvPhoto;
    private TextView tvFolderCatalog;
    private Toolbar toolBar;

    private PopupWindow mPopupWindow;
    private RelativeLayout rlBottom;

    private View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        getLoaderManager().initLoader(0, null, mCursorLoader);
        mImageAdapter = new ImageAdapter(this);

        rootView = LayoutInflater.from(this).inflate(R.layout.activity_select_photo, null);
        rlBottom = findViewById(R.id.rl_bottom);

        rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(this, 4));
        rvPhoto.addItemDecoration(new PhotoItemDecoration(5));
        rvPhoto.setAdapter(mImageAdapter);

        tvFolderCatalog = findViewById(R.id.tv_folder_catalog);
        toolBar = findViewById(R.id.toolBar);
        tvFolderCatalog.setOnClickListener(this);
        Log.i(TAG, "maxThread:" + Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_folder_catalog:
                View view = LayoutInflater.from(this).inflate(R.layout.item_image, null);
                if (mPopupWindow == null) {
                    mPopupWindow = new PopupWindow(this);
                    mPopupWindow.setContentView(view);
                    mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mPopupWindow.setOutsideTouchable(true);
                }
                rlBottom.measure(0, 0);
                int measuredHeight = rlBottom.getMeasuredHeight();
                mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, measuredHeight);
                break;

            default:
                break;

        }
    }


    private class CursorCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_INFO={
                MediaStore.Images.Media.DATA,           //文件的绝对路径
                MediaStore.Images.Media.DISPLAY_NAME,   //文件的显示名称
                MediaStore.Images.Media.DATE_ADDED,     //文件添加的日期
                MediaStore.Images.Media._ID,            //文件的主键
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME     //包含该图片的文件夹名
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0) {
                return new CursorLoader(SelectPhotoActivity.this, MediaStore.Images.Media
                        .EXTERNAL_CONTENT_URI, IMAGE_INFO, null, null, IMAGE_INFO[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> imageList = new ArrayList<>();

                if (data.getCount() > 0) {
                    data.moveToFirst();
                    while (data.moveToNext()) {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_INFO[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_INFO[1]));
                        long date = data.getLong(data.getColumnIndexOrThrow(IMAGE_INFO[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_INFO[3]));
                        String bucketName = data.getString(data.getColumnIndexOrThrow(IMAGE_INFO[4]));

                        Image image = new Image();
                        image.setPath(path);
                        image.setName(name);
                        image.setDate(date);
                        image.setId(id);
                        image.setBucketName(bucketName);

                        imageList.add(image);

                    }
                }
                mImageAdapter.setData(imageList);

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}

