package com.mercury.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * @author wang.zhonghao
 * @date 2018/3/6
 * @descript
 */

public class LoaderTask extends AsyncTask<String, Void, Bitmap> {

    private WeakReference<ImageView> mWeakReference;
    private String                   path;
    private int                      reqWidth;
    private int                      reqHeight;

    public static final String TAG = "LoaderTask";
    public static int count;

    public LoaderTask(ImageView imageView, String path, int reqWidth, int reqHeight) {
        mWeakReference = new WeakReference<ImageView>(imageView);
        this.path = path;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }


    @Override
    protected Bitmap doInBackground(String... strings) {
        String path = strings[0];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = ImageLoader.getInstance().calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap != null) {
            Log.i(TAG, "path:" + path);
            ImageLoader.getInstance().addImageToCache(path, bitmap);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = mWeakReference.get();
        if (imageView != null && path.equals(imageView.getTag())) {
            count++;
            Log.i(TAG, "onPostExecute: " + count);
            imageView.setImageBitmap(bitmap);
        }

    }
}
