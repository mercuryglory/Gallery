package com.mercury.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author wang.zhonghao
 * @date 2018/8/21
 * @descript
 */

public class ImageGalleryActivity extends AppCompatActivity {

    private ViewPager vpImage;
    private Toolbar   toolBar;
    private TextView  tvSelect;
    private ImageView ivSelect;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        vpImage = findViewById(R.id.vp_image);

        Intent intent = getIntent();
        ArrayList<Image> imageList = intent.getParcelableArrayListExtra("imageList");
        int currentPos = intent.getIntExtra("currentPos", 0);

        ImageGalleryAdapter adapter = new ImageGalleryAdapter(imageList);
        vpImage.setAdapter(adapter);

        vpImage.setCurrentItem(currentPos);
    }

    private class ImageGalleryAdapter extends PagerAdapter{

        private ArrayList<Image> mList;


        public ImageGalleryAdapter(ArrayList<Image> list) {
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
                ImageLoader.getInstance().loadImage(imageView, image.getPath(), ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, null, false);
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            container.addView(imageView);
            return imageView;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);
            view.setDrawingCacheEnabled(true);
            Bitmap drawingCache = view.getDrawingCache();
            view.setDrawingCacheEnabled(false);
            drawingCache.recycle();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
