package com.example.amemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.legacy.content.WakefulBroadcastReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO 设置提醒后的事件
        setUpNotification(context);
        Toast.makeText(context, "您设置的时间到了！",
                Toast.LENGTH_SHORT).show();
        playRing(context);

    }

    public static void setUpNotification(Context c){
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
                .setContentTitle("随便")
                .setContentText("随随便便写")
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