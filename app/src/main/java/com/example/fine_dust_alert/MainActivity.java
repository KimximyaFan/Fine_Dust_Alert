package com.example.fine_dust_alert;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://openAPI.seoul.go.kr:8088/";

    private TextView airQualityStatus;
    private TextView pm10Info;
    private TextView pm25Info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView 연결
        airQualityStatus = findViewById(R.id.air_quality_status);
        pm10Info = findViewById(R.id.pm10_info);
        pm25Info = findViewById(R.id.pm25_info);

        // Retrofit 설정 및 API 호출
        fetchAirQualityData();
    }

    private void fetchAirQualityData() {
        // HTTP 로깅 인터셉터 설정
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient에 로깅 인터셉터 추가
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();

        // ApiService 초기화
        ApiService apiService = retrofit.create(ApiService.class);

        // API 호출
        Call<AirQualityResponse> call = apiService.getAirQualityData();
        call.enqueue(new Callback<AirQualityResponse>() {
            @Override
            public void onResponse(Call<AirQualityResponse> call, Response<AirQualityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    displayError();
                }
            }

            @Override
            public void onFailure(Call<AirQualityResponse> call, Throwable t) {
                displayError();
            }
        });
    }

    private void updateUI(AirQualityResponse data) {
        // 첫 번째 행의 데이터 추출
        Row airQualityRow = data.getRows().get(0);

        airQualityStatus.setText("대기 상태: " + airQualityRow.getGrade());
        pm10Info.setText("미세먼지 (PM10): " + airQualityRow.getPm10() + "㎍/m³");
        pm25Info.setText("초미세먼지 (PM2.5): " + airQualityRow.getPm25() + "㎍/m³");
    }

    private void displayError() {
        airQualityStatus.setText("대기 상태: 데이터 불러오기 실패");
        pm10Info.setText("미세먼지 (PM10): -");
        pm25Info.setText("초미세먼지 (PM2.5): -");
    }
}
