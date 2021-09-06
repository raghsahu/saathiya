package com.prometteur.sathiya.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;

import java.util.ArrayList;

public class RecycleImagesAdapter extends RecyclerView.Adapter<RecycleImagesAdapter.ViewHolder> {

    Context nContext;
    ArrayList<Uri> imguris;
    ArrayList<String> hobbiesTextArr;
    private final OnItemClickListener clickListener;

    public RecycleImagesAdapter(Context nContext, ArrayList<Uri> imguris,ArrayList<String> hobbiesTextArr, OnItemClickListener clickListener) {
        this.nContext = nContext;
        this.imguris = imguris;
        this.hobbiesTextArr = hobbiesTextArr;
        this.clickListener = clickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(nContext).inflate(R.layout.recycle_images,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(nContext)
                .asBitmap()
                .load(imguris.get(position))
                .into(holder.imgView);
        //holder.imgView.setClipToOutline(true);
        if(hobbiesTextArr.get(position).isEmpty()){
            holder.hobbyName.setVisibility(View.GONE);
        }else{
            holder.hobbyName.setVisibility(View.VISIBLE);
            holder.hobbyName.setText(nContext.getString(R.string.my_hobby)+" - "+hobbiesTextArr.get(position));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imguris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView hobbyName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.imgView);
            hobbyName=itemView.findViewById(R.id.tvDetail);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

}
