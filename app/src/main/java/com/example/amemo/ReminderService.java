package com.example.amemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import android.os.SystemClock;

public class ReminderService extends Service {

    private final ReminderServiceBinder binder = new ReminderServiceBinder();

    public ReminderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ReminderServiceBinder extends Binder {
        public ReminderService getService() {
            return ReminderService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        long mSeconds = intent.getLongExtra("passedSeconds",0);

        String memoId = intent.getStringExtra("memoId");

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long triggerAtTime = SystemClock.elapsedRealtime() + mSeconds;
        Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra("memoId", memoId);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);

    }
}