package com.prometteur.sathiya.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanNotification;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.utills.AppConstants;

import java.util.ArrayList;

import static com.prometteur.sathiya.utills.AppConstants.getReviewDate;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Myholder> {


    ArrayList<beanNotification> list;
    Context ctx;

    public NotificationAdapter(ArrayList<beanNotification> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
        notifyDataSetChanged();
    }

    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vv = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new Myholder(vv);
    }

    public void onBindViewHolder(@NonNull Myholder holder, final int position) {

        Glide.with(ctx)
                .load(list.get(position).getImgProfileUrl())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_my_profile))
                .into(holder.imgReciverProfile);

        holder.tvMsg.setText(list.get(position).getNotification());
        String strDate=getReviewDate(ctx,list.get(position).getDate());
        if(strDate.equalsIgnoreCase("0 minutes ago")) {
            holder.tvTimeSpan.setText("Just now");
        }else {
            holder.tvTimeSpan.setText(strDate);
        }



        holder.tvMatriID.setText(list.get(position).getMatriId());
        if(list.get(position).getMatriId()!=null && !list.get(position).getMatriId().equalsIgnoreCase("null") && !list.get(position).getMatriId().isEmpty()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThirdHomeActivity.matri_id = list.get(position).getMatriId();
                    //   MemberViewProfile.is_shortlist=singleUser.getIs_shortlisted();
                    ctx.startActivity(new Intent(ctx, ThirdHomeActivity.class));
                }
            });
        }
    }

    public int getItemCount() {
        return list.size();
    }

    class Myholder extends RecyclerView.ViewHolder {

        TextView tvMsg, tvMatriID,tvTimeSpan;
        ImageView imgReciverProfile;

        public Myholder(View itemView) {
            super(itemView);

            imgReciverProfile = itemView.findViewById(R.id.imgReciverProfile);
            tvMsg = itemView.findViewById(R.id.tv_notimsg);
            tvMatriID = itemView.findViewById(R.id.tvMatriID);
            tvTimeSpan = itemView.findViewById(R.id.tvTimeSpan);
        }
    }

    public void clearData() {
        // clear the data
        list.clear();
    }
}
