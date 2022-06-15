package com.example.amemo.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.amemo.CacheHandler;
import com.example.amemo.databinding.FragmentGroupBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        Lock gotResponse = new ReentrantLock();

        final RecyclerView recyclerView = binding.viewGroup;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        List<GroupItem> list = new ArrayList<>();
        CacheHandler.User user = CacheHandler.getUser();
        for (String groupId : user.joinedGroups) {
            CacheHandler.Group group = CacheHandler.getGroup(groupId);
            if (group == null) {
                // TODO: request for data
            }
            list.add(new GroupItem(group));
        }
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