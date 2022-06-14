package com.example.amemo.ui.memo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amemo.R;

import java.util.List;




public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder>{
    private List<MemoItem> memoList;
    ViewGroup parentIn;
    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView memoName;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View view) {
            super(view);
            memoName = (TextView)view.findViewById(R.id.memo_name);
            linearLayout = view.findViewById(R.id.memo_linear_layout);
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
        holder.linearLayout.getLayoutParams().height = (int) (holder.memoName.getMeasuredHeight() + 100);
    }
    @Override
    public int getItemCount(){
        return memoList.size();
    }
}
