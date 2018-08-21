package com.mercury.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang.zhonghao
 * @date 2018/8/20
 * @descript
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{

    public  LayoutInflater            mInflater;
    public  Context                   mContext;
    private List<AlbumBucket> mData;
    private int selectPosition;

    public AlbumAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = mInflater.inflate(R.layout.item_album, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AlbumBucket albumBucket = mData.get(position);
        holder.tvBucketName.setText(albumBucket.getName());
        holder.tvCount.setText(albumBucket.getImageList().size() + "张");
        holder.rbSelect.setVisibility(albumBucket.isChecked() ? View.VISIBLE : View.INVISIBLE);

        String path = albumBucket.getImageList().get(0).getPath();
        int size = DisplayUtils.dp2px(mContext, 80);

        ImageLoader.getInstance().loadImage(holder.ivAlbum, path, size, size);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumBucket preAlbum = mData.get(selectPosition);
                preAlbum.setChecked(false);
                notifyItemChanged(selectPosition);
                selectPosition = position;
                albumBucket.setChecked(true);
                holder.rbSelect.setVisibility(View.VISIBLE);
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelect(position, albumBucket);
                }
            }
        });

    }

    private OnSelectListener mOnSelectListener;

    public void setOnSelectListener(OnSelectListener selectListener) {
        this.mOnSelectListener = selectListener;
    }

    public interface OnSelectListener{
        void onSelect(int position, AlbumBucket bucket);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<AlbumBucket> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAlbum;
        TextView tvBucketName;
        TextView tvCount;
        RadioButton rbSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.iv_album);
            tvBucketName = itemView.findViewById(R.id.tv_bucket_name);
            tvCount = itemView.findViewById(R.id.tv_count);
            rbSelect = itemView.findViewById(R.id.rb_select);
        }
    }
}
