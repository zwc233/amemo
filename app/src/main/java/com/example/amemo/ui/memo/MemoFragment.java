package com.example.amemo.ui.memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Cache;
import com.example.amemo.CacheHandler;
import com.example.amemo.databinding.FragmentMemoBinding;

import java.util.ArrayList;
import java.util.List;

public class MemoFragment extends Fragment {

    private FragmentMemoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MemoViewModel memoViewModel =
                new ViewModelProvider(this).get(MemoViewModel.class);

        binding = FragmentMemoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView recyclerView = binding.viewMemo;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        List<MemoItem> list = new ArrayList<>();
        for (String memoId : CacheHandler.user.createdMemos) {
            CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
            list.add(new MemoItem(memo));
        }
        for (String memoId : CacheHandler.user.notedMemos) {
            if (!CacheHandler.user.createdMemos.contains(memoId)) {
                CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
                list.add(new MemoItem(memo));
            }
        }
        for (String memoId : CacheHandler.user.emphasizedMemos) {
            if (!CacheHandler.user.createdMemos.contains(memoId)) {
                CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
                list.add(new MemoItem(memo));
            }
        }

        MemoAdapter fruitAdapter = new MemoAdapter(list);

        recyclerView.setAdapter(fruitAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updateMemos() {
        final RecyclerView recyclerView = binding.viewMemo;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        List<MemoItem> list = new ArrayList<>();
        for (String memoId : CacheHandler.user.createdMemos) {
            CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
            list.add(new MemoItem(memo));
        }
        for (String memoId : CacheHandler.user.notedMemos) {
            if (!CacheHandler.user.createdMemos.contains(memoId)) {
                CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
                list.add(new MemoItem(memo));
            }
        }
        for (String memoId : CacheHandler.user.emphasizedMemos) {
            if (!CacheHandler.user.createdMemos.contains(memoId)) {
                CacheHandler.Memo memo = CacheHandler.getMemo(memoId);
                list.add(new MemoItem(memo));
            }
        }

        MemoAdapter fruitAdapter = new MemoAdapter(list);

        recyclerView.setAdapter(fruitAdapter);
    }
}