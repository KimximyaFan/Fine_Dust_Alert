package com.example.fine_dust_alert;

import androidx.core.app.NotificationCompat;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AirQualityWorker extends Worker {

    public AirQualityWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // TODO: API 호출 로직 추가
        int currentPm10 = 80; // 예시 데이터 (API에서 받아와야 함)

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        int threshold = prefs.getInt("threshold_pm10", 70);

        if (currentPm10 > threshold) {
            sendNotification(currentPm10);
        }

        return Result.success();
    }

    private void sendNotification(int pm10) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "air_quality_alerts")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("미세먼지 경고")
                .setContentText("미세먼지 수치가 " + pm10 + "을 초과했습니다!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
