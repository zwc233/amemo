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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amemo.ui.group.GroupAdapter;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeeAllMemberDialog extends Dialog {
    public SeeAllMemberDialog(@NonNull Context context) {
        super(context, R.style.bottom_dialog_bg_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_see_members_layout);
        setWindowTheme();
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        RecyclerView recyclerView = findViewById(R.id.allMembersRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        List<MemberItem> list = new ArrayList<>();
        list.add(new MemberItem("成员A"));
        list.add(new MemberItem("成员B"));
        list.add(new MemberItem("成员C"));
        SeeAllMemberAdapter fruitAdapter = new SeeAllMemberAdapter(list);
        recyclerView.setAdapter(fruitAdapter);

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
