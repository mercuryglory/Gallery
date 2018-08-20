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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private FolderPopupWindow mPopupWindow;
    private RelativeLayout rlBottom;
    private View viewBottom;

    private View rootView;

    private List<AlbumBucket> albumList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        getLoaderManager().initLoader(0, null, mCursorLoader);
        mImageAdapter = new ImageAdapter(this);

        rootView = LayoutInflater.from(this).inflate(R.layout.activity_select_photo, null);
        viewBottom = findViewById(R.id.view_bottom);
        rlBottom = findViewById(R.id.rl_bottom);

        rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(this, 4));
        rvPhoto.addItemDecoration(new PhotoItemDecoration(5));
        rvPhoto.setAdapter(mImageAdapter);

        tvFolderCatalog = findViewById(R.id.tv_folder_catalog);
        toolBar = findViewById(R.id.toolBar);
        tvFolderCatalog.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_folder_catalog:

                if (mPopupWindow == null) {
                    mPopupWindow = new FolderPopupWindow(this);
                    mPopupWindow.setOutsideTouchable(true);
                    mPopupWindow.setData(albumList);
                }
                if (mPopupWindow.getAdapter() != null) {
                    mPopupWindow.getAdapter().setOnSelectListener(new AlbumAdapter.OnSelectListener() {

                        @Override
                        public void onSelect(int position, AlbumBucket bucket) {
                            mImageAdapter.setData(bucket.getImageList());
                            tvFolderCatalog.setText(bucket.getName());
                            mPopupWindow.dismiss();
                        }
                    });
                }
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
//                    rlBottom.measure(0, 0);
//                    int measuredHeight = rlBottom.getMeasuredHeight();
//                    mPopupWindow.showAtLocation(viewBottom, Gravity.BOTTOM, 0, 0 );
                    mPopupWindow.showAsDropDown(viewBottom, 0, 0);
                }
                Log.i(TAG, "onClick: " + mPopupWindow.isShowing());
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
            Log.i(TAG, "onCreateLoader: ");
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
                Map<String,AlbumBucket> albumMap = new HashMap<>();


                AlbumBucket bucketAll = new AlbumBucket();
                bucketAll.setChecked(true);
                bucketAll.setName("全部");

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

                        AlbumBucket bucket = albumMap.get(bucketName);
                        if (bucket != null) {
                            bucket.addImage(image);
                        } else {
                            bucket = new AlbumBucket();
                            bucket.setName(bucketName);
                            bucket.addImage(image);
                            albumMap.put(bucketName, bucket);
                        }

                    }
                }
                mImageAdapter.setData(imageList);

                albumList = new ArrayList<>();
                bucketAll.setImageList(imageList);
                albumList.add(bucketAll);

                for (Map.Entry<String, AlbumBucket> entry : albumMap.entrySet()) {
                    albumList.add(entry.getValue());
                }

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.i(TAG, "onLoaderReset: ");
        }
    }
}

