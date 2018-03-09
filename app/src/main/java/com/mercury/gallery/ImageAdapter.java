package com.mercury.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private List<Image>    mData;
    public  LayoutInflater mInflater;
    public  Context        mContext;

    public static final String TAG = "ImageAdapter";

    public ImageAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image = mData.get(position);
        String path = image.getPath();
        int id = image.getId();

        int itemWidth = ScreenUtils.getScreenWidth(mContext)/4;
        int itemHeight = ScreenUtils.getScreenHeight(mContext)/4;
//        Glide.with(mContext).load(path).placeholder(new ColorDrawable(Color.parseColor("#E9EBF0")
//        )).into(holder.ivPhoto);
        ImageLoader.getInstance().loadImage(holder.ivPhoto, path,
                itemWidth, itemHeight);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Image> list){
        if (list != null) {
            mData.clear();
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;

        private ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }


    static class OnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        }
    }

}
