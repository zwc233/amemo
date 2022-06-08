package com.example.amemo.ui.group;

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

import com.example.amemo.databinding.FragmentGroupBinding;


import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private FragmentGroupBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GroupViewModel groupViewModel =
                new ViewModelProvider(this).get(GroupViewModel.class);

        binding = FragmentGroupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // final TextView textView = binding.textGroup;
        // groupViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final RecyclerView recyclerView = binding.viewGroup;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        List<GroupItem> list = new ArrayList<>();
        list.add(new GroupItem("abc"));
        list.add(new GroupItem("bcd"));
        GroupAdapter fruitAdapter = new GroupAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}