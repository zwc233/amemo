package com.example.amemo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Utils extends AppCompatActivity{
    protected static boolean useThemeStatusBarColor = false;
    protected static boolean useStatusBarColor = true;
    public static void setStatusBar(AppCompatActivity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (useThemeStatusBarColor) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colortheme));
        } else {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (useStatusBarColor) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    // 获取音量权限，在修改提醒级别之前调用
    public static boolean getVolumePermission(Context c) {
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent);
            return false;
        }else{
            return true;
        }
    }

    // 设置静音
    public static void setVolumeMute(Context c){
        AudioManager audioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        if (getVolumePermission(c)){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }else {
            Toast.makeText(c,"请打开对应权限",Toast.LENGTH_LONG).show();
        }

    }

    // 设置振动
    public static void setVolumeVibrate(Context c){
        AudioManager audioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        if (getVolumePermission(c)){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }else {
            Toast.makeText(c,"请打开对应权限",Toast.LENGTH_LONG).show();
        }
    }

    // 设置正常
    public static void setVolumeNormal(Context c){
        AudioManager audioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        if (getVolumePermission(c)){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }else {
            Toast.makeText(c,"请打开对应权限",Toast.LENGTH_LONG).show();
        }
    }

    // 设置音量
    public static void setVolume(Context c, int volume){
        AudioManager audioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        if (getVolumePermission(c)){
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,volume,AudioManager.FLAG_SHOW_UI);
        }else {
            Toast.makeText(c,"请打开对应权限",Toast.LENGTH_LONG).show();
        }
    }

    public static void startNoteAfter(long mSeconds, String memoId, Context c) {
        Intent intentS = new Intent(c, ReminderService.class);
        intentS.putExtra("passedSeconds",mSeconds);
        intentS.putExtra("memoId", memoId);
        c.startService(intentS);
    }
}
