package com.theappwelt.vhelp.api;

import com.theappwelt.vhelp.model.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ShowProfileInfoAPI {
    
	@FormUrlEncoded
    @POST("androidapi/showprofileinfo")
    Call<Value> show(@Field("idUser") String namaPeminjam);
}
