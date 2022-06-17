package com.example.amemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InviteUserDialog extends Dialog {

    public String groupId;

    public InviteUserDialog(@NonNull Context context, String groupId) {
        super(context, R.style.bottom_dialog_bg_style);
        this.groupId = groupId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_invite_user_layout);
        setWindowTheme();
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        EditText inviteeNameText = findViewById(R.id.editTextInputUserID);

        Button btn = findViewById(R.id.btnInviteUser);
        btn.setOnClickListener(v ->{
            String inviteeName = inviteeNameText.getText().toString();
            if (inviteeName.equals(CacheHandler.getUser().username)) {
                Toast.makeText(getContext(),
                        R.string.cannot_invite_yourself,
                        Toast.LENGTH_SHORT).show();
            }
            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.inviteUrl),
                            response -> {
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    System.out.println(responseObj.getString("msg"));
                                    if (responseObj.getString("code").equals("200")) {
                                        Toast.makeText(getContext(),
                                                R.string.invite_user_success,
                                                Toast.LENGTH_SHORT).show();
                                        CacheHandler.getGroup(groupId).members.add(inviteeName);
                                        // TODO: refresh recycler view adapter
                                    } else if (responseObj.getString("code").equals("400")) {
                                        Toast.makeText(getContext(),
                                                R.string.only_owner_can_invite,
                                                Toast.LENGTH_SHORT).show();
                                    } else if (responseObj.getString("code").equals("402")) {
                                        Toast.makeText(getContext(),
                                                R.string.cannot_invite_yourself,
                                                Toast.LENGTH_SHORT).show();
                                    } else if (responseObj.getString("code").equals("403")) {
                                        Toast.makeText(getContext(),
                                                R.string.user_already_in_group,
                                                Toast.LENGTH_SHORT).show();
                                    } else if (responseObj.getString("msg").startsWith("User not found.")) {
                                        Toast.makeText(getContext(),
                                                R.string.user_does_not_exist,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(),
                                                R.string.invite_user_failure,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(getContext(),
                                            R.string.response_parse_failure,
                                            Toast.LENGTH_SHORT).show();
                                }
                                InviteUserDialog.this.dismiss();
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(getContext(),
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
                                InviteUserDialog.this.dismiss();
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("token", CacheHandler.getToken());
                            params.put("inviteeName", inviteeName);
                            params.put("groupId", groupId);
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
