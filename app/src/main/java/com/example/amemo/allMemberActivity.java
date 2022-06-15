package com.example.amemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class allMemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utils.setStatusBar(this);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_member);

        RecyclerView recyclerView = findViewById(R.id.allMembersRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<MemberItem> list = new ArrayList<>();
        list.add(new MemberItem("成员A"));
        list.add(new MemberItem("成员B"));
        list.add(new MemberItem("成员C"));
        SeeAllMemberAdapter fruitAdapter = new SeeAllMemberAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
    }
}