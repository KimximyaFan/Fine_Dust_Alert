package com.example.fine_dust_alert;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class AirQualityWorker extends Worker {

    private static final String BASE_URL = "http://openAPI.seoul.go.kr:8088/";
    private static final String CHANNEL_ID = "air_quality_alerts";
    private static final String PREFS_NAME = "user_settings";

    public AirQualityWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // SharedPreferences에서 사용자 설정 읽기
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int threshold = prefs.getInt("threshold_pm10", 70);

        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        try {
            // 동기 호출로 API 요청
            Call<AirQualityResponse> call = apiService.getAirQualityData();
            Response<AirQualityResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                Row airQualityRow = response.body().getRows().get(0);
                int currentPm10 = airQualityRow.getPm10();

                // 임계값 초과 시 알림 발송
                if (currentPm10 > threshold) {
                    sendNotification(threshold);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry(); // 실패 시 작업 재시도
        }

        return Result.success(); // 작업 성공
    }

    private void sendNotification(int threshold) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("미세먼지 경고")
                .setContentText("미세먼지 수치가 " + threshold + "을 초과했습니다!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    public static void saveUserSettings(Context context, int interval, int threshold) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notification_interval", interval);
        editor.putInt("threshold_pm10", threshold);
        editor.apply();

        rescheduleWorker(context, interval);
    }

    public static void rescheduleWorker(Context context, int interval) {
        // 기존 작업 취소
        WorkManager.getInstance(context).cancelAllWorkByTag("AirQualityWorker");

        // 새로운 WorkRequest 생성
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                AirQualityWorker.class,
                interval, TimeUnit.MINUTES
        ).addTag("AirQualityWorker").build();

        // WorkManager 실행
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}

