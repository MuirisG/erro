package com.example.erro.API;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/api/apitest")
    Call<GetMessageResponse> getMessage(
            @Field("code") String code,
            @Field("username") String username,
            @Field("password") String password,
            @Field("gmail") String gmail);
}
