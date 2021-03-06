package com.prometteur.sathiya.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.utills.AppConstants;

import org.w3c.dom.Text;


public class GeneralAdapter1 extends RecyclerView.Adapter<GeneralAdapter1.MyViewHolder> {

    public Context context;
    String[] arrGeneralList;
    LinearLayout Slidingpage;
    RelativeLayout SlidingDrawer;
    ImageView btnMenuClose;
    TextView edtGeneral;
   // TextInputLayout textInputNoOfChiled,textInputChiledLivingStatus;
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_name;
        public LinearLayout cardView;

        public MyViewHolder(View view)
        {
            super(view);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            cardView = (LinearLayout) view.findViewById(R.id.cardView);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public GeneralAdapter1(Context context, String[] fields_list, RelativeLayout SlidingDrawer, LinearLayout Slidingpage,
                           ImageView btnMenuClose, TextView edtedtGeneralCode/*, TextInputLayout textInputNoOfChiled, TextInputLayout textInputChiledLivingStatus*/) {

        this.context = context;
        this.arrGeneralList = fields_list;
        this.SlidingDrawer=SlidingDrawer;
        this.Slidingpage=Slidingpage;
        this.btnMenuClose=btnMenuClose;
        this.edtGeneral=edtedtGeneralCode;
      /*  this.textInputNoOfChiled=textInputNoOfChiled;
        this.textInputChiledLivingStatus=textInputChiledLivingStatus;*/

        notifyDataSetChanged();

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {

        holder.tv_name.setText(arrGeneralList[position]);

        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                edtGeneral.setText(arrGeneralList[position]);
               /* if(arrGeneralList[position].equalsIgnoreCase("Never Married"))
                {
                    textInputNoOfChiled.setVisibility(View.GONE);
                    textInputChiledLivingStatus.setVisibility(View.GONE);
                }else
                {
                    textInputNoOfChiled.setVisibility(View.VISIBLE);
                    textInputChiledLivingStatus.setVisibility(View.VISIBLE);
                }*/

                SlidingDrawer.setVisibility(View.GONE);
                SlidingDrawer.startAnimation(AppConstants.outToLeftAnimation());

                Slidingpage.setVisibility(View.GONE);
                Slidingpage.startAnimation(AppConstants.outToLeftAnimation());

                btnMenuClose.setVisibility(View.GONE);
                btnMenuClose.startAnimation(AppConstants.outToLeftAnimation());

                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.cardView.getWindowToken(), 0);

                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrGeneralList.length;
    }



}
