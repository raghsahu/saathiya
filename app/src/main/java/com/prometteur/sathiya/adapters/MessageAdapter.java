package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.databinding.ItemChatsBinding;
import com.prometteur.sathiya.databinding.ItemMessageLeftBinding;
import com.prometteur.sathiya.databinding.ItemMessageRightBinding;
import com.prometteur.sathiya.model.Chat_Model;
import com.prometteur.sathiya.utills.OnLoadMoreListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Predicted Address";
    private static final int MESSAGE_SENT_TYPE = 1;
    private static final int MESSAGE_RECEIVED_TYPE = 2;
    Context nContext;
    List<Chat_Model> homeResultList;
    Activity nActivity;
    String myid, fmid,  imgProfile, imgUserProfile;
    public MessageAdapter(Activity nActivity, List<Chat_Model> homeResultList, String myid, String fmid, String imgProfile,String imgUserProfile) {
        this.nContext = nActivity;
        this.nActivity = nActivity;
        this.homeResultList = homeResultList;
        this.myid = myid;
        this.fmid = fmid;
        this.imgProfile = imgProfile;
        this.imgUserProfile = imgUserProfile;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_RECEIVED_TYPE) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_message_left, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == MESSAGE_SENT_TYPE) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_message_right, parent, false);
            return new ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
/*
        if (holder1 instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) holder1;
if(position==0)
{
    holder.leftBinding.tvDate.setVisibility(View.VISIBLE);
}else if(position==4)
{
    holder.leftBinding.tvDate.setVisibility(View.VISIBLE);
    holder.leftBinding.tvDate.setText("24/01/2021");
}else
{
    holder.leftBinding.tvDate.setVisibility(View.GONE);
}

        } else if (holder1 instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder1;
        }
*/


        Chat_Model sentMessageData = homeResultList.get(position);

        switch (holder1.getItemViewType()) {
            case MESSAGE_SENT_TYPE:
                ((ViewHolder) holder1).setSentData(sentMessageData, position);
                break;
            case MESSAGE_RECEIVED_TYPE:
                ((LoadingViewHolder) holder1).setRecieveData(sentMessageData, position);
        }

    }

    @Override
    public int getItemCount() {
        return homeResultList == null ? 0 : homeResultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Chat_Model sentMessageData = homeResultList.get(position);

        if (sentMessageData.getFrom_id().equalsIgnoreCase(myid))
            return MESSAGE_SENT_TYPE;
        else
            return MESSAGE_RECEIVED_TYPE;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
ItemMessageRightBinding rightBinding;
        public ViewHolder(View itemView) {
            super(itemView);
            rightBinding=ItemMessageRightBinding.bind(itemView);
        }

        void setSentData(final Chat_Model data, int position) {
            rightBinding.tvRecieveMessage.setText(data.getMsg());
            rightBinding.tvDateTime.setText(data.getDate());

           /* Glide.with(itemView.getContext())
                    .load(ToImgProfile)
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_my_profile))
                    .into(ivRecieverProfile);


            if (position < sentMessageDataList.size() - 1)
                if (!sentMessageDataList.get(position + 1).getFrom_id().equalsIgnoreCase(matriID)) {
                    ivRecieverProfile.setVisibility(View.INVISIBLE);
                } else {
                    ivRecieverProfile.setVisibility(View.VISIBLE);
                }*/

        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        ItemMessageLeftBinding leftBinding;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            leftBinding=ItemMessageLeftBinding.bind(itemView);
        }

        void setRecieveData(final Chat_Model sentData, int position) {
            leftBinding.tvSentMessage.setText(sentData.getMsg());
            leftBinding.tvDateTime.setText(sentData.getDate());
          /*  Glide.with(itemView.getContext())
                    .load(FromUserProfile)
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_my_profile))
                    .into(ivSenderProfile);
            if (position < sentMessageDataList.size() - 1)
                if (sentMessageDataList.get(position + 1).getFrom_id().equalsIgnoreCase(matriID)) {
                    ivSenderProfile.setVisibility(View.INVISIBLE);
                } else {
                    ivSenderProfile.setVisibility(View.VISIBLE);
                }*/
        }

    }

}
