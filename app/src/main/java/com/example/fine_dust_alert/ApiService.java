package com.example.fine_dust_alert;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("7349677a7168796a37336c48745457/xml/ListAvgOfSeoulAirQualityService/1/5/")
    Call<AirQualityResponse> getAirQualityData();
}
