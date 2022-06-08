package com.example.amemo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomBottomDialog extends Dialog {

    public CustomBottomDialog(@NonNull Context context) {
        super(context, R.style.bottom_dialog_bg_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_create_memo_layout);
        setWindowTheme();
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        EditText startTime = findViewById(R.id.editTextMemoNoteDate);
        startTime.setInputType(InputType.TYPE_NULL);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("aLong");
                new CardDatePickerDialog.Builder(v.getContext())
                        .setTitle("SET MAX DATE")
                        .setOnChoose("确定", aLong -> {
                            //aLong  = millisecond
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String time = sdf.format(new Date(aLong));
                            startTime.setText(time);
                            return null;
                        }).build().show();
            }
        });

        Button btn = findViewById(R.id.btnSubmitMemo);
        btn.setOnClickListener(v ->{
            //TODO 填写上传Memo代码
            CustomBottomDialog.this.dismiss();
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