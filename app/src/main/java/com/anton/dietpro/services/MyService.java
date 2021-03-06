package com.anton.dietpro.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import com.anton.dietpro.R;
import com.anton.dietpro.activity.MainActivity;
import com.anton.dietpro.models.Nutrition;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        run();
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void run() {
            try {
                Nutrition nutrition = Nutrition.getNutritionByTime(new Date(System.currentTimeMillis() + (( 60 * 1000)*15)),getApplicationContext());
                if (nutrition != null) {
                    sendNotif(nutrition);
                }
                TimeUnit.MINUTES.sleep(5);
                startService(new Intent(getApplicationContext(),MyService.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotif(Nutrition nutrition) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);
        builder.setTicker(getString(R.string.tikerText));
        builder.setContentTitle(getString(R.string.notificationTitle));//DietPro Notification
        builder.setContentText(String.format(getString(R.string.notificationText),
                nutrition.getName(),
                (new SimpleDateFormat("kk:mm")).format(nutrition.getDatetime()) ));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pIntent);
        builder.setAutoCancel(true);
        builder.setShowWhen(true);
        builder.setSubText(getString(R.string.timeForEat));   //API level 16
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setWhen(System.currentTimeMillis());
        Notification myNotication = builder.build();
        myNotication.defaults = Notification.DEFAULT_SOUND |
                Notification.DEFAULT_VIBRATE;
        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(nutrition.getId(), myNotication);
    }


}
