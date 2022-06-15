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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amemo.CacheHandler;
import com.example.amemo.CustomBottomDialog;
import com.example.amemo.R;
import com.example.amemo.UrlUtils;
import com.example.amemo.Utils;
import com.example.amemo.ui.memo.MemoAdapter;
import com.example.amemo.ui.memo.MemoItem;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        RequestQueue requestQueue = Volley.newRequestQueue(this);

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
        List<String> notFound = new ArrayList<>();
        CacheHandler.Group group = CacheHandler.getGroup(groupId);
        if (group != null) {
            for (String memoId : group.memos) {
                CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
                if (memo == null) {
                    notFound.add(memoId);
                } else {
                    list.add(new MemoItem(memo));
                }
            }
            if (notFound.size() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("token", CacheHandler.getToken());
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < 5; i++) {
                        jsonArray.put("" + i);
                    }
                    jsonObject.put("memoIds", jsonArray);
                    requestQueue.add(
                            new JsonObjectRequest(
                                    Request.Method.POST,
                                    UrlUtils.makeHttpUrl(UrlUtils.groupInfoUrl),
                                    null,
                                    response -> {
                                        System.out.println(response.toString());
                                    },
                                    error -> {
                                        System.out.println(error.getMessage());
                                    }
                            )
                    );
                } catch (JSONException e) {
                    Toast.makeText(this,
                            R.string.request_failed,
                            Toast.LENGTH_SHORT).show();
                    System.out.println(e.getMessage());
                }
            }
        }
        MemoAdapter fruitAdapter = new MemoAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
    }
}