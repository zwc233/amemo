package com.example.amemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.legacy.content.WakefulBroadcastReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String memoId = intent.getStringExtra("memoId");
        System.out.println("Handling " + memoId + "...");
        if (CacheHandler.user.emphasizedMemos.contains(memoId)) {
            System.out.println("(Emphasized)");
            CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
            SharedPreferences sp = context.getSharedPreferences("root_preferences", Context.MODE_PRIVATE);
            int globalLevel = 0;
            boolean note = sp.getBoolean("is_opened_note", true);
            System.out.println("Permit note: " + (note ? "true" : "false"));
            boolean ring = sp.getBoolean("note_method", true);
            System.out.println("Permit ring: " + (ring ? "true" : "false"));
            if (note) {
                if (ring) {
                    globalLevel = 2;
                } else {
                    globalLevel = 1;
                }
            }
            if (globalLevel == 2) {
                Utils.setVolumeNormal(context);
                Utils.setVolume(context, 8);
            } else if (globalLevel == 1) {
                Utils.setVolumeVibrate(context);
            } else {
                Utils.setVolumeMute(context);
            }
            setUpNotification(context, memo.title, memo.content);
            playRing(context);
        } else if (CacheHandler.user.notedMemos.contains(memoId)) {
            System.out.println("(Noted)");
            CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
            setUpNotification(context, memo.title, memo.content);
            playRing(context);
        }
    }

    public static void setUpNotification(Context c, String title, String content){
        NotificationManager manager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(c, AfterLoginActivity.class);
        intent.putExtra("Notification",true);


        String channelId = "whatever"; //根据业务执行
        String channelName = "whatever content"; //这个是channelid 的解释，在安装的时候会展示给用户看
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);

        manager.createNotificationChannel(notificationChannel);

        PendingIntent pendingIntent = PendingIntent.getActivity(c,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;
        notification = new Notification.Builder(c,"whatever") //引用加上channelid
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1,notification);
    }

    private static void playRing(Context context) {
        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (Utils.getVolumePermission(context)){
            System.out.println("This is " + audioManager.getRingerMode());
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
                Uri uri = Uri.parse("android.resource://"+"com.example.amemo"+"/"+R.raw.notification_bell);
                Ringtone rt = RingtoneManager.getRingtone(context, uri);
                rt.play();
            } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                Vibrator vibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
                long[] pattern = {3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000};
                vibrator.vibrate(pattern,-1);
            } else {
                Toast.makeText(context, "您设置的备忘录时间到了", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "请申请对应音量权限", Toast.LENGTH_LONG).show();
        }
    }

}