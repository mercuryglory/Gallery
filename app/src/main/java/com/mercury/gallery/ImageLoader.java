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

    public static final String TAG = "ImageLoader";

    private Executor mExecutor;

    private final Handler mainHandler;

    private int count;

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
        mCache = new LruCache<String, Bitmap>(maxMemory);
    }

    public void addImageToCache(String key, Bitmap bitmap) {
        if (mCache.get(key) == null && bitmap != null) {
            synchronized (mCache) {
                mCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getImageFromCache(String key) {
        synchronized (mCache) {
            return mCache.get(key);
        }
    }

    public void loadImage(final ImageView imageView, String path, int reqWidth, int
            reqHeight) {
        Bitmap bitmap = getImageFromCache(path);
        imageView.setTag(path);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.parseColor("#E9EBF0")));
//            LoaderTask task = new LoaderTask(imageView, path, reqWidth, reqHeight);
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
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

        public WorkerRunnable(ImageView imageView, String path, int reqWidth, int reqHeight) {
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
            if (bitmap != null) {
                ImageLoader.getInstance().addImageToCache(path, bitmap);
            }
            if (mImageView != null && path.equals(mImageView.getTag())) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, WorkerRunnable.this.hashCode() + ":" + System.currentTimeMillis());
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
