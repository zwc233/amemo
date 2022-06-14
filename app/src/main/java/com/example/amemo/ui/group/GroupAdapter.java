package com.example.amemo.ui.group;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amemo.CacheHandler;
import com.example.amemo.R;

import java.util.List;

class GroupItem {
    public CacheHandler.Group group;

    public GroupItem(CacheHandler.Group group) {
        this.group = group;
    }
}


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{
    private List<GroupItem> groupItemList;
    ViewGroup parentIn;
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView groupName;
        ImageView groupIcon;
        TextView groupContent;

        public ViewHolder(@NonNull View view) {
            super(view);
            groupName = (TextView)view.findViewById(R.id.group_name);
            groupIcon = (ImageView)view.findViewById(R.id.group_icon);
            groupContent = (TextView)view.findViewById(R.id.group_content);
        }
    }

    public GroupAdapter(List<GroupItem> memoList) {
        this.groupItemList = memoList;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentIn = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item,parent,false);
        GroupAdapter.ViewHolder viewHolder = new GroupAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        GroupItem groupItem = groupItemList.get(position);
        holder.groupName.setText(groupItem.group.name);
        holder.groupContent.setText(groupItem.group.description);


        holder.itemView.setOnClickListener(v -> {
            System.out.println(position);
            notifyDataSetChanged();
            holder.itemView.setBackgroundColor(Color.argb(0,240,240,240));

            Intent intent = new Intent();
            intent.setClass(v.getContext(), InGroupActivity.class);
            intent.putExtra("groupId", groupItem.group.id);
            v.getContext().startActivity(intent);
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    //TODO
                    holder.itemView.setBackgroundResource(R.drawable.group_shape);
                    holder.itemView.setPadding(43,43,43,43);
                }
            }, 200);   //


        });
    }

    @Override
    public int getItemCount(){
        return groupItemList.size();
    }
}
