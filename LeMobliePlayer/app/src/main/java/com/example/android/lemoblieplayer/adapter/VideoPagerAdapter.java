package com.example.android.lemoblieplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.bean.MediaItem;
import com.example.android.lemoblieplayer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51477 on 2017/8/28.
 */

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoPagerViewHolder> {
    private Context mContext;
    private List<MediaItem> mMediaItemList = new ArrayList<>();
    private Utils mUtils;

    private boolean mIsVideo;

    public VideoPagerAdapter(Context mContext ,boolean isVideo) {
        this.mContext = mContext;
        mUtils = new Utils();
        mIsVideo = isVideo;
    }

    private OnVideoItemClickListener mOnVideoItemClickListener;

    public void setVideoDate(List<MediaItem> mediaItemList, OnVideoItemClickListener onVideoItemClickListener) {
        if (mediaItemList != null && mediaItemList.size() > 0) {
            mMediaItemList.clear();
            mMediaItemList.addAll(mediaItemList);
            mOnVideoItemClickListener = onVideoItemClickListener;
            notifyDataSetChanged();
        }
    }

    @Override
    public VideoPagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.item_video_pager;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new VideoPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoPagerViewHolder holder, int position) {
        MediaItem mediaItem = mMediaItemList.get(position);

        if(!mIsVideo){
            holder.ivItemIcon.setImageResource(R.drawable.music_icon);
        }
        String s=mediaItem.getName();
        s=s.substring(0, s.lastIndexOf('.'));
        holder.tvItemName.setText(s);
        holder.tvItemDuration.setText(mUtils.stringForTime((int) mediaItem.getDuration()));
        holder.tvItemSize.setText(android.text.format.Formatter.formatFileSize(mContext, mediaItem.getSize()));
    }

    @Override
    public int getItemCount() {
        return mMediaItemList == null ? 0 : mMediaItemList.size();
    }

    class VideoPagerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivItemIcon;
        private TextView tvItemName;
        private TextView tvItemDuration;
        private TextView tvItemSize;

        public VideoPagerViewHolder(View itemView) {
            super(itemView);
            ivItemIcon = (ImageView) itemView.findViewById(R.id.iv_item_icon);
            tvItemName = (TextView) itemView.findViewById(R.id.tv_item_name);
            tvItemDuration = (TextView) itemView.findViewById(R.id.tv_item_duration);
            tvItemSize = (TextView) itemView.findViewById(R.id.tv_item_size);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnVideoItemClickListener.OnClick(mMediaItemList.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnVideoItemClickListener {
        void OnClick(MediaItem mediaItem, int position);
    }
}
