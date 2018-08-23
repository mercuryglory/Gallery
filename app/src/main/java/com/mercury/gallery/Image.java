package com.mercury.gallery;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author wang.zhonghao
 * @date 2018/3/5
 * @descript
 */

public class Image implements Parcelable{

    private int id;
    private String path;
    private String name;
    private String bucketName;
    private long date;
    private boolean checked;

    public Image() {

    }

    private Image(Parcel in) {
        id = in.readInt();
        path = in.readString();
        name = in.readString();
        bucketName = in.readString();
        date = in.readLong();
        checked = in.readByte() != 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(bucketName);
        dest.writeLong(date);
        dest.writeByte((byte) (checked ? 1 : 0));
    }

}
