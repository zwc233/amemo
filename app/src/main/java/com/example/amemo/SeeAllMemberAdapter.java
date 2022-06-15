package com.example.amemo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amemo.R;
import com.example.amemo.ui.group.GroupAdapter;
import com.example.amemo.ui.group.InGroupActivity;

import java.util.List;

class MemberItem {
    public String name;
    public int remindLevel;
    public MemberItem(String name) {
        this.name = name;
        remindLevel = 0;
    }

    public String getName() {
        return name;
    }

}
public class SeeAllMemberAdapter extends RecyclerView.Adapter<SeeAllMemberAdapter.ViewHolder>{
    private final List<MemberItem> memberItemList;
    ViewGroup parentIn;
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView memberName;
        ImageView remindLevelImage;
        public ViewHolder(@NonNull View view) {
            super(view);
            memberName = (TextView)view.findViewById(R.id.member_name);
            remindLevelImage = view.findViewById(R.id.remindLevelImage);
        }
    }

    public SeeAllMemberAdapter(List<MemberItem> memoList) {
        this.memberItemList = memoList;
    }

    @NonNull
    @Override
    public SeeAllMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentIn = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeeAllMemberAdapter.ViewHolder holder, int position) {
        MemberItem memberItem = memberItemList.get(position);
        holder.memberName.setText(memberItem.getName());

        holder.itemView.setOnClickListener(v -> {
            System.out.println(position);
            notifyDataSetChanged();
            memberItem.remindLevel += 1;
            if (memberItem.remindLevel%3 == 0){
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_off_24);
            } else if (memberItem.remindLevel%3 == 1){
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
        return memberItemList.size();
    }
}
