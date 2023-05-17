package com.luftce.lucerapp.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Calculation;
import com.luftce.lucerapp.models.Precio;
import com.luftce.lucerapp.models.formaters.CalculationAdapter;

import java.util.ArrayList;
import java.util.List;


public class SocialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        DatabaseReference calculationsRef = FirebaseDatabase.getInstance().getReference("calculations");

        Query query = calculationsRef.orderByChild("saving").limitToLast(5);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Calculation> calculationList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot calculationSnapshot : userSnapshot.getChildren()) {
                        Calculation calculation = calculationSnapshot.getValue(Calculation.class);
                        calculation.setUser(userSnapshot.getKey());
                        calculationList.add(calculation);
                    }
                }



                // Process the calculationList with the 5 calculations with the highest saving value

                init(calculationList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });


            return inflater.inflate(R.layout.fragment_social, container, false);
    }
    public void init(List< Calculation > calculationsList){
        CalculationAdapter calculationAdapter = new CalculationAdapter(calculationsList);
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(calculationAdapter);
    }
}