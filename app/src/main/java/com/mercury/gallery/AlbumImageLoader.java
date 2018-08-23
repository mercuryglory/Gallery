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
 * @date 2018/8/23
 * @descript
 */

public class AlbumImageLoader implements ImageLoader {

    private static AlbumImageLoader sInstance = new AlbumImageLoader();

    private final LruCache<String, Bitmap> mCache;

    private Executor mExecutor;

    private static final Handler mainHandler =new Handler(Looper.getMainLooper());

    public static AlbumImageLoader getInstance() {
        return sInstance;
    }

    private AlbumImageLoader() {
        mExecutor = Executors.newFixedThreadPool(6);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 8);
        mCache = new LruCache<String, Bitmap>(maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public void loadImage(@NonNull ImageView imageView, String path, int reqWidth, int reqHeight) {
        Bitmap bitmap = getImageFromCache(path, reqWidth, reqHeight);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.parseColor("#E9EBF0")));
            imageView.setBackground(new ColorDrawable(Color.BLACK));

            mExecutor.execute(new WorkerRunnable(new WeakReference<>(this),imageView, path, reqWidth, reqHeight));

        }
    }

    public void addImageToCache(String key, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mCache) {
                mCache.put("album_" + key, bitmap);
            }
        }
    }

    public Bitmap getImageFromCache(String key, int width, int height) {
        synchronized (mCache) {
            return mCache.get(key + width + height);
        }
    }

    private static class WorkerRunnable implements Runnable, Comparable {

        private AlbumImageLoader mLoader;
        private ImageView mImageView;
        private String    path;
        private int       reqWidth;
        private int       reqHeight;

        WorkerRunnable(WeakReference<AlbumImageLoader> loader, ImageView imageView, String path, int reqWidth, int reqHeight) {
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
            options.inSampleSize = DisplayUtils.calculateMaxSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            final Bitmap bitmap = BitmapFactory.decodeFile(path,options);
            mLoader.addImageToCache(path, bitmap);

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
