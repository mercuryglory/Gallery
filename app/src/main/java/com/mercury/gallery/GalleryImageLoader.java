package com.mercury.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author wang.zhonghao
 * @date 2018/8/22
 * @descript
 */

public class GalleryImageLoader implements ImageLoader {

    private static GalleryImageLoader sInstance;

    private final LruCache<String, Bitmap> mCache;

    private Executor mExecutor;

    private static final Handler mainHandler=new Handler(Looper.getMainLooper());

    public static GalleryImageLoader getInstance() {
        if (sInstance == null) {
            sInstance = new GalleryImageLoader();
        }
        return sInstance;
    }

    public GalleryImageLoader() {
        mExecutor = Executors.newFixedThreadPool(6);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 8);
        mCache = new LruCache<String, Bitmap>(maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                return value.getByteCount();
            }
        };
    }

    public void addImageToCache(String key, int width, int height, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mCache) {
                mCache.put(key + width + height, bitmap);
            }
        }
    }

    public Bitmap getImageFromCache(String key, int width, int height) {
        synchronized (mCache) {
            return mCache.get(key + width + height);
        }
    }

    @Override
    public void loadImage(@NonNull ImageView imageView, String path, int reqWidth, int reqHeight) {
        Bitmap bitmap = getImageFromCache(path, reqWidth, reqHeight);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.parseColor("#000000")));
            mExecutor.execute(new WorkerRunnable(new WeakReference<>(this),imageView, path, reqWidth, reqHeight));

        }
    }

    private static class WorkerRunnable implements Runnable, Comparable {

        private GalleryImageLoader mLoader;
        private ImageView mImageView;
        private String    path;
        private int       reqWidth;
        private int       reqHeight;

        WorkerRunnable(WeakReference<GalleryImageLoader> loader, ImageView imageView, String path, int reqWidth, int reqHeight) {
            this.mLoader = loader.get();
            this.mImageView = imageView;
            this.path = path;
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
        }

        @Override
        public void run() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = DisplayUtils.calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            final Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            mLoader.addImageToCache(path, reqWidth, reqHeight, bitmap);

            if (mImageView != null) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(bitmap);
                    }
                });
            }

        }

        @Override
        public int compareTo(@NonNull Object o) {
            return 0;
        }
    }
}
