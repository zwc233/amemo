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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amemo.R;
import com.example.amemo.ui.group.GroupAdapter;
import com.example.amemo.ui.group.InGroupActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MemberItem {
    public String name;
    public int remindLevel;
    public MemberItem(String name, String groupId) {
        this.name = name;
        CacheHandler.User.FollowRecord followRecord = new CacheHandler.User.FollowRecord(name, groupId);
        if (CacheHandler.user.particularInterests.contains(followRecord)) {
            remindLevel = 1;
        } else if (CacheHandler.user.followedUsers.contains(followRecord)) {
            remindLevel = 0;
        } else {
            remindLevel = -1;
        }
    }

    public String getName() {
        return name;
    }

}
public class SeeAllMemberAdapter extends RecyclerView.Adapter<SeeAllMemberAdapter.ViewHolder>{
    private final List<MemberItem> memberItemList;
    String groupId;
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

    public SeeAllMemberAdapter(List<MemberItem> memoList, String groupId) {
        this.memberItemList = memoList;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public SeeAllMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentIn = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent,false);
        return new ViewHolder(view);
    }

    Lock gotResponse = new ReentrantLock();

    @Override
    public void onBindViewHolder(@NonNull SeeAllMemberAdapter.ViewHolder holder, int position) {
        MemberItem memberItem = memberItemList.get(position);
        holder.memberName.setText(memberItem.getName());

        RequestQueue requestQueue = Volley.newRequestQueue(parentIn.getContext());

        holder.itemView.setOnClickListener(v -> {
            System.out.println(position);
            notifyDataSetChanged();
            gotResponse.lock();
            memberItem.remindLevel++;
            if (memberItem.remindLevel > 1) {
                memberItem.remindLevel = -1;
            }
            requestQueue.add(
                    new StringRequest(
                            Request.Method.POST,
                            UrlUtils.makeHttpUrl(UrlUtils.followUrl),
                            response -> {
                                try {
                                    JSONObject responseObj = new JSONObject(response);
                                    if (responseObj.getString("code").equals("200")) {
                                        if (memberItem.remindLevel < 0) {
                                            Toast.makeText(parentIn.getContext(),
                                                    R.string.unfollow_success,
                                                    Toast.LENGTH_SHORT).show();
                                            CacheHandler.User.FollowRecord followRecord =
                                                    new CacheHandler.User.FollowRecord(
                                                            memberItem.name,
                                                            groupId
                                                    );
                                            CacheHandler.User user = CacheHandler.getUser();
                                            user.particularInterests.remove(followRecord);
                                            user.followedUsers.remove(followRecord);
                                            JSONArray createdMemos =
                                                    responseObj.getJSONObject("info").getJSONArray("createdMemos");
                                            for (int i = 0; i < createdMemos.length(); i++) {
                                                JSONObject memo = createdMemos.getJSONObject(i);
                                                user.emphasizedMemos.remove(memo.getString("id"));
                                                user.notedMemos.remove(memo.getString("id"));
                                            }
                                        } else if (memberItem.remindLevel == 0) {
                                            Toast.makeText(parentIn.getContext(),
                                                    R.string.follow_success,
                                                    Toast.LENGTH_SHORT).show();
                                            CacheHandler.User.FollowRecord followRecord =
                                                    new CacheHandler.User.FollowRecord(
                                                            memberItem.name,
                                                            groupId
                                                    );
                                            CacheHandler.User user = CacheHandler.getUser();
                                            user.particularInterests.remove(followRecord);
                                            user.followedUsers.add(followRecord);
                                            JSONArray createdMemos =
                                                    responseObj.getJSONObject("info").getJSONArray("createdMemos");
                                            for (int i = 0; i < createdMemos.length(); i++) {
                                                JSONObject memo = createdMemos.getJSONObject(i);
                                                user.emphasizedMemos.remove(memo.getString("id"));
                                                if (memo.getString("group").equals(groupId)) {
                                                    user.notedMemos.add(memo.getString("id"));
                                                    CacheHandler.saveMemo(memo);
                                                }
                                            }
                                        } else {
                                            Toast.makeText(parentIn.getContext(),
                                                    R.string.special_interest_success,
                                                    Toast.LENGTH_SHORT).show();
                                            CacheHandler.User.FollowRecord followRecord =
                                                    new CacheHandler.User.FollowRecord(
                                                            memberItem.name,
                                                            groupId
                                                    );
                                            CacheHandler.User user = CacheHandler.getUser();
                                            user.particularInterests.add(followRecord);
                                            user.followedUsers.remove(followRecord);
                                            JSONArray createdMemos =
                                                    responseObj.getJSONObject("info").getJSONArray("createdMemos");
                                            for (int i = 0; i < createdMemos.length(); i++) {
                                                JSONObject memo = createdMemos.getJSONObject(i);
                                                if (memo.getString("group").equals(groupId)) {
                                                    user.emphasizedMemos.add(memo.getString("id"));
                                                    CacheHandler.saveMemo(memo);
                                                }
                                                user.notedMemos.remove(memo.getString("id"));
                                            }
                                        }
                                    } else {
                                        Toast.makeText(parentIn.getContext(),
                                                R.string.follow_failed,
                                                Toast.LENGTH_SHORT).show();
                                        memberItem.remindLevel--;
                                        if (memberItem.remindLevel < -1) {
                                            memberItem.remindLevel = 1;
                                        }
                                    }
                                } catch (JSONException e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(parentIn.getContext(),
                                            R.string.request_failed,
                                            Toast.LENGTH_SHORT).show();
                                    memberItem.remindLevel--;
                                    if (memberItem.remindLevel < -1) {
                                        memberItem.remindLevel = 1;
                                    }
                                }
                                gotResponse.unlock();
                            },
                            error -> {
                                System.out.println(error.getMessage());
                                Toast.makeText(parentIn.getContext(),
                                        R.string.no_response,
                                        Toast.LENGTH_SHORT).show();
                                memberItem.remindLevel--;
                                if (memberItem.remindLevel < -1) {
                                    memberItem.remindLevel = 1;
                                }
                                gotResponse.unlock();
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("token", CacheHandler.getToken());
                            params.put("followeeName", memberItem.name);
                            params.put("groupId", groupId);
                            params.put("level", "" + memberItem.remindLevel);
                            return params;
                        }
                    }
            );

            gotResponse.lock();
            if (memberItem.remindLevel < 0){
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_off_24);
            } else if (memberItem.remindLevel == 0){
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notifications_24_gray);
            } else {
                holder.remindLevelImage.setImageResource(R.drawable.ic_baseline_notification_important_24);
            }
            holder.itemView.setBackgroundColor(Color.argb(0,250,250,250));
            gotResponse.unlock();

            new Handler().postDelayed(new Runnable(){
                public void run() {
                    holder.itemView.setBackgroundResource(R.drawable.group_shape);
                    holder.itemView.setPadding(38,38,38,38);
                }
            }, 200);
        });
    }
    @Override
    public int getItemCount(){
        return memberItemList.size();
    }
}
