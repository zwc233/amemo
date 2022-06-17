package com.example.amemo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.setStatusBar(this);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        Utils.getVolumePermission(getApplicationContext());

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        EditText usernameInput = this.findViewById(R.id.inputUsr);

        EditText passwordInput = this.findViewById(R.id.inputPwd);

        ImageButton btnLogin = findViewById(R.id.login_btn);

        Button btnRegister = findViewById(R.id.registerUser);

        Button btnFuncIntro = findViewById(R.id.functionIntro);

        Button btnPsw = findViewById(R.id.findPwd);

        CacheHandler.context = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (CacheHandler.getToken() != null) {
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);

            System.out.println("Using saved token: " + CacheHandler.getToken());

            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.personalInfoUrl),
                            response -> {
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    System.out.println(responseObj.getString("msg"));
                                    if (responseObj.getString("code").equals("200")) {
                                        JSONObject userObj = responseObj.getJSONObject("info");
                                        CacheHandler.setUser(userObj);
                                        Intent intent = new Intent(MainActivity.this, AfterLoginActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                                        startService(new Intent(this, SubscribeService.class));
                                    } else {
                                        Toast.makeText(this,
                                                R.string.invalid_token,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(this,
                                            R.string.request_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                                btnLogin.setEnabled(true);
                                btnRegister.setEnabled(true);
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(this,
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
                                btnLogin.setEnabled(true);
                                btnRegister.setEnabled(true);
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("token", CacheHandler.getToken());
                            return params;
                        }
                    }
            );
        }

        btnLogin.setOnClickListener(v -> {
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.signInUrl),
                            response -> {
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    System.out.println(responseObj.getString("msg"));
                                    String code = responseObj.getString("code");
                                    if (code.equals("200")) {
                                        String token = responseObj.getString("token");
                                        CacheHandler.setToken(token);

                                        JSONObject userObj = responseObj.getJSONObject("info");
                                        System.out.println(response);
                                        CacheHandler.setUser(userObj);

                                        startService(new Intent(this, SubscribeService.class));

                                        Intent intent = new Intent();
                                        intent.setClass(
                                                MainActivity.this,
                                                AfterLoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(this,
                                                R.string.incorrect_username_or_password,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(this,
                                            R.string.response_parse_failure,
                                            Toast.LENGTH_SHORT).show();
                                }
                                btnLogin.setEnabled(true);
                                btnRegister.setEnabled(true);
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(this,
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
                                btnLogin.setEnabled(true);
                                btnRegister.setEnabled(true);
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", usernameInput.getText().toString());
                            params.put("password", passwordInput.getText().toString());
                            return params;
                        }
                    }
            );
        });

        btnFuncIntro.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, FunctionIntro.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        });
    }
}