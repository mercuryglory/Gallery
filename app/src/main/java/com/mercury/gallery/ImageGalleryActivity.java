package com.mercury.gallery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @author wang.zhonghao
 * @date 2018/8/21
 * @descript
 */

public class ImageGalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager vpImage;
    private Toolbar   toolBar;
    private TextView  tvSelect;
    private ImageView ivSelect;
    private MenuItem  menuTitle;

    private ArrayList<Image> imageList;
    private ArrayList<String> selectedList;
    private String currentPath;

    public static final String TAG = "ImageGalleryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        toolBar = findViewById(R.id.toolBar);
        vpImage = findViewById(R.id.vp_image);
        tvSelect = findViewById(R.id.tv_select);
        ivSelect = findViewById(R.id.iv_select);

        tvSelect.setOnClickListener(this);
        ivSelect.setOnClickListener(this);

        Intent intent = getIntent();
        imageList = intent.getParcelableArrayListExtra("imageList");
        int currentPos = intent.getIntExtra("currentPos", 0);
        selectedList = intent.getStringArrayListExtra("selectList");

        ImageGalleryAdapter adapter = new ImageGalleryAdapter(imageList);
        vpImage.setAdapter(adapter);


        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        PageListener listener = new PageListener();
        vpImage.addOnPageChangeListener(listener);
        vpImage.setCurrentItem(currentPos);
        listener.onPageSelected(currentPos);

    }

    @Override
    public void onClick(View v) {
        if (!ivSelect.isSelected()) {
            if (selectedList.size() == 6) {
                Toast.makeText(this, "你最多只能选择6张照片", Toast.LENGTH_SHORT).show();
            } else {
                ivSelect.setSelected(!ivSelect.isSelected());
                selectedList.add(currentPath);
            }
        } else {
            ivSelect.setSelected(!ivSelect.isSelected());
            selectedList.remove(currentPath);
        }
        updateSelectSize();

    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            currentPath = imageList.get(position).getPath();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(position + 1 + "/" + imageList.size());
            }
            ivSelect.setSelected(selectedList.contains(currentPath));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        menuTitle = menu.findItem(R.id.item_title);
        updateSelectSize();
        return super.onCreateOptionsMenu(menu);
    }

    private void updateSelectSize() {
        if (menuTitle != null) {
            if (selectedList.isEmpty()) {
                menuTitle.setTitle("发送");
            } else {
                menuTitle.setTitle("发送(" + selectedList.size() + "/6)");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.item_title) {
            completeSelected();

        }
        return super.onOptionsItemSelected(item);
    }

    private void completeSelected() {
        Intent intent = new Intent();
        if (selectedList.isEmpty() && !imageList.isEmpty()) {
            selectedList.add(currentPath);
        }
        intent.putStringArrayListExtra("pathList", selectedList);
        setResult(RESULT_OK,intent);
        finish();
    }

    private class ImageGalleryAdapter extends PagerAdapter{

        private ArrayList<Image> mList;


        private ImageGalleryAdapter(ArrayList<Image> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setBackground(new ColorDrawable(Color.BLACK));
            Image image = mList.get(position);
            if (!TextUtils.isEmpty(image.getPath())) {
                ImageLoaderFactory.createGalleryLoader().loadImage(imageView, image.getPath(),
                        ScreenUtils.getScreenWidth(container.getContext()), ScreenUtils
                                .getScreenHeight(container.getContext()));
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            container.addView(imageView);
            return imageView;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//            ImageView view = (ImageView) object;
//            container.removeView(view);
//            view.setDrawingCacheEnabled(true);
//            Bitmap drawingCache = view.getDrawingCache();
//            view.setDrawingCacheEnabled(false);
//            drawingCache.recycle();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
