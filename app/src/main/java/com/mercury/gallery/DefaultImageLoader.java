package com.mercury.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

/**
 * @author wang.zhonghao
 * @date 2018/3/6
 * @descript
 */

public class DefaultImageLoader implements ImageLoader{

    private static DefaultImageLoader sInstance;

    private final LruCache<String, Bitmap> mCache;

    private static final String TAG = "ImageLoader";

    private Executor mExecutor;

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static DefaultImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (DefaultImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new DefaultImageLoader();
                }
            }
        }
        return sInstance;
    }

    public DefaultImageLoader() {
        mExecutor = new FifoPriorityThreadPoolExecutor(8);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 8);
        long l = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long l1 = Runtime.getRuntime().maxMemory() / 1024 / 1024;
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
    public void clearCache() {
        if (mCache != null) {
            mCache.evictAll();
        }
    }

    @Override
    public void loadImage(@NonNull final ImageView imageView, String path, int reqWidth, int
            reqHeight) {
        Bitmap bitmap = getImageFromCache(path, reqWidth, reqHeight);
        imageView.setTag(path);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.parseColor("#E9EBF0")));

            mExecutor.execute(new WorkerRunnable(new WeakReference<>(this),imageView, path, reqWidth, reqHeight));

        }
    }

    private static class WorkerRunnable implements Runnable, Comparable {

        private DefaultImageLoader mLoader;
        private ImageView mImageView;
        private String    path;
        private int       reqWidth;
        private int       reqHeight;

        WorkerRunnable(WeakReference<DefaultImageLoader> loader, ImageView imageView, String path, int reqWidth, int reqHeight) {
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
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = DisplayUtils.calculateInSampleSize(options,
                    reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            final Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            mLoader.addImageToCache(path, reqWidth, reqHeight, bitmap);

            if (mImageView != null && path.equals(mImageView.getTag())) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, hashCode() + ":" + System
                                .currentTimeMillis());
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
