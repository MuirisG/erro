package com.example.erro.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grapesnberries.curllogger.CurlLoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://mycouncil.net";
    private static ApiClient mInstance;
    private static OkHttpClient client = buildClient();
    Gson gson = new GsonBuilder().setLenient().create();
    private Retrofit retrofit;

    private ApiClient(OkHttpClient client) {
        retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
    }

    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder =
                new OkHttpClient.Builder()
                        .addInterceptor(new CurlLoggerInterceptor())
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(1, TimeUnit.MINUTES)
                        .writeTimeout(1, TimeUnit.MINUTES);
        return builder.build();
    }

    public static synchronized ApiClient getInstance() {
        if (mInstance == null) {
            mInstance = new ApiClient(client);
        }
        return mInstance;
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }
}
