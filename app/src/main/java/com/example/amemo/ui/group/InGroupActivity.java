package com.example.amemo.ui.group;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

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

        ImageButton btn = findViewById(R.id.btnCreateNewMemo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog customBottomDialog = new CustomBottomDialog(InGroupActivity.this);
                customBottomDialog.show();
                overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.no_anim);

            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recyclerViewInGroup);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<MemoItem> list = new ArrayList<>();
        list.add(new MemoItem("abc"));
        list.add(new MemoItem("bcd\nDDD\nFFF\nJJJ\nKKK\nDDD\nWWW"));
        MemoAdapter fruitAdapter = new MemoAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
    }
}