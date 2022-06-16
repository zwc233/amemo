package com.example.amemo.ui.memo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.example.amemo.ui.group.InGroupActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder>{
    private List<MemoItem> memoList;
    ViewGroup parentIn;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView memoName;
        LinearLayout linearLayout;
        ImageView remindLevelImage;

        public ViewHolder(@NonNull View view) {
            super(view);
            memoName = (TextView)view.findViewById(R.id.memo_name);
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemoItem memo = memoList.get(position);
        holder.memoName.setText(memo.memo.title);
        holder.memoName.measure(0,0);
        final float scale = parentIn.getContext().getResources().getDisplayMetrics().density;
        holder.linearLayout.getLayoutParams().height = (int) (holder.memoName.getMeasuredHeight() * 1 + 100);

        RequestQueue requestQueue = Volley.newRequestQueue(parentIn.getContext());

        Lock gotResponse = new ReentrantLock();

        holder.itemView.setOnClickListener(v -> {
            System.out.println(position);
            notifyDataSetChanged();
            memo.remindLevel += 1;
            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.noteMemoUrl),
                            response -> {
                               try {
                                   JSONObject responseObj = new JSONObject(response);
                                   System.out.println(responseObj.getString("msg"));
                                   if (responseObj.getString("code").equals("200")) {
                                       switch (memo.remindLevel % 3) {
                                           case 0: {
                                               Toast.makeText(parentIn.getContext(),
                                                       R.string.unnote_success,
                                                       Toast.LENGTH_SHORT).show();
                                               CacheHandler.User user = CacheHandler.getUser();
                                               user.emphasizedMemos.remove(memo.memo.id);
                                               user.notedMemos.remove(memo.memo.id);
                                               break;
                                           }
                                           case 1: {
                                               Toast.makeText(parentIn.getContext(),
                                                       R.string.note_success,
                                                       Toast.LENGTH_SHORT).show();
                                               CacheHandler.User user = CacheHandler.getUser();
                                               user.emphasizedMemos.remove(memo.memo.id);
                                               user.notedMemos.add(memo.memo.id);
                                               break;
                                           }
                                           case 2: {
                                               Toast.makeText(parentIn.getContext(),
                                                       R.string.emphasize_success,
                                                       Toast.LENGTH_SHORT).show();
                                               CacheHandler.User user = CacheHandler.getUser();
                                               user.emphasizedMemos.add(memo.memo.id);
                                               user.notedMemos.remove(memo.memo.id);
                                               break;
                                           }
                                       }
                                   }
                               } catch (JSONException e) {
                                   System.out.println(e.getMessage());
                                   Toast.makeText(parentIn.getContext(),
                                           R.string.response_parse_failure,
                                           Toast.LENGTH_SHORT).show();
                               }
                               gotResponse.unlock();
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(parentIn.getContext(),
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
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
            if (memo.remindLevel%3 == 0){
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_off_24);
            } else if (memo.remindLevel%3 == 1){
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
