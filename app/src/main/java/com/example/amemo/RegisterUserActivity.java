package com.example.amemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RegisterUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Utils.setStatusBar(this);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        Button btn = findViewById(R.id.btnRegister);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(RegisterUserActivity.this,AfterLoginActivity.class);
            startActivity(intent);
        });
    }
}