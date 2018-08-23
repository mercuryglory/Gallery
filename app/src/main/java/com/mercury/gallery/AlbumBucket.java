package com.mercury.gallery;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang.zhonghao
 * @date 2018/8/20
 * @descript
 */

public class AlbumBucket implements Parcelable{

    private String name;

    private List<Image> imageList = new ArrayList<>();

    private boolean isChecked;

    private long coverDate;

    public long getCoverDate() {
        if (!imageList.isEmpty()) {
            this.coverDate = imageList.get(0).getDate();
        }
        return coverDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void addImage(Image image) {
        this.imageList.add(image);
    }

    protected AlbumBucket() {

    }

    private AlbumBucket(Parcel in) {
        name = in.readString();
        imageList = in.createTypedArrayList(Image.CREATOR);
        isChecked = in.readByte() != 0;
    }

    public static final Creator<AlbumBucket> CREATOR = new Creator<AlbumBucket>() {
        @Override
        public AlbumBucket createFromParcel(Parcel in) {
            return new AlbumBucket(in);
        }

        @Override
        public AlbumBucket[] newArray(int size) {
            return new AlbumBucket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(imageList);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public String toString() {
        return "AlbumBucket{" +
                "name='" + name + '\'' +
                ", imageList=" + imageList +
                ", isChecked=" + isChecked +
                '}';
    }

}
