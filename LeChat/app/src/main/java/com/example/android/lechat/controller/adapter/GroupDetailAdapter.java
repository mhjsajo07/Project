package com.example.android.lechat.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.lechat.R;
import com.example.android.lechat.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51477 on 2017/8/24.
 */

public class GroupDetailAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mIsCanModify;
    private List<UserInfo> mUserInfoList = new ArrayList<>();
    private boolean isDeleteModel;

    public GroupDetailAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsCanModify = isCanModify;
        mOnGroupDetailListener = onGroupDetailListener;
    }

    private OnGroupDetailListener mOnGroupDetailListener;

    public void setUserData(List<UserInfo> userInfoList) {
        if (userInfoList != null && userInfoList.size() >= 0) {
            mUserInfoList.clear();
            initUsers();
            mUserInfoList.addAll(0, userInfoList);
            notifyDataSetChanged();
        }
    }

    public boolean isDeleteModel() {
        return isDeleteModel;
    }

    public void setDeleteModel(boolean deleteModel) {
        isDeleteModel = deleteModel;
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");
        mUserInfoList.add(delete);
        mUserInfoList.add(0, add);
    }

    @Override
    public int getCount() {
        return mUserInfoList == null ? 0 : mUserInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return mUserInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final UserInfo userInfo = mUserInfoList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_groupdetail, null);
            holder.ivGroupImage = (ImageView) convertView.findViewById(R.id.iv_group_image);
            holder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            holder.ivGroupDelete = (ImageView) convertView.findViewById(R.id.iv_group_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mIsCanModify) {
            if (position == getCount() - 1) {
                if (isDeleteModel) {
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.tvGroupName.setVisibility(View.INVISIBLE);
                    holder.ivGroupImage.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.ivGroupDelete.setVisibility(View.GONE);
                }
            } else if (position == getCount() - 2) {
                if (isDeleteModel) {
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.tvGroupName.setVisibility(View.INVISIBLE);
                    holder.ivGroupImage.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.ivGroupDelete.setVisibility(View.INVISIBLE);
                }
            } else {
                convertView.setVisibility(View.VISIBLE);
                holder.tvGroupName.setText(userInfo.getName());
                holder.tvGroupName.setVisibility(View.VISIBLE);
                if (isDeleteModel) {
                    holder.ivGroupDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.ivGroupDelete.setVisibility(View.GONE);
                }
            }
            if (position == getCount() - 1) {
                holder.ivGroupImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isDeleteModel) {
                            isDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            } else if (position == getCount() - 2) {
                holder.ivGroupImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.addMembers();
                    }
                });
            } else {
                holder.ivGroupDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position == 0) {
                            return;
                        }
                        mOnGroupDetailListener.deleteMembers(userInfo);
                    }
                });
            }
        } else {
            if (position == getCount() - 1 || position == getCount() - 2) {
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);
                holder.tvGroupName.setText(userInfo.getName());
                holder.ivGroupDelete.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        private ImageView ivGroupImage;
        private TextView tvGroupName;
        private ImageView ivGroupDelete;
    }

    public interface OnGroupDetailListener {
        void addMembers();

        void deleteMembers(UserInfo user);
    }
}
