package com.example.modelcarcollection.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiService {

    @Headers({
            "content-type: application/json"
    })
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    Call<okhttp3.ResponseBody> identifyCar(
            @Query("key") String apiKey,
            @Body RequestBody body
    );
}