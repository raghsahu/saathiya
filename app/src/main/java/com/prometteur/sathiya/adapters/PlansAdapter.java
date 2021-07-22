package com.prometteur.sathiya.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanCaste;
import com.prometteur.sathiya.beans.beanPackages;
import com.prometteur.sathiya.utills.AppConstants;

import java.util.ArrayList;
import java.util.Locale;


public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.MyViewHolder> {

    public Context context;
    ArrayList<beanPackages> packages;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvCalls;
        public TextView tvPrice;

        public MyViewHolder(View view)
        {
            super(view);
            tvCalls =  view.findViewById(R.id.tvCalls);
            tvPrice =  view.findViewById(R.id.tvPrice);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plans_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public PlansAdapter(Context context, ArrayList<beanPackages> packages) {

        this.context = context;
        this.packages = packages;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final beanPackages pkg = packages.get(position);

        holder.tvCalls.setText(""+pkg.getPlanCallLimit());
        holder.tvPrice.setText(""+pkg.getPlanCallRate());
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

}
