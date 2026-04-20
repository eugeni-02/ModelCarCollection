package com.example.modelcarcollection.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AnthropicService {

    @Headers({
            "anthropic-version: 2023-06-01",
            "content-type: application/json"
    })
    @POST("v1/messages")
    Call<okhttp3.ResponseBody> identifyCar(
            @retrofit2.http.Header("x-api-key") String apiKey,
            @Body RequestBody body
    );
}