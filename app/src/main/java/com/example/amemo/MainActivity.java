package com.example.amemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    final private Lock gotResponse = new ReentrantLock();
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.setStatusBar(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        EditText usernameInput = this.findViewById(R.id.inputUsr);

        EditText passwordInput = this.findViewById(R.id.inputPwd);

        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }

        //TODO 调用示例
        Utils.startNoteAfter(2000,MainActivity.this);

//        AlarmManager alarmMgr = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
//        Intent intentD = new Intent(MainActivity.this, AlarmReceiver.class);
//        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intentD, 0);
//
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() +
//                        3 * 1000, alarmIntent);

        ImageButton btnLogin = findViewById(R.id.login_btn);
        btnLogin.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setClass(
                    MainActivity.this,
                    AfterLoginActivity.class);
            startActivity(intent);
//            try {
//                gotResponse.lock();
//                btnLogin.setEnabled(false);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("username", usernameInput.getText().toString());
//                jsonObject.put("password", passwordInput.getText().toString());
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                        Request.Method.POST,
//                        UrlUtils.makeHttpRequest(UrlUtils.signInUrl),
//                        jsonObject,
//                        (JSONObject response) -> {
//                            try {
//                                Toast.makeText(this,
//                                        response.getString("msg"),
//                                        Toast.LENGTH_SHORT).show();
//                                String code = response.getString("code");
//                                if (code.equals("200")) {
//                                    String token = response.getString("token");
//                                    savedInstanceState.putString("token", token);
//                                    Intent intent = new Intent();
//                                    intent.setClass(
//                                            MainActivity.this,
//                                            AfterLoginActivity.class);
//                                    startActivity(intent);
//                                }
//                            } catch (JSONException e) {
//                                Toast.makeText(this,
//                                        R.string.response_parse_failure,
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        },
//                        error -> {
//                            Toast.makeText(this,
//                                    error.getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                );
//                requestQueue.add(jsonObjectRequest);
//            } catch (JSONException e) {
//                Toast.makeText(this,
//                        R.string.request_failed,
//                        Toast.LENGTH_SHORT).show();
//            } finally {
//                btnLogin.setEnabled(true);
//                gotResponse.unlock();
//            }
        });

//        String token = savedInstanceState.getString("token");
//        if (token != null) {
//            Toast.makeText(this, R.string.use_saved_token, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this,AfterLoginActivity.class);
//            startActivity(intent);
//        }

        Button btnSet = findViewById(R.id.findPwd);
        calendar = Calendar.getInstance();
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                calendar.setTimeInMillis(System.currentTimeMillis());
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                // TODO Auto-generated method stub
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                // 建立Intent和PendingIntent来调用目标组件
                                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                                // 获取闹钟管理的实例
                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                // 设置闹钟
                                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                // 设置周期闹钟
                                am.setRepeating(AlarmManager.RTC_WAKEUP,
                                        System.currentTimeMillis() + (10 * 1000),
                                        (24 * 60 * 60 * 1000), pendingIntent);
                                String tmpS = "设置闹钟时间为" + format(hourOfDay)
                                        + ":" + format(minute);
                                Toast.makeText(MainActivity.this, tmpS,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, mHour, mMinute, true).show();
            }
        });






        Button btn = findViewById(R.id.registerUser);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,RegisterUserActivity.class);
            startActivity(intent);
        });
    }

    // 格式化字符串7:3-->07:03
    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }
}