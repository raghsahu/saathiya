package com.prometteur.sathiya.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanLanguage;

import java.util.ArrayList;

import static com.prometteur.sathiya.SplashActivity.strLang;

public abstract class AdapterLanguages extends RecyclerView.Adapter<AdapterLanguages.Myholder> {
    Context ctx;
    ArrayList<beanLanguage> languages;
    public AdapterLanguages(Context ctx, ArrayList<beanLanguage> languages) {
        this.ctx = ctx;
        this.languages = languages;
    }

    public AdapterLanguages.Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View vv = LayoutInflater.from(ctx).inflate(R.layout.item_language, parent, false);
        return new Myholder(vv);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLanguages.Myholder holder, @SuppressLint("RecyclerView") final int position) {

        holder.rdobtn_language.setText(languages.get(position).getLanguage());
        if(languages.get(position).getLanguage().equals(strLang)){
            holder.rdobtn_language.setChecked(true);
        }else {
            holder.rdobtn_language.setChecked(false);
        }
        holder.rdobtn_language.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ItemClick(position, languages.get(position).getLanguage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public boolean isHeader(int position){
        return isProgressPos(position);
    }
    private boolean isProgressPos(int position){
        return position == languages.size();
    }



    class Myholder extends RecyclerView.ViewHolder {
        RadioButton rdobtn_language;
        public Myholder(View itemView) {
            super(itemView);

            rdobtn_language = itemView.findViewById(R.id.rdobtn_language);

        }
    }

    public abstract void ItemClick(int pos, String language);
}
