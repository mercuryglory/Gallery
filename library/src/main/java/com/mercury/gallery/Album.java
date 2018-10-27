package com.mercury.gallery;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * @author wang.zhonghao
 * @date 2018/10/27
 * @descript
 */

public class Album {

    public static final String RESULT_KEY = "pathList";

    int requestCode;
    int maxCount;
    Context mContext;

    public Album(Builder builder) {
        this.requestCode = builder.requestCode;
        this.maxCount = builder.maxCount;
        this.mContext = builder.mContext;
    }

    public Album() {
        this(new Builder());
    }

    public static final class Builder {
        int requestCode;
        int maxCount;
        Context mContext;

        public Builder() {
            requestCode = 100;
            maxCount = 9;
        }

        public Builder requestCode(int code) {
            requestCode = code;
            return this;
        }

        public Builder maxCount(int count) {
            maxCount = checkCount(count);
            return this;
        }

        public Builder with(Context context) {
            mContext = checkContext(context);
            return this;
        }

        private Context checkContext(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("context can not be null");
            }
            if (context instanceof Application) {
                throw new IllegalArgumentException("cannot start Activity from the Application " +
                        "component");
            }
            return context;
        }

        private int checkCount(int maxCount) {
            if (maxCount < 0) {
                throw new IllegalArgumentException("count < 0");
            }
            return maxCount;
        }

        public Album build() {
            return new Album(this);
        }
    }


    public void startAlbum() {
        if (mContext == null) {
            return;
        }
        Intent intent = new Intent(mContext, SelectPhotoActivity.class);
        intent.putExtra("maxCount", maxCount);
        if (mContext instanceof FragmentActivity) {
            ((FragmentActivity) mContext).startActivityForResult(intent, requestCode);
        } else if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent, requestCode);
        }
    }

    public static ArrayList<String> parseResult(Intent data) {
        if (data == null || data.getStringArrayListExtra(RESULT_KEY) == null) {
            return new ArrayList<>();
        }
        return data.getStringArrayListExtra(RESULT_KEY);
    }

}
