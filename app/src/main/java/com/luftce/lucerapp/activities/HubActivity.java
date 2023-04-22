package com.luftce.lucerapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Api;
import com.luftce.lucerapp.models.Precio;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HubActivity extends AppCompatActivity {

    private TextView miTextView;
    private Retrofit retrofit;

    private Api myApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        retrofit = new Retrofit.Builder().baseUrl("https://api.preciodelaluz.org/").
        addConverterFactory(GsonConverterFactory.create()).build();

        miTextView = (TextView) findViewById(R.id.textPrueba);

        myApi = retrofit.create(Api.class);

        getPrecios();

    }

    private void getPrecios(){
        Call<List<Precio>> call = myApi.getPrecios();
        call.enqueue(new Callback<List<Precio>>() {
            @Override
            public void onResponse(Call<List<Precio>> call, Response<List<Precio>> response) {
                List<Precio> listaPrecios = response.body();
                String text = "";
                for(Precio p : listaPrecios){
                    text += p.getDate()+p.getHour()+p.getMarket()+p.getPrice().toString() + "\n";
                }
                miTextView.setText(text);
                Toast.makeText(HubActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Precio>> call, Throwable t) {
                Toast.makeText(HubActivity.this, "No ha funcionado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}