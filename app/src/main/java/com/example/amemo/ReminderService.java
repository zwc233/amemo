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
    private ReminderServiceBinder binder = new ReminderServiceBinder();
    public WSClient wsClient;

    public ReminderService() {
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        URI uri = URI.create(UrlUtils.makeWsUrl(CacheHandler.token));
//        Log.e("ReminderService", "uri is: " + uri.toString());
//        wsClient = new WSClient(uri) {
//            @Override
//            public void onMessage(String message) {
//                try {
//                    JSONObject jsonObject = new JSONObject(message);
//                    String type = jsonObject.getString("type");
//                    if (type.equals("Create Memo")) {
//                        JSONObject memo = jsonObject.getJSONObject("memo");
//                        Log.e("WSClient", "received memo " + memo.getString("title") +
//                                " created by " + memo.getString("creator") + ".");
//                        CacheHandler.saveMemo(jsonObject);
//                        Intent intent = new Intent();
//                        intent.setAction("Create Memo");
//                        intent.putExtra("groupId", memo.getString("group"));
//                        intent.putExtra("memoId", memo.getString("id"));
//                        intent.putExtra("creator", memo.getString("creator"));
//                        sendBroadcast(intent);
//                        // TODO: schedule background reminders
//                    } else if (type.equals("Delete Memo")) {
//                        JSONObject memo = jsonObject.getJSONObject("memo");
//                        Log.e("WSClient", "memo " + memo.getString("title") + "deleted.");
//                        CacheHandler.removeMemo(jsonObject.getString(("id")));
//                        Intent intent = new Intent();
//                        intent.setAction("Delete Memo");
//                        intent.putExtra("groupId", memo.getString("group"));
//                        intent.putExtra("memoId", memo.getString("id"));
//                        intent.putExtra("creator", memo.getString("creator"));
//                        sendBroadcast(intent);
//                        // TODO: schedule background reminders
//                    }
//                } catch (JSONException e) {
//                    Log.e("WSClient", "failed to parse json:", e);
//                }
//            }
//        };
//        return START_STICKY;
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ReminderServiceBinder extends Binder {
        public ReminderService getService() {
            return ReminderService.this;
        }
        // TODO: Return the communication channel to the service.
//        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        long mSeconds = intent.getLongExtra("passedSeconds",0);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long triggerAtTime = SystemClock.elapsedRealtime() + mSeconds;
        Intent i = new Intent(this, AlarmReceiver.class);
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