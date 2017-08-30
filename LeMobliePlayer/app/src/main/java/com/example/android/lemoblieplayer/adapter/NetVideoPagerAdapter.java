package com.example.android.lemoblieplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.bean.MediaItem;

import java.util.List;

/**
 * Created by 51477 on 2017/8/28.
 */

public class NetVideoPagerAdapter extends BaseAdapter {
    private Context context;
    private List<MediaItem> mediaItems;

    public NetVideoPagerAdapter(Context context, List<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems == null ? 0 : mediaItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mediaItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHoder viewHoder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_netvideo_pager, null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_name);
            viewHoder.tv_desc = (TextView) convertView.findViewById(R.id.tv_item_size);
            viewHoder.tv_duration = (TextView) convertView.findViewById(R.id.tv_item_duration);

            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }
        MediaItem mediaItem = mediaItems.get(position);

        viewHoder.tv_name.setText(mediaItem.getName());
        long a = mediaItem.getDuration();
        long hour = a / 3600;
        long minute = a % 3600 / 60;
        long second = a % 60;
        String time = null;
        if (hour == 0) {
            time = minute + ":" + second;
            if (second == 0) {
                time = null;
                time = minute + ":00";
                if (second < 10) {
                    time = null;
                    time = minute + ":0" + second;
                }
            }
        }
        viewHoder.tv_duration.setText(time);
        //使用xUtils3请求图片
//        x.image().bind(viewHoder.iv_icon, mediaItem.getImageUrl());

        Glide.with(context).load(mediaItem.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHoder.iv_icon);
        viewHoder.tv_desc.setText(mediaItem.getMovieDesc());
        return convertView;
    }


//    @Override
//    public void onBindViewHolder(NetVideoPagerViewHolder holder, int position) {
//        MediaItem mediaItem = mMediaItemList.get(position);
//        holder.tvItemName.setText(mediaItem.getName());
//        long a = mediaItem.getDuration();
//        long hour = a / 3600;
//        long minute = a % 3600 / 60;
//        long second = a % 60;
//        String time = null;
//        if (hour == 0) {
//            time = minute + ":" + second;
//            if (second == 0) {
//                time = null;
//                time = minute + ":00";
//                if (second < 10) {
//                    time = null;
//                    time = minute + ":0" + second;
//                }
//            }
//        }
//        holder.tvItemDuration.setText(time);
//        holder.tvItemSize.setText(mediaItem.getMovieDesc());
//        x.image().bind(holder.ivItemIcon, mediaItem.getImageUrl());
//    }


    class ViewHoder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
        TextView tv_duration;
    }
}
