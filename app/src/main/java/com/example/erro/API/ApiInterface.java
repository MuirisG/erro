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
            @Field("zone") String zone,
            @Field("directorate") String directorate,
            @Field("surface") String surface,
            @Field("area") String area,
            @Field("locationX") String locationX,
            @Field("locationY") String locationY,
            @Field("gmail") String gmail);
}
