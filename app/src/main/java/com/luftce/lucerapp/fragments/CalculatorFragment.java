package com.luftce.lucerapp.fragments;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Color;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luftce.lucerapp.R;
import com.luftce.lucerapp.activities.MainActivity;
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
    private TextView precioCalculo, precioAhorro;
    private float coste, ahorro;
    private String inicioCalculo, finCalculo, usuario;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_calculadora, container, false);

        precioCalculo = view.findViewById(R.id.estimacionText);
        precioAhorro = view.findViewById(R.id.AhorroTextView);

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
            botonSeleccionado.setAlpha(0.5f);

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
                fileInputStream = requireContext().openFileInput("usuario.json");
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
                Type listType1 = new TypeToken<String>() {}.getType();
                usuario = gson.fromJson(jsonString.toString(), listType1);
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
        Button aniadirButton = view.findViewById(R.id.aniadirButton);

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
        aniadirButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ postearEnDatabase(); }
        });

    }

    @SuppressLint("SetTextI18n")
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
            ahorro = (float) (masCaro.getPrice()/1000 * seleccionado.getPotencia() * (60-mIni)/60);
            for (int i = hIni + 1 ; i<hFin; i++){
                precio = OrdenaPrecios.encontrarPrecio(precios, i);
                total += precio.getPrice()/1000 * seleccionado.getPotencia();
                ahorro += masCaro.getPrice()/1000 * seleccionado.getPotencia();
            }
            precio = OrdenaPrecios.encontrarPrecio(precios, hFin);
            total += precio.getPrice()/1000 * seleccionado.getPotencia() * (mFin)/60;
            ahorro += masCaro.getPrice()/1000 * seleccionado.getPotencia() * (mFin)/60;
            ahorro = ahorro-total;

            String calculoString = String.valueOf(total).substring(0,4) + "€";
            precioCalculo.setText(calculoString);
            String ahorroString = String.valueOf(ahorro).substring(0,4) + "€";
            precioAhorro.setText(ahorroString);
            precioAhorro.setTextColor(Color.GREEN);

            coste = total;
            inicioCalculo = String.valueOf(hIni) + ":" + String.valueOf(mIni) ;
            finCalculo = String.valueOf(hFin) + ":" + String.valueOf(mFin) ;


        } else if (hIni == hFin && mFin > mIni){
            precio = OrdenaPrecios.encontrarPrecio(precios, hIni);
            total += precio.getPrice()/1000 * seleccionado.getPotencia() * (mFin-mIni)/60;
            ahorro = (float) (masCaro.getPrice()/1000 * seleccionado.getPotencia() * (mFin-mIni)/60);
            ahorro = ahorro - total;

            String calculoString = String.valueOf(total).substring(0,4) + "€";
            precioCalculo.setText(calculoString);
            String ahorroString = String.valueOf(ahorro).substring(0,4) + "€";
            precioAhorro.setTextColor(Color.GREEN);

            coste = total;
        } else {
            Toast.makeText(requireContext(), "Las horas no son válidas", Toast.LENGTH_SHORT).show();
            precioCalculo.setText("Precio");
            coste = 0;
            ahorro= 0;
        }
    }

    private void seleccionarEdom(String nombre, Button button) {
        seleccionado = buscarEdom(nombre);
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
        botonSeleccionado.setAlpha(0.5f);
    }

    private void postearEnDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference calculationsRef = database.getReference("calculations");



        String calculationId = calculationsRef.child(usuario).push().getKey();
        DatabaseReference calculationNodeRef = calculationsRef.child(usuario).child(calculationId);

        calculationNodeRef.child("startHour").setValue(inicioCalculo);
        calculationNodeRef.child("finishHour").setValue(finCalculo);
        calculationNodeRef.child("edom").setValue(seleccionado.getEdom());
        calculationNodeRef.child("price").setValue(coste);
        calculationNodeRef.child("saving").setValue(ahorro);
        Toast.makeText(getContext(), "Calculo guardado en la base de datos", Toast.LENGTH_SHORT).show();


    }

}