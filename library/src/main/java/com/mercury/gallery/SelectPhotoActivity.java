package com.mercury.gallery;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wang.zhonghao
 * @date 2018/3/5
 * @descript
 */

public class SelectPhotoActivity extends AppCompatActivity implements View.OnClickListener, ImageAdapter.OnCheckListener {

    public static final String TAG = "SelectPhotoActivity";
    public static final int REQUEST_GALLERY = 100;

    private CursorCallback mCursorLoader = new CursorCallback();

    private ImageAdapter mImageAdapter;
    private RecyclerView rvPhoto;
    private TextView tvFolderCatalog;
    private FrameLayout flContent;
    private Toolbar toolBar;
    private MenuItem menuTitle;

    private FolderPopupWindow mPopupWindow;
    private RelativeLayout rlBottom;
    private View viewBottom;

    private List<AlbumBucket> albumList;
    private ArrayList<String> mPathList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        getLoaderManager().initLoader(0, null, mCursorLoader);
        mImageAdapter = new ImageAdapter(this);

        viewBottom = findViewById(R.id.view_bottom);
        rlBottom = findViewById(R.id.rl_bottom);
        flContent = findViewById(R.id.fl_content);
        flContent.getForeground().setAlpha(0);

        rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(this, 4));
        rvPhoto.addItemDecoration(new PhotoItemDecoration(5));
        rvPhoto.setAdapter(mImageAdapter);

        tvFolderCatalog = findViewById(R.id.tv_folder_catalog);
        toolBar = findViewById(R.id.toolBar);
        tvFolderCatalog.setOnClickListener(this);

        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("本地图片");
        }

        mImageAdapter.setOnCheckListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.item_title) {
            if (mPathList.isEmpty()) {
                Toast.makeText(this, "您还没有选择图片", Toast.LENGTH_SHORT).show();
            } else {
                completeSelected(mPathList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //finish the selection of images and return the paths
    private void completeSelected(ArrayList<String> pathList) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("pathList", pathList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            ArrayList<String> pathList = data.getStringArrayListExtra("pathList");
            if (pathList != null) {
                completeSelected(pathList);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        menuTitle = menu.findItem(R.id.item_title);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_folder_catalog) {
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
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flContent.getForeground().setAlpha(0);
                }
            });
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
                //                    rlBottom.measure(0, 0);
                //                    int measuredHeight = rlBottom.getMeasuredHeight();
                //                    mPopupWindow.showAtLocation(viewBottom, Gravity.BOTTOM, 0,
                // 0 );
                mPopupWindow.showAsDropDown(viewBottom, 0, 0);
                flContent.getForeground().setAlpha(127);
            }
            Log.i(TAG, "onClick: " + mPopupWindow.isShowing());

        }
    }

    @Override
    public void onCheck(ArrayList<String> pathList) {
        mPathList = pathList;
        if (pathList.isEmpty()) {
            menuTitle.setTitle("发送");
        } else {
            menuTitle.setTitle("发送(" + pathList.size() + "/6)");
        }
    }

    private class CursorCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_INFO = {
                MediaStore.Images.Media.DATA,           //文件的绝对路径
                MediaStore.Images.Media.DISPLAY_NAME,   //文件的显示名称
                MediaStore.Images.Media.DATE_TAKEN,     //文件添加的日期
                MediaStore.Images.Media._ID,            //文件的主键
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME     //包含该图片的文件夹名
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader: ");
            if (id == 0) {
                return new CursorLoader(SelectPhotoActivity.this, MediaStore.Images.Media
                        .EXTERNAL_CONTENT_URI, IMAGE_INFO, null, null, IMAGE_INFO[2] + " DESC," +
                        IMAGE_INFO[1] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> imageList = new ArrayList<>();
                Map<String,AlbumBucket> albumMap = new HashMap<>();

                if (data.getCount() > 0) {
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

                AlbumBucket bucketAll = new AlbumBucket();
                bucketAll.setChecked(true);
                bucketAll.setName("全部");
                albumList = new ArrayList<>();
                bucketAll.setImageList(imageList);
                albumList.add(bucketAll);

                for (Map.Entry<String, AlbumBucket> entry : albumMap.entrySet()) {
                    albumList.add(entry.getValue());
                }
                Collections.sort(albumList);
                data.close();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.i(TAG, "onLoaderReset: ");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoaderFactory.createImageLoader().clearCache();
        ImageLoaderFactory.createAlbumLoader().clearCache();
    }
}

