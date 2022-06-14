package com.example.amemo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CustomBottomDialog extends Dialog {

    public CustomBottomDialog(@NonNull Context context) {
        super(context, R.style.bottom_dialog_bg_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AtomicLong timestamp = new AtomicLong();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_create_memo_layout);
        setWindowTheme();
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        List<CacheHandler.Group> groups = new ArrayList<>(CacheHandler.groups.values());
        SelectGroupAdapter adapter = new SelectGroupAdapter(getContext(), groups);
        Spinner groupSelection = findViewById(R.id.GroupSelection);
        groupSelection.setAdapter(adapter);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        EditText startTime = findViewById(R.id.editTextMemoNoteDate);
        startTime.setInputType(InputType.TYPE_NULL);
        startTime.setOnClickListener(v -> {
            new CardDatePickerDialog.Builder(v.getContext())
                    .setTitle("SET MAX DATE")
                    .setOnChoose("确定", aLong -> {
                        //aLong  = millisecond
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = sdf.format(new Date(aLong));
                        timestamp.set(aLong);
                        startTime.setText(time);
                        return null;
                    }).build().show();
        });

        Button btn = findViewById(R.id.btnSubmitMemo);
        btn.setOnClickListener(v ->{
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", savedInstanceState.getString("token"));

            } catch (JSONException e) {
                Toast.makeText(getContext(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            }
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

class SelectGroupAdapter extends BaseAdapter {
    final private Context context;
    final private List<CacheHandler.Group> groups;

    public SelectGroupAdapter(Context context, List<CacheHandler.Group> groups) {
        this.context = context;
        this.groups = new ArrayList<>(groups);
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public CacheHandler.Group getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.select_group_item, null);
        TextView groupNameText = convertView.findViewById(R.id.select_group_name);
        TextView groupIdText = convertView.findViewById(R.id.select_group_id);
        groupNameText.setText(getItem(position).name);
        groupIdText.setText(getItem(position).id);
        return convertView;
    }
}