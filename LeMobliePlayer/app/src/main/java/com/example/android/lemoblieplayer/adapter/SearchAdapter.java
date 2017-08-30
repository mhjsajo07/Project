package com.example.android.lemoblieplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private Context mContext;

    private List<SearchBean.ItemData> mItemDataList = new ArrayList<>();

    public SearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setmItemDataListData(List<SearchBean.ItemData> itemDataList) {
        if (itemDataList != null && itemDataList.size() > 0) {
            mItemDataList.clear();
            mItemDataList.addAll(itemDataList);
            notifyDataSetChanged();
        }
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.item_netvideo_pager;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        SearchBean.ItemData itemData = mItemDataList.get(position);
        holder.tvItemName.setText(itemData.getItemTitle());
        holder.tvItemSize.setText(itemData.getKeywords());
        holder.tvItemDuration.setText(null);
        Glide.with(mContext).load(itemData.getItemImage().getImgUrl1())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(holder.ivItemIcon);
    }

    @Override
    public int getItemCount() {
        return mItemDataList == null ? 0 : mItemDataList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemIcon;
        private TextView tvItemDuration;
        private TextView tvItemName;
        private TextView tvItemSize;

        public SearchViewHolder(View itemView) {
            super(itemView);
            ivItemIcon = (ImageView) itemView.findViewById(R.id.iv_item_icon);
            tvItemDuration = (TextView) itemView.findViewById(R.id.tv_item_duration);
            tvItemName = (TextView) itemView.findViewById(R.id.tv_item_name);
            tvItemSize = (TextView) itemView.findViewById(R.id.tv_item_size);

        }
    }
}

