package com.example.android.lechat.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.android.lechat.R;
import com.example.android.lechat.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51477 on 2017/8/23.
 */

public class PickContactAdapter extends RecyclerView.Adapter<PickContactAdapter.PickContactViewHolder> {
    private Context mContext;
    private List<PickContactInfo> mPickContactInfoList = new ArrayList<>();
    private List<String> members = new ArrayList<>();

    public PickContactAdapter(Context mContext, OnItemClickListener onItemClickListener, List<String> mExistmembers) {
        this.mContext = mContext;
        this.mOnItemClickListener = onItemClickListener;
        members.clear();
        members = mExistmembers;
    }

    private OnItemClickListener mOnItemClickListener;

    public List<String> getPickContacts() {
        List<String> picks = new ArrayList<>();
        for (PickContactInfo pickContactInfo : mPickContactInfoList) {
            if (pickContactInfo.isChecked()) {
                picks.add(pickContactInfo.getUser().getName());
            }
        }
        return picks;
    }

    public interface OnItemClickListener {
        void onClick(PickContactInfo pickContactInfo);
    }

    public void setPickContactData(List<PickContactInfo> pickContactInfoList) {
        if (pickContactInfoList != null && pickContactInfoList.size() >= 0) {
            mPickContactInfoList.clear();
            mPickContactInfoList.addAll(pickContactInfoList);
            notifyDataSetChanged();
        }
    }

    @Override
    public PickContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.item_pick_contact;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new PickContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PickContactViewHolder holder, int position) {
        PickContactInfo pickContactInfo = mPickContactInfoList.get(position);

        holder.cbPickPick.setChecked(pickContactInfo.isChecked());
        holder.tvPickName.setText(pickContactInfo.getUser().getName());
        if (members.contains(pickContactInfo.getUser().getHxid())) {
            holder.cbPickPick.setChecked(true);
            pickContactInfo.setIsChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return mPickContactInfoList == null ? 0 : mPickContactInfoList.size();
    }

    class PickContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckBox cbPickPick;
        private TextView tvPickName;

        public PickContactViewHolder(View itemView) {
            super(itemView);
            cbPickPick = (CheckBox) itemView.findViewById(R.id.cb_pick_pick);
            tvPickName = (TextView) itemView.findViewById(R.id.tv_pick_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onClick(mPickContactInfoList.get(getAdapterPosition()));
        }
    }
}
