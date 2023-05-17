package com.luftce.lucerapp.models.formaters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Calculation;
import com.luftce.lucerapp.models.Precio;

import java.text.DecimalFormat;
import java.util.List;

public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder> {

    private List<Calculation> calculationList;

    // Constructor
    public CalculationAdapter(List<Calculation> calculationList) {
        this.calculationList = calculationList;
    }

    // Create the ViewHolder
    public static class CalculationViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView horario;
        public TextView ahorro;
        // Add more TextViews or views for other calculation properties

        public CalculationViewHolder(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.userView); // Replace with actual TextView IDs
            horario = itemView.findViewById(R.id.horarioView);
            ahorro = itemView.findViewById(R.id.ahorroView);
            // Initialize other TextViews or views
        }
    }

    // Create the ViewHolder and inflate the layout
    @Override
    public CalculationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_calculation, parent, false);
        return new CalculationViewHolder(view);
    }

    // Bind the data to the ViewHolder
    @Override
    public void onBindViewHolder(CalculationViewHolder holder, int position) {
        Calculation calculation = calculationList.get(position);
        String horarioString = calculation.getEdom() + ": " + calculation.getStartHour() + "-" + calculation.getFinishHour();
        String ahorroString = "Ahorra: " + String.valueOf(calculation.getSaving()).substring(0,4) + "€";
        holder.user.setText(calculation.getUser());
        holder.horario.setText(horarioString);
        holder.ahorro.setText(ahorroString);
        holder.ahorro.setTextColor(Color.GREEN);
        // Bind other calculation properties to the respective TextViews or views
    }

    // Return the number of items in the calculationList
    @Override
    public int getItemCount() {
        return calculationList.size();
    }
}
