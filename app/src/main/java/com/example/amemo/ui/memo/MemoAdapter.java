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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amemo.R;
import com.example.amemo.ui.group.InGroupActivity;

import java.util.List;




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
        holder.memoName.setText(memo.getName());
        holder.memoName.measure(0,0);
        final float scale = parentIn.getContext().getResources().getDisplayMetrics().density;
        holder.linearLayout.getLayoutParams().height = (int) (holder.memoName.getMeasuredHeight() * 1 + 100);

        holder.itemView.setOnClickListener(v -> {
            System.out.println(position);
            notifyDataSetChanged();
            memo.remindLevel += 1;
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
            }, 200);   //
        });
    }
    @Override
    public int getItemCount(){
        return memoList.size();
    }
}
