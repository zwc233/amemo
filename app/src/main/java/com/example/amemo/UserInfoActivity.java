package com.example.amemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Utils.setStatusBar(this);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        TextView usernameText = findViewById(R.id.ig_name);
        usernameText.setText(CacheHandler.getUser().username);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(UserInfoActivity.this,AfterLoginActivity.class);
        startActivity(intent);
    }
}