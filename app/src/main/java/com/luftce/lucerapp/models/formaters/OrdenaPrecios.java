package com.luftce.lucerapp.models.formaters;

import com.luftce.lucerapp.models.Precio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrdenaPrecios {
    public static List<Precio> ordenarPrecios(List<Precio> list){

        Collections.sort(list, new Comparator<Precio>() {
            @Override
            public int compare(Precio p1, Precio p2) {
                return Integer.compare(Integer.parseInt(p1.getHour().substring(0,2)), Integer.parseInt(p2.getHour().substring(0,2)));
            }
        });

        return list;
    }
}
