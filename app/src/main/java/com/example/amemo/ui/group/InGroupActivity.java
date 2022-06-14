package com.example.amemo.ui.group;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.amemo.CacheHandler;
import com.example.amemo.CustomBottomDialog;
import com.example.amemo.R;
import com.example.amemo.Utils;
import com.example.amemo.ui.memo.MemoAdapter;
import com.example.amemo.ui.memo.MemoItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class InGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_group);
        Utils.setStatusBar(this);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        Intent intent = getIntent();
        String groupId = intent.getStringExtra("groupId");

        ImageButton btn = findViewById(R.id.btnCreateNewMemo);
        btn.setOnClickListener(v -> {
            CustomBottomDialog customBottomDialog = new CustomBottomDialog(InGroupActivity.this);
            customBottomDialog.show();
            overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.no_anim);
        });

        final RecyclerView recyclerView = findViewById(R.id.recyclerViewInGroup);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<MemoItem> list = new ArrayList<>();
        CacheHandler.Group group = CacheHandler.getGroup(groupId);
        if (group != null) {
            for (String memoId : group.memos) {
                CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
                if (memo != null) {
                    list.add(new MemoItem(memo));
                }
            }
        }
        MemoAdapter fruitAdapter = new MemoAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
    }
}