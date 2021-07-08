package com.prometteur.sathiya.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanReligion;
import com.prometteur.sathiya.utills.AppConstants;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;


public class ReligionAdapter extends RecyclerView.Adapter<ReligionAdapter.MyViewHolder> {

    public Context context;
    ArrayList<beanReligion> arrReligionList;
    private ArrayList<beanReligion> arrFilter;
    LinearLayout Slidingpage;
    RelativeLayout SlidingDrawer;
  //  ImageView btnMenuClose;
  TextView edtReligion;

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

    public ReligionAdapter(Context context, ArrayList<beanReligion> fields_list, RelativeLayout SlidingDrawer, LinearLayout Slidingpage,
                           TextView edtReligion) {

        this.context = context;
        this.arrReligionList = fields_list;
        this.SlidingDrawer=SlidingDrawer;
        this.Slidingpage=Slidingpage;
        //this.btnMenuClose=btnMenuClose;
        this.edtReligion=edtReligion;
        this.arrFilter = new ArrayList<beanReligion>();
        this.arrFilter.addAll(arrReligionList);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final beanReligion objCountry = arrReligionList.get(position);

        if (objCountry.getName() != null)
        {
            holder.tv_name.setText(objCountry.getName());
        }


        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppConstants.ReligionName=objCountry.getName();
                AppConstants.ReligionId=objCountry.getReligion_id();

                edtReligion.setText(AppConstants.ReligionName);

                SlidingDrawer.setVisibility(View.GONE);
                SlidingDrawer.startAnimation(AppConstants.outToLeftAnimation());

                Slidingpage.setVisibility(View.GONE);
                Slidingpage.startAnimation(AppConstants.outToLeftAnimation());

//                btnMenuClose.setVisibility(View.GONE);
//                btnMenuClose.startAnimation(AppConstants.outToLeftAnimation());

                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.cardView.getWindowToken(), 0);

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrReligionList.size();
    }


    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        arrReligionList.clear();
        if (charText.length() == 0)
        {
            arrReligionList.addAll(arrFilter);
        }
        else
        {
            for (beanReligion wp : arrFilter)
            {
                String ProductName=wp.getName();

                if (ProductName.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    arrReligionList.add(wp);
                }
            }
        }
        if (arrReligionList.size() == 0)
        {
            arrReligionList.addAll(arrFilter);
        }else
        {
            notifyDataSetChanged();
        }

    }

}
