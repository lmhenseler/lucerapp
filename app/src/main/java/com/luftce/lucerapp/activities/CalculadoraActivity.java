package com.luftce.lucerapp.activities;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.BaseOnSliderTouchListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Api;
import com.luftce.lucerapp.models.Edom;
import com.luftce.lucerapp.models.Precio;
import com.luftce.lucerapp.models.formaters.OrdenaPrecios;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalculadoraActivity extends AppCompatActivity {

    private ArrayList<Edom> listaEdom;
    private List<Precio> precios;
    private Edom seleccionado;
    private Button botonSeleccionado;
    private TextView precioCalculo;


    protected void onCreate(Bundle savedinstanceState){
        super.onCreate(savedinstanceState);
        setContentView(R.layout.layout_calculadora);
        this.precios = codigoPrueba();
        precioCalculo = findViewById(R.id.estimacionText);


        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("potencias.json");


            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();

            Type listType = new TypeToken<List<Edom>>() {}.getType();
            listaEdom = gson.fromJson(reader, listType);
            inputStream.close();
            seleccionado = buscarEdom("lavadora");
            botonSeleccionado = findViewById(R.id.lavadoraButton);

            botonSeleccionado.setClickable(false);
            botonSeleccionado.setAlpha(0.7f);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void calcularPrecio(View view){
        TimePicker horaInicio = findViewById(R.id.horaInicioPicker);
        TimePicker horaFinal = findViewById(R.id.horaFInPicker);
        int hIni, hFin, mIni, mFin;
        hIni = horaInicio.getHour(); hFin= horaFinal.getHour(); mIni=horaInicio.getMinute(); mFin = horaFinal.getMinute();

        float total = 0;
        Precio precio;
        if (hFin > hIni) {

            precio = OrdenaPrecios.encontrarPrecio(precios, hIni);
            total += precio.getPrice()/1000 * seleccionado.getPotencia() * (60-mIni)/60;
            for (int i = hIni + 1 ; i<hFin; i++){
                precio = OrdenaPrecios.encontrarPrecio(precios, i);
                total += precio.getPrice()/1000 * seleccionado.getPotencia();
            }
            precio = OrdenaPrecios.encontrarPrecio(precios, hFin);
            total += precio.getPrice()/1000 * seleccionado.getPotencia() * (60-mFin)/60;
            precioCalculo.setText(String.valueOf(total));


        } else if (hIni == hFin && mFin > mIni){
            precio = OrdenaPrecios.encontrarPrecio(precios, hIni);
            total += precio.getPrice()/1000 * seleccionado.getPotencia() * (mFin-mIni)/60;
            precioCalculo.setText(String.valueOf(total));

        }else {
            Toast.makeText(this, "Las horas no son válidas", Toast.LENGTH_SHORT).show();
            precioCalculo.setText("Precio");
        }


    }


    public void seleccionarLavadora(View view){
        seleccionado = buscarEdom("lavadora");
        cambiarSeleccionado((Button) view);

    }

    public void seleccionarLavaPlatos(View view){
        seleccionado = buscarEdom("lavaplatos");
        cambiarSeleccionado((Button) view);
    }

    public void seleccionarVitro(View view){
        seleccionado = buscarEdom("vitro");
        cambiarSeleccionado((Button) view);
    }

    public void seleccionarHorno(View view){
        seleccionado = buscarEdom("horno");
        cambiarSeleccionado((Button) view);
    }

    private Edom buscarEdom(String nombre){
        Edom ret = null;
        for (Edom edom : listaEdom) {
            if (edom.getEdom().equals(nombre)) {
                ret = edom;
            }
        }
        return ret;
    }
    private void cambiarSeleccionado(Button button){
        botonSeleccionado.setClickable(true);
        botonSeleccionado.setAlpha(1);
        botonSeleccionado = button;
        botonSeleccionado.setClickable(false);
        botonSeleccionado.setAlpha(0.7f);
    }

    private List<Precio> codigoPrueba() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.preciodelaluz.org/").
                addConverterFactory(GsonConverterFactory.create()).build();

        Api myApi = retrofit.create(Api.class);

        final List<Precio> ret = new ArrayList<>();
        Call<List<Precio>> call = myApi.getPrecios();
        call.enqueue(new Callback<List<Precio>>() {
            @Override
            public void onResponse(Call<List<Precio>> call, Response<List<Precio>> response) {

                ret.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<Precio>> call, Throwable t) {
                Toast.makeText(CalculadoraActivity.this, "No ha funcionado", Toast.LENGTH_SHORT).show();
            }
        });
        return ret;
    }
}
