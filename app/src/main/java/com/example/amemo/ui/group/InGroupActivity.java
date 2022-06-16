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
import com.example.amemo.AfterLoginActivity;
import com.example.amemo.CustomBottomDialog;
import com.example.amemo.InviteUserDialog;
import com.example.amemo.MainActivity;
import com.example.amemo.R;
import com.example.amemo.UrlUtils;
import com.example.amemo.SeeAllMemberDialog;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
            CustomBottomDialog customBottomDialog =
                    new CustomBottomDialog(
                            InGroupActivity.this,
                            getIntent().getStringExtra("groupId")
                    );
            customBottomDialog.show();
            overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.no_anim);
        });

        ImageButton btn2 = findViewById(R.id.btnInvite);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteUserDialog customBottomDialog = new InviteUserDialog(
                        InGroupActivity.this,
                        getIntent().getStringExtra("groupId"));
                customBottomDialog.show();
                overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.no_anim);

            }
        });

        ImageButton btn3 = findViewById(R.id.btnSeeMember);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeeAllMemberDialog customBottomDialog = new SeeAllMemberDialog(InGroupActivity.this,
                        intent.getStringExtra("groupId"));
                customBottomDialog.show();
                overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.no_anim);
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recyclerViewInGroup);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<MemoItem> list = new ArrayList<>();
        StringBuilder notFound = new StringBuilder();
        Lock gotResponse = new ReentrantLock();
        CacheHandler.Group group = CacheHandler.getGroup(groupId);
        while (group == null) {
            gotResponse.lock();
            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.groupInfoUrl),
                            response -> {
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    System.out.println(responseObj.getString("msg"));
                                    if (responseObj.getString("code").equals("200")) {
                                        JSONObject info = responseObj.getJSONArray("result")
                                                .getJSONObject(0);
                                        if (info.getString("code").equals("200")) {
                                            CacheHandler.saveGroup(info.getJSONObject("info"));
                                        } else {
                                            Toast.makeText(this,
                                                    "群组 " + groupId + " 可能已经被删除",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (responseObj.getString("code").equals("400")) {
                                        Toast.makeText(this,
                                                R.string.invalid_token,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this,
                                                R.string.request_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(this,
                                            R.string.response_parse_failure,
                                            Toast.LENGTH_SHORT).show();
                                }
                                gotResponse.unlock();
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(this,
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
                                gotResponse.unlock();
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("token", CacheHandler.getToken());
                            params.put("gIds", groupId);
                            return params;
                        }
                    }
            );
            gotResponse.lock();
            group = CacheHandler.getGroup(groupId);
            gotResponse.unlock();
        }
        MemoAdapter fruitAdapter = new MemoAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
    }
}