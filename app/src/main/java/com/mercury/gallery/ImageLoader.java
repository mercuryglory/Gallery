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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author wang.zhonghao
 * @date 2018/3/6
 * @descript
 */

public class ImageLoader {

    private static ImageLoader sInstance;

    private final LruCache<String, Bitmap> mCache;

    private static final String TAG = "ImageLoader";

    private Executor mExecutor;

    private final Handler mainHandler;

    public static ImageLoader getInstance() {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader();
                }
            }
        }
        return sInstance;
    }

    public ImageLoader() {
        mainHandler = new Handler(Looper.getMainLooper());
        mExecutor = Executors.newFixedThreadPool(6);
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

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap
                    newValue) {
                Log.i(TAG, toString());

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

    public void loadImage(final ImageView imageView, String path, int reqWidth, int
            reqHeight) {
        Bitmap bitmap = getImageFromCache(path, reqWidth, reqHeight);
        imageView.setTag(path);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.parseColor("#E9EBF0")));
            //            LoaderTask task = new LoaderTask(imageView, path, reqWidth, reqHeight);
            //            ExecutorService executorService = Executors.newFixedThreadPool(1000);
            //            task.executeOnExecutor(executorService, path);

            mExecutor.execute(new WorkerRunnable(imageView, path, reqWidth, reqHeight));

        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int
            reqHeight) {
        //源图片的宽高
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            //自身的宽高和在布局中显示宽高的比例
            while (width / inSampleSize > reqWidth || height / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private class WorkerRunnable implements Runnable, Comparable {

        private ImageView mImageView;
        private String    path;
        private int       reqWidth;
        private int       reqHeight;

        WorkerRunnable(ImageView imageView, String path, int reqWidth, int reqHeight) {
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
            options.inSampleSize = ImageLoader.getInstance().calculateInSampleSize(options,
                    reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            final Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ImageLoader.getInstance().addImageToCache(path, reqWidth, reqHeight, bitmap);

            if (mImageView != null && path.equals(mImageView.getTag())) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, WorkerRunnable.this.hashCode() + ":" + System
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
