package com.example.amemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Utils.setStatusBar(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        EditText usernameText = findViewById(R.id.editTextInputRegisterUserName);

        EditText passwordText = findViewById(R.id.editTextInputRegisterPassword);

        EditText confirmPswText = findViewById(R.id.editTextInputRegisterTwicePassword);

        Button btn = findViewById(R.id.btnRegister);

        btn.setOnClickListener(v -> {
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            String confirmPsw = confirmPswText.getText().toString();
            if (!password.equals(confirmPsw)) {
                Toast.makeText(this,
                        R.string.inconsistent_password,
                        Toast.LENGTH_SHORT).show();
            } else {
                btn.setEnabled(false);
                requestQueue.add(
                        new StringRequest(
                                Request.Method.POST,
                                UrlUtils.makeHttpUrl(UrlUtils.signUpUrl),
                                response -> {
                                    try {
                                        JSONObject responseObj = new JSONObject(response);
                                        System.out.println(responseObj.getString("msg"));
                                        if (responseObj.getString("code").equals("200")) {
                                            Toast.makeText(this,
                                                    R.string.register_success,
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this,
                                                    R.string.register_failure,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        System.out.println(e.getMessage());
                                        Toast.makeText(this,
                                                R.string.response_parse_failure,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    btn.setEnabled(true);
                                },
                                error -> {
                                    System.out.println(error.getMessage());
                                    Toast.makeText(this,
                                            R.string.no_response,
                                            Toast.LENGTH_SHORT).show();
                                    btn.setEnabled(true);
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("username", username);
                                params.put("password", password);
                                return params;
                            }
                        }
                );
            }
        });
    }
}