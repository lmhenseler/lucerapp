package com.luftce.lucerapp.models.formaters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Precio;

import java.text.DecimalFormat;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Precio> mData;
    private LayoutInflater mInflater;
    private Context context;

    public ListAdapter(List<Precio> mData, Context context) {

        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = mData;
    }

    public int getItemCount(){
        return mData.size();
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.layout_precio, null);
        return new ListAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Precio> items){mData = items;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        View rectangleImage;
        TextView hour, price;

        ViewHolder(View itemView){
            super(itemView);
            rectangleImage=itemView.findViewById(R.id.rectangleView);
            hour = itemView.findViewById(R.id.hourView);
            price = itemView.findViewById(R.id.priceView);
        }

        void bindData(final Precio item){

            Double p = item.getPrice()/1000;

            if (p<0.099){
                rectangleImage.setBackgroundColor(Color.GREEN);
            }
            else if(p<0.1215){
                rectangleImage.setBackgroundColor(Color.YELLOW);
            }
            else{
                rectangleImage.setBackgroundColor(Color.RED);

            }
            DecimalFormat df = new DecimalFormat("#.#####");
            price.setText(df.format(p) + "€/Kwh");
            hour.setText(item.getHour());
        }
    }


}
