package com.luftce.lucerapp.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    @GET("/v1/prices/cheapests?zone=PCB&n=24")
    Call<List<Precio>> getPrecios();

}
