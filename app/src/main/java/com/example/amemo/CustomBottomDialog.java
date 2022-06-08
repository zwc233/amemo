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
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

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

        EditText startTime = findViewById(R.id.textMemoNoteDate);
        startTime.setInputType(InputType.TYPE_NULL);
        final long[] startTimeStamp = {0};
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
                        //做你想做的事情
                        @SuppressLint("DefaultLocale") String str = String.format("%02d:%02d", arg1,arg2);
                        startTime.setText(str);

                        try {
                            startTimeStamp[0] = dateToStamp(arg1, arg2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                dialog.show();
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

    public static long dateToStamp(int hour,int minute) throws ParseException {
        Date date1 = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String s = formatter.format(date1) + String.format(" %d:%d:00", hour,minute);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        assert date != null;
        long ts = date.getTime();
        return ts;
    }
}