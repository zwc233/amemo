package com.example.amemo;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

        btn.setOnClickListener(v ->{
            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", CacheHandler.getToken());
                jsonObject.put("name", name);
                jsonObject.put("description", description);
                requestQueue.add(new JsonObjectRequest(
                        Request.Method.POST,
                        UrlUtils.makeHttpUrl(UrlUtils.createGroupUrl),
                        jsonObject,
                        response -> {
                            try {
                                System.out.println(response.getString("msg"));
                                if (response.getString("code").equals("200")) {
                                    Toast.makeText(getContext(),
                                            R.string.create_group_success,
                                            Toast.LENGTH_SHORT).show();
                                    CacheHandler.saveGroup(response);
                                } else if (response.getString("code").equals("400")) {
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
                            Toast.makeText(getContext(),
                                    R.string.no_response,
                                    Toast.LENGTH_SHORT).show();
                            CreateNewGroupDialog.this.dismiss();
                        }
                ));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
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
