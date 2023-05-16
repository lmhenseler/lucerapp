package com.luftce.lucerapp.fragments;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.BaseOnSliderTouchListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luftce.lucerapp.R;
import com.luftce.lucerapp.models.Api;
import com.luftce.lucerapp.models.Edom;
import com.luftce.lucerapp.models.Precio;
import com.luftce.lucerapp.models.formaters.OrdenaPrecios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

public class CalculatorFragment extends Fragment {

    private ArrayList<Edom> listaEdom;
    private List<Precio> precios;
    private Precio masCaro;
    private Edom seleccionado;
    private Button botonSeleccionado;
    private TextView precioCalculo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_calculadora, container, false);

        precioCalculo = view.findViewById(R.id.estimacionText);


        AssetManager assetManager = requireContext().getAssets();

        // Get the internal storage directory
        File internalStorageDir = requireContext().getFilesDir();

        // Create a file object for the desired file in the internal storage directory


        try {
            InputStream inputStream = assetManager.open("potencias.json");

            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();

            Type listType = new TypeToken<List<Edom>>() {}.getType();
            listaEdom = gson.fromJson(reader, listType);
            inputStream.close();
            seleccionado = buscarEdom("lavadora");
            botonSeleccionado = view.findViewById(R.id.lavadoraButton);

            botonSeleccionado.setClickable(false);
            botonSeleccionado.setAlpha(0.7f);

            FileInputStream fileInputStream;
            try {
                fileInputStream = requireContext().openFileInput("precios.json");
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuilder jsonString = new StringBuilder();
                String line;
                try {
                    while ((line = reader1.readLine()) != null) {
                        jsonString.append(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Type listType1 = new TypeToken<List<Precio>>() {}.getType();
                precios = gson.fromJson(jsonString.toString(), listType1);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fileInputStream = requireContext().openFileInput("mascaro.json");
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(fileInputStream));
                StringBuilder jsonString = new StringBuilder();
                String line;
                try {
                    while ((line = reader1.readLine()) != null) {
                        jsonString.append(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Type listType1 = new TypeToken<Precio>() {}.getType();
                masCaro = gson.fromJson(jsonString.toString(), listType1);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button calcularButton = view.findViewById(R.id.calcularButton);
        Button lavadoraButton = view.findViewById(R.id.lavadoraButton);
        Button lavaPlatosButton = view.findViewById(R.id.lavaplatosButton);
        Button vitroButton = view.findViewById(R.id.vitroButton);
        Button hornoButton = view.findViewById(R.id.hornoButton);

        calcularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularPrecio();
            }
        });

        lavadoraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarEdom("lavadora", lavadoraButton);
            }
        });

        lavaPlatosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarEdom("lavaplatos", lavaPlatosButton);
            }
        });

        vitroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarEdom("vitro", vitroButton);
            }
        });hornoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarEdom("horno", hornoButton);
            }
        });
    }

    private void calcularPrecio() {
        TimePicker horaInicio = requireView().findViewById(R.id.horaInicioPicker);
        TimePicker horaFinal = requireView().findViewById(R.id.horaFInPicker);
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
            total += precio.getPrice()/1000 * seleccionado.getPotencia() * (mFin)/60;
            precioCalculo.setText(String.valueOf(total));
        } else if (hIni == hFin && mFin > mIni){
            precio = OrdenaPrecios.encontrarPrecio(precios, hIni);
            total += precio.getPrice()/1000 * seleccionado.getPotencia() * (mFin-mIni)/60;
            precioCalculo.setText(String.valueOf(total));
        } else {
            Toast.makeText(requireContext(), "Las horas no son válidas", Toast.LENGTH_SHORT).show();
            precioCalculo.setText("Precio");
        }
    }

    private void seleccionarEdom(String nombre, Button button) {
        seleccionado = buscarEdom(nombre);
        precioCalculo.setText(seleccionado.getEdom());
        cambiarSeleccionado(button);
    }

    private Edom buscarEdom(String nombre) {
        Edom ret = null;
        for (Edom edom : listaEdom) {
            if (edom.getEdom().equals(nombre)) {
                ret = edom;
            }
        }
        return ret;
    }

    private void cambiarSeleccionado(Button button) {
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
                Toast.makeText(requireContext(), "No ha funcionado", Toast.LENGTH_SHORT).show();
            }
        });
        //masCaro = ret.get(1);
        //precioCalculo.setText(masCaro.getPrice().toString());
        return OrdenaPrecios.ordenarPrecios(ret);
    }}