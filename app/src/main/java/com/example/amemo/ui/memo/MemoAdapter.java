package com.example.amemo.ui.memo;

import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amemo.CacheHandler;
import com.example.amemo.R;
import com.example.amemo.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder>{
    private List<MemoItem> memoList;
    ViewGroup parentIn;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView memoTitle;
        TextView memoContent;
        TextView memoTime;
        LinearLayout linearLayout;
        ImageView remindLevelImage;

        public ViewHolder(@NonNull View view) {
            super(view);
            memoTitle = view.findViewById(R.id.memoTitleText);
            memoContent = view.findViewById(R.id.memoContentText);
            memoTime = view.findViewById(R.id.memoTimeText);
            linearLayout = view.findViewById(R.id.memo_linear_layout);
            remindLevelImage = view.findViewById(R.id.remindLevelImage);
        }
    }

    public MemoAdapter(List<MemoItem> memoList) {
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentIn = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    Lock gotResponse = new ReentrantLock();

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemoItem memo = memoList.get(position);
        holder.memoTitle.setText(memo.memo.title);
        holder.memoTitle.measure(0,0);
        holder.memoContent.setText(memo.memo.content);
        holder.memoContent.measure(0, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date(memo.memo.when));
        holder.memoTime.setText(time);
        holder.memoTime.measure(0, 0);
        if (memo.remindLevel < 0){
            holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_off_24);
        } else if (memo.remindLevel == 0){
            holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_24_gray);
        } else {
            holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notification_important_24);
        }
        final float scale = parentIn.getContext().getResources().getDisplayMetrics().density;
        holder.linearLayout.getLayoutParams().height =
                (int) (holder.memoTitle.getMeasuredHeight() +
                holder.memoContent.getMeasuredHeight() +
                holder.memoTime.getMeasuredHeight() + 100);

        RequestQueue requestQueue = Volley.newRequestQueue(parentIn.getContext());

        holder.itemView.setOnClickListener(v -> {
            System.out.println(position);
            notifyDataSetChanged();
            gotResponse.lock();
            memo.remindLevel += 1;
            if (memo.remindLevel > 1) {
                memo.remindLevel = -1;
            }
            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.noteMemoUrl),
                            response -> {
                               try {
                                   JSONObject responseObj = new JSONObject(response);
                                   System.out.println(responseObj.getString("msg"));
                                   if (responseObj.getString("code").equals("200")) {
                                       if (memo.remindLevel < 0) {
                                           Toast.makeText(parentIn.getContext(),
                                                   R.string.unnote_success,
                                                   Toast.LENGTH_SHORT).show();
                                           CacheHandler.User user = CacheHandler.getUser();
                                           user.emphasizedMemos.remove(memo.memo.id);
                                           user.notedMemos.remove(memo.memo.id);
                                       } else if (memo.remindLevel == 0) {
                                           Toast.makeText(parentIn.getContext(),
                                                   R.string.note_success,
                                                   Toast.LENGTH_SHORT).show();
                                           CacheHandler.User user = CacheHandler.getUser();
                                           user.emphasizedMemos.remove(memo.memo.id);
                                           user.notedMemos.add(memo.memo.id);
                                       } else {
                                           Toast.makeText(parentIn.getContext(),
                                                   R.string.emphasize_success,
                                                   Toast.LENGTH_SHORT).show();
                                           CacheHandler.User user = CacheHandler.getUser();
                                           user.emphasizedMemos.add(memo.memo.id);
                                           user.notedMemos.remove(memo.memo.id);
                                       }
                                   } else {
                                       Toast.makeText(parentIn.getContext(),
                                               R.string.follow_failed,
                                               Toast.LENGTH_SHORT).show();
                                       memo.remindLevel--;
                                       if (memo.remindLevel < -1) {
                                           memo.remindLevel = 1;
                                       }
                                   }
                               } catch (JSONException e) {
                                   System.out.println(e.getMessage());
                                   Toast.makeText(parentIn.getContext(),
                                           R.string.response_parse_failure,
                                           Toast.LENGTH_SHORT).show();
                                   memo.remindLevel--;
                                   if (memo.remindLevel < -1) {
                                       memo.remindLevel = 1;
                                   }
                               }
                               gotResponse.unlock();
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(parentIn.getContext(),
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
                                memo.remindLevel--;
                                if (memo.remindLevel < -1) {
                                    memo.remindLevel = 1;
                                }
                                gotResponse.unlock();
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("token", CacheHandler.getToken());
                            params.put("memoId", memo.memo.id);
                            params.put("level", "" + memo.remindLevel);
                            return params;
                        }
                    }
            );
            if (memo.remindLevel < 0){
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_off_24);
            } else if (memo.remindLevel == 0){
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_24_gray);
            } else {
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notification_important_24);
            }
            holder.itemView.setBackgroundColor(Color.argb(0,250,250,250));

            new Handler().postDelayed(new Runnable(){
                public void run() {
                    //TODO
                    holder.itemView.setBackgroundResource(R.drawable.group_shape);
                    holder.itemView.setPadding(38,38,38,38);
                }
            }, 200);
        });
    }
    @Override
    public int getItemCount(){
        return memoList.size();
    }
}
