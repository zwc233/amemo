package com.example.amemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.setStatusBar(this);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        ImageButton btnLogin = findViewById(R.id.login_btn);
        btnLogin.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,AfterLoginActivity.class);
            startActivity(intent);
        });
    }
}