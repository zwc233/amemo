package com.example.amemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
        //TODO 设置提醒后的事件
        setUpNotification(context);
        Toast.makeText(context, "您设置的时间到了！",
                Toast.LENGTH_SHORT).show();
        playRing(context);

    }

    public static void setUpNotification(Context c){
        //设置点击以后跳转到的活动NotificationActivity
        Intent intent=new Intent(c,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(c,0,intent,0);
        NotificationManager manager=(NotificationManager)c.getSystemService(NOTIFICATION_SERVICE);

        Notification  notification=new Notification .Builder(c)
                .setContentTitle("这是通知标题")
                .setContentText("这是通知内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)//点击以后可以进入通知具体内容
                .setAutoCancel(true)//点击以后通知自动消失
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