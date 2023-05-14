package com.luftce.lucerapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Api;
import com.luftce.lucerapp.models.Precio;
import com.luftce.lucerapp.models.formaters.ListAdapter;
import com.luftce.lucerapp.models.formaters.OrdenaPrecios;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LightFragment extends Fragment {

    private TextView miTextView;

    private Retrofit retrofit;
    List<Precio> listaPrecios;
    private Api myApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light, container, false);

        retrofit = new Retrofit.Builder().baseUrl("https://api.preciodelaluz.org/").
                addConverterFactory(GsonConverterFactory.create()).build();

        myApi = retrofit.create(Api.class);

        getPrecios(view);

        return view;
    }

    private void getPrecios(final View view){
        Call<List<Precio>> call = myApi.getPrecios();
        call.enqueue(new Callback<List<Precio>>() {
            @Override
            public void onResponse(Call<List<Precio>> call, Response<List<Precio>> response) {

                init(response, view);
            }

            @Override
            public void onFailure(Call<List<Precio>> call, Throwable t) {
                Toast.makeText(getActivity(), "No ha funcionado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void init(Response<List<Precio>> response, View view){
        listaPrecios = OrdenaPrecios.ordenarPrecios(response.body());

        ListAdapter listAdapter = new ListAdapter(listaPrecios, getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.listRV);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(listAdapter);
    }
}