package com.mercury.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author wang.zhonghao
 * @date 2018/3/6
 * @descript
 */

public class ImageLoader {

    private static ImageLoader sInstance;

    private LruCache<String, Bitmap> mCache;

    public static final String TAG = "ImageLoader";

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
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 8);
        mCache = new LruCache<String,Bitmap>(maxMemory);
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
            LoaderTask task = new LoaderTask(imageView, path, reqWidth, reqHeight);
            Log.i(TAG, "loadImage: " + path);
//            task.execute(path);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
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


}
