package com.example.android.lechat.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.lechat.R;
import com.example.android.lechat.model.bean.GroupInfo;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51477 on 2017/8/23.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupListHolder> {
    private Context mContext;

    private List<EMGroup> mGroupInfoList = new ArrayList<>();

    public GroupListAdapter(Context mContext,OnClickItemListener onClickItemListener) {
        this.mContext = mContext;
        this.mOnClickItemListener = onClickItemListener;
    }

    private OnClickItemListener mOnClickItemListener;

    public interface OnClickItemListener {
        void onClick(EMGroup emGroup);
    }

    public void setGroupListData(List<EMGroup> groupInfoList) {
        if (groupInfoList != null && groupInfoList.size() >= 0) {
            mGroupInfoList.clear();
            mGroupInfoList.addAll(groupInfoList);
            notifyDataSetChanged();
        }
    }

    @Override
    public GroupListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.item_group_list;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new GroupListHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupListHolder holder, int position) {
        EMGroup emGroup = mGroupInfoList.get(position);

        holder.tvGroupName.setText(emGroup.getGroupName());
        holder.tvGroupSummary.setText(emGroup.getDescription());
    }

    @Override
    public int getItemCount() {
        return mGroupInfoList == null ? 0 : mGroupInfoList.size();
    }

    class GroupListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvGroupName;
        private TextView tvGroupSummary;

        public GroupListHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.tv_group_name);
            tvGroupSummary = (TextView) itemView.findViewById(R.id.tv_group_summary);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickItemListener.onClick(mGroupInfoList.get(getAdapterPosition()));
        }
    }
}
