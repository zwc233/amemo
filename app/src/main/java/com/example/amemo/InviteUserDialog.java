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

import androidx.annotation.NonNull;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InviteUserDialog extends Dialog {

    public InviteUserDialog(@NonNull Context context) {
        super(context, R.style.bottom_dialog_bg_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_invite_user_layout);
        setWindowTheme();
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Button btn = findViewById(R.id.btnInviteUser);
        btn.setOnClickListener(v ->{
            //TODO 填写邀请用户代码
            InviteUserDialog.this.dismiss();
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
