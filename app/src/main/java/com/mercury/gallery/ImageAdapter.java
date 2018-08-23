package com.mercury.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<Image>    mData;
    public  LayoutInflater mInflater;
    public  Context        mContext;
    private ArrayList<String> pathList;

    public ImageAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mData = new ArrayList<>();
        pathList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Image image = mData.get(position);
        final String path = image.getPath();

        int itemWidth = ScreenUtils.getScreenWidth(mContext) / 4;
        int itemHeight = ScreenUtils.getScreenHeight(mContext) / 4;

        ImageLoaderFactory.createImageLoader().loadImage(holder.ivPhoto, path,
                itemWidth, itemHeight);
        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image.isChecked() && pathList.size() >= 6) {
                    Toast.makeText(mContext, "你最多只能选择6张照片", Toast.LENGTH_SHORT).show();
                    return;
                }
                image.setChecked(!v.isSelected());
                holder.cbSelect.setSelected(image.isChecked());
                if (image.isChecked()) {
                    pathList.add(path);
                } else {
                    pathList.remove(path);
                }
                if (mOnCheckListener != null) {
                    mOnCheckListener.onCheck(pathList);
                }
            }
        });
        holder.cbSelect.setSelected(image.isChecked());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageGalleryActivity.class);
                intent.putParcelableArrayListExtra("imageList", (ArrayList<? extends Parcelable>) mData);
                intent.putExtra("currentPos", position);
                intent.putExtra("isSelect", image.isChecked());
                intent.putStringArrayListExtra("selectList", pathList);
                mContext.startActivity(intent);

            }
        });

    }

    private OnItemClickListener mOnItemClickListener;
    private OnCheckListener mOnCheckListener;

    public interface OnItemClickListener{
        void onItemClick();
    }

    public interface OnCheckListener{
        void onCheck(ArrayList<String> pathList);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.mOnCheckListener = onCheckListener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Image> list) {
        if (list != null) {
            mData.clear();
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        ImageView  cbSelect;

        private ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }
    }

}
