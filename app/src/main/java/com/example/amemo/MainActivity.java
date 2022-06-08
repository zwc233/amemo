package com.example.amemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    private Lock gotResponse = new ReentrantLock();

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

        ImageButton btnLogin = findViewById(R.id.login_btn);
        btnLogin.setOnClickListener(v -> {
            try {
                gotResponse.lock();
                btnLogin.setEnabled(false);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", usernameInput.getText().toString());
                jsonObject.put("password", passwordInput.getText().toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        UrlUtils.makeHttpRequest(UrlUtils.signInUrl),
                        jsonObject,
                        (JSONObject response) -> {
                            try {
                                Toast.makeText(this,
                                        response.getString("msg"),
                                        Toast.LENGTH_SHORT).show();
                                String code = response.getString("code");
                                if (code.equals("200")) {
                                    String token = response.getString("token");
                                    savedInstanceState.putString("token", token);
                                    Intent intent = new Intent();
                                    intent.setClass(
                                            MainActivity.this,
                                            AfterLoginActivity.class);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(this,
                                        R.string.response_parse_failure,
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            ;
                        }
                );
            } catch (JSONException e) {
                Toast.makeText(this,
                        R.string.request_failed,
                        Toast.LENGTH_SHORT).show();
            } finally {
                btnLogin.setEnabled(true);
                gotResponse.unlock();
            }
        });

        String token = savedInstanceState.getString("token");
        if (token != null) {
            Toast.makeText(this, R.string.use_saved_token, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,AfterLoginActivity.class);
            startActivity(intent);
        }

        Button btn = findViewById(R.id.registerUser);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,RegisterUserActivity.class);
            startActivity(intent);
        });
    }
}