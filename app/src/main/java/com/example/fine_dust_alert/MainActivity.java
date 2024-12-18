package com.example.fine_dust_alert;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://openAPI.seoul.go.kr:8088/";
    private static final String PREFS_NAME = "user_settings";
    private static final String CHANNEL_ID = "air_quality_alerts";
    private static final int POST_NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView airQualityStatus;
    private TextView pm10Info;
    private TextView pm25Info;
    private EditText notificationInterval;
    private EditText thresholdPm10;
    private Button saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView 연결
        airQualityStatus = findViewById(R.id.air_quality_status);
        pm10Info = findViewById(R.id.pm10_info);
        pm25Info = findViewById(R.id.pm25_info);
        notificationInterval = findViewById(R.id.notification_interval);
        thresholdPm10 = findViewById(R.id.threshold_pm10);
        saveSettingsButton = findViewById(R.id.save_settings_button);

        // 알림 채널 생성
        createNotificationChannel();

        // 권한 확인 및 요청
        checkAndRequestNotificationPermission();

        // 사용자 설정 로드
        loadUserSettings();

        // 설정 저장 버튼 클릭 이벤트
        saveSettingsButton.setOnClickListener(v -> {
            saveUserSettings();
            startBackgroundWorker();
        });

        // 데이터 가져오기 및 화면 업데이트
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

    private void loadUserSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int interval = prefs.getInt("notification_interval", 60);
        int threshold = prefs.getInt("threshold_pm10", 70);

        notificationInterval.setText(String.valueOf(interval));
        thresholdPm10.setText(String.valueOf(threshold));
    }

    private void saveUserSettings() {
        int interval = Integer.parseInt(notificationInterval.getText().toString());
        int threshold = Integer.parseInt(thresholdPm10.getText().toString());

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notification_interval", interval);
        editor.putInt("threshold_pm10", threshold);
        editor.apply();
    }

    private void startBackgroundWorker() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int interval = prefs.getInt("notification_interval", 60);

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                AirQualityWorker.class,
                interval, TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Air Quality Alerts";
            String description = "Notifications for air quality changes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        POST_NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == POST_NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
                // 알림을 보낼 준비 완료
            } else {
                // 권한이 거부된 경우
                // 알림 기능 제한 처리 가능
            }
        }
    }
}
