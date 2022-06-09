package com.example.amemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO 设置提醒后的事件


        Toast.makeText(context, "您设置的时间到了！",
                Toast.LENGTH_SHORT).show();
        playRing(context);

    }

    private static void playRing(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(context, uri);
        rt.play();
    }


}