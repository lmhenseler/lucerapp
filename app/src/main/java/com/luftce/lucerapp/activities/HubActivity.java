package com.luftce.lucerapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Api;
import com.luftce.lucerapp.models.formaters.ListAdapter;
import com.luftce.lucerapp.models.Precio;
import com.luftce.lucerapp.models.formaters.OrdenaPrecios;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HubActivity extends AppCompatActivity {

    private TextView miTextView;
    private Retrofit retrofit;
    List<Precio> listaPrecios;
    private Api myApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        retrofit = new Retrofit.Builder().baseUrl("https://api.preciodelaluz.org/").
        addConverterFactory(GsonConverterFactory.create()).build();

        myApi = retrofit.create(Api.class);

        getPrecios();

    }

    private void getPrecios(){
        Call<List<Precio>> call = myApi.getPrecios();
        call.enqueue(new Callback<List<Precio>>() {
            @Override
            public void onResponse(Call<List<Precio>> call, Response<List<Precio>> response) {

                init(response);
            }

            @Override
            public void onFailure(Call<List<Precio>> call, Throwable t) {
                Toast.makeText(HubActivity.this, "No ha funcionado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void init(Response<List<Precio>> response){
        listaPrecios = OrdenaPrecios.ordenarPrecios(response.body());

        ListAdapter listAdapter = new ListAdapter(listaPrecios, HubActivity.this);
        RecyclerView recyclerView = findViewById(R.id.listRV);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HubActivity.this));
        recyclerView.setAdapter(listAdapter);
    }

}