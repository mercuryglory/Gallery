package com.mercury.gallery;

import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * @author wang.zhonghao
 * @date 2018/8/22
 * @descript
 */

public interface ImageLoader {

    void loadImage(@NonNull final ImageView imageView, String path, int reqWidth, int reqHeight);

}
