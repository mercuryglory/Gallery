package com.mercury.gallery;

/**
 * @author wang.zhonghao
 * @date 2018/8/22
 * @descript
 */

public class ImageLoaderFactory {

    public static ImageLoader createImageLoader() {
        return DefaultImageLoader.getInstance();
    }

    public static ImageLoader createGalleryLoader() {
        return GalleryImageLoader.getInstance();
    }
}
