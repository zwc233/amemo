package com.example.amemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateNewGroupDialog extends Dialog {
    public CreateNewGroupDialog(@NonNull Context context) {
        super(context, R.style.bottom_dialog_bg_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_create_group_layout);
        setWindowTheme();
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        EditText nameText = findViewById(R.id.editTextMemoNoteDate);

        EditText descriptionText = findViewById(R.id.editTextMemoTitle);

        Button btn = findViewById(R.id.btnCreateNewGroup);

        btn.setOnClickListener(v -> {
            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.createGroupUrl),
                            response -> {
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    System.out.println(responseObj.getString("msg"));
                                    if (responseObj.getString("code").equals("200")) {
                                        Toast.makeText(getContext(),
                                                R.string.create_group_success,
                                                Toast.LENGTH_SHORT).show();
                                        String groupId = responseObj.getString("id");
                                        JSONObject groupObj = new JSONObject();
                                        groupObj.put("id", groupId);
                                        groupObj.put("name", nameText.getText().toString());
                                        groupObj.put("description", descriptionText.getText().toString());
                                        groupObj.put("owner", CacheHandler.user.username);
                                        groupObj.put("admins", new JSONArray());
                                        groupObj.put("members", new JSONArray());
                                        groupObj.put("memos", new JSONArray());
                                        CacheHandler.saveGroup(groupObj);
                                        CacheHandler.user.joinedGroups.add(groupId);
                                    } else if (responseObj.getString("code").equals("400")) {
                                        Toast.makeText(getContext(),
                                                R.string.invalid_token,
                                                Toast.LENGTH_SHORT).show();
                                        // TODO: flush caches; route back to main activity
                                    } else {
                                        Toast.makeText(getContext(),
                                                R.string.create_group_failure,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(),
                                            R.string.response_parse_failure,
                                            Toast.LENGTH_SHORT).show();
                                }
                                CreateNewGroupDialog.this.dismiss();
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(getContext(),
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
                                CreateNewGroupDialog.this.dismiss();
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();

                            String name = nameText.getText().toString();
                            String description = descriptionText.getText().toString();

                            params.put("token", CacheHandler.getToken());
                            params.put("name", name);
                            params.put("description", description);

                            return params;
                        }
                    }
            );
        });

    }

    private void setWindowTheme() {
        Window window = this.getWindow();
        // 设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        // 设置弹出动画
        window.setWindowAnimations(R.style.show_dialog_animStyle);
        // 设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
