package com.example.android.lechat.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.lechat.R;
import com.example.android.lechat.model.bean.InvationInfo;
import com.example.android.lechat.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51477 on 2017/8/23.
 */

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteViewHolder> {
    private List<InvationInfo> mInvationInfoList = new ArrayList<>();
    private Context mContext;

    private InviteOnClickListener mInviteOnClickListener;

    public InviteAdapter(Context mContext, InviteOnClickListener inviteOnClickListener) {
        this.mContext = mContext;
        this.mInviteOnClickListener = inviteOnClickListener;
    }


    public interface InviteOnClickListener {
        void AgreeInvite(InvationInfo invationInfo);

        void RefuseInvite(InvationInfo invationInfo);

        void AgreeApplication(InvationInfo invationInfo);

        void RefuseApplication(InvationInfo invationInfo);

    }

    public void setInviteData(List<InvationInfo> invationInfoList) {
        if (invationInfoList != null && invationInfoList.size() >= 0) {
            mInvationInfoList.clear();
            mInvationInfoList.addAll(invationInfoList);
            notifyDataSetChanged();
        }
    }

    @Override
    public InviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.item_invite_list;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new InviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InviteViewHolder holder, int position) {
        final InvationInfo invationInfo = mInvationInfoList.get(position);

        if (invationInfo.getUser() != null) {//好友申请
            holder.btnInviteAgree.setVisibility(View.INVISIBLE);
            holder.btnInviteRefuse.setVisibility(View.INVISIBLE);
            holder.tbInviteName.setText(invationInfo.getUser().getName());
            if (invationInfo.getStatus() == InvationInfo.InvitationStatus.NEW_INVITE) {
                if (invationInfo.getReason() != null) {
                    holder.tvInviteReason.setText(invationInfo.getReason());
                } else {
                    holder.tvInviteReason.setText("申请你为好友");
                }
                holder.btnInviteAgree.setVisibility(View.VISIBLE);
                holder.btnInviteRefuse.setVisibility(View.VISIBLE);
            } else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT) {
                holder.tvInviteReason.setText("你接受了对方的好友申请");
            } else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) {
                if (invationInfo.getReason() != null) {
                    holder.tvInviteReason.setText(invationInfo.getReason());
                } else {
                    holder.tvInviteReason.setText("对方接受了你的好友邀请");
                }
            }
        } else if (invationInfo.getGroup() != null) {//群申请
            holder.tbInviteName.setText(invationInfo.getGroup().getGroupId());
            holder.iconInvite.setImageResource(R.drawable.ic_advanced_team);
            holder.btnInviteAgree.setVisibility(View.INVISIBLE);
            holder.btnInviteRefuse.setVisibility(View.INVISIBLE);
            // 显示原因
            switch (invationInfo.getStatus()) {
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.tbInviteName.setText(invationInfo.getUser().getName());
                    holder.tvInviteReason.setText("您的群申请请已经被接受");
                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    holder.tvInviteReason.setText("您的好友将您拉入该群");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.tvInviteReason.setText("对方拒绝您的群申请");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.tvInviteReason.setText("对方拒绝您的群邀请");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    holder.btnInviteAgree.setVisibility(View.VISIBLE);
                    holder.btnInviteRefuse.setVisibility(View.VISIBLE);

                    // 接受邀请

                    // 拒绝邀请

                    holder.tvInviteReason.setText("您收到了群邀请");
                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    holder.btnInviteAgree.setVisibility(View.VISIBLE);
                    holder.btnInviteRefuse.setVisibility(View.VISIBLE);
                    holder.btnInviteAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mInviteOnClickListener.AgreeApplication(invationInfo);
                        }
                    });
                    holder.btnInviteRefuse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mInviteOnClickListener.RefuseApplication(invationInfo);
                        }
                    });

                    holder.tvInviteReason.setText("您收到了群申请");
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.tvInviteReason.setText("你接受了群邀请");
                    break;

                // 您批准了群申请
                case GROUP_ACCEPT_APPLICATION:
                    holder.tvInviteReason.setText("您批准了群申请");
                    break;

                // 您拒绝了群邀请
                case GROUP_REJECT_INVITE:
                    holder.tvInviteReason.setText("您拒绝了群邀请");
                    break;

                // 您拒绝了群申请
                case GROUP_REJECT_APPLICATION:
                    holder.tvInviteReason.setText("您拒绝了群申请");
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mInvationInfoList == null ? 0 : mInvationInfoList.size();
    }

    class InviteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tbInviteName;
        private TextView tvInviteReason;
        private Button btnInviteAgree;
        private Button btnInviteRefuse;
        private ImageView iconInvite;

        public InviteViewHolder(View itemView) {
            super(itemView);
            tbInviteName = (TextView) itemView.findViewById(R.id.tb_invite_name);
            tvInviteReason = (TextView) itemView.findViewById(R.id.tv_invite_reason);
            btnInviteAgree = (Button) itemView.findViewById(R.id.btn_invite_agree);
            btnInviteRefuse = (Button) itemView.findViewById(R.id.btn_invite_refuse);
            iconInvite = (ImageView) itemView.findViewById(R.id.icon_invite);
            btnInviteAgree.setOnClickListener(this);
            btnInviteRefuse.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == btnInviteAgree) {
                mInviteOnClickListener.AgreeInvite(mInvationInfoList.get(getAdapterPosition()));
            } else if (view == btnInviteRefuse) {
                mInviteOnClickListener.RefuseInvite(mInvationInfoList.get(getAdapterPosition()));
            }
        }
    }
}
