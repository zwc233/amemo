package com.example.amemo.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amemo.CacheHandler;
import com.example.amemo.R;
import com.example.amemo.UrlUtils;
import com.example.amemo.databinding.FragmentGroupBinding;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        Lock gotResponse = new ReentrantLock();

        final RecyclerView recyclerView = binding.viewGroup;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        List<GroupItem> list = new ArrayList<>();
        StringBuilder notFound = new StringBuilder();
        CacheHandler.User user = CacheHandler.getUser();
        for (CacheHandler.Group group : CacheHandler.groups.values()) {
            list.add(new GroupItem(group));
        }

//        for (String groupId : user.joinedGroups) {
//            CacheHandler.Group group = CacheHandler.getGroup(groupId);
//            if (group == null) {
//                if (notFound.length() > 0) {
//                    notFound.append(":");
//                }
//                notFound.append(groupId);
//            }
//            list.add(new GroupItem(group));
//        }
//        if (notFound.length() > 0) {
//            requestQueue.add(
//                    new StringRequest(
//                            Request.Method.POST,
//                            UrlUtils.makeHttpUrl(UrlUtils.memoInfoUrl),
//                            response -> {
//                                try {
//                                    JSONObject responseObj = new JSONObject(response);
//                                    JSONArray result = responseObj.getJSONArray("result");
//                                    for (int i = 0; i < result.length(); i++) {
//                                        JSONObject info = result.getJSONObject(i);
//                                        String gId = info.getString("id");
//                                        if (info.getString("code").equals("200")) {
//                                            JSONObject groupObj = info.getJSONObject("info");
//                                            CacheHandler.saveGroup(groupObj);
//                                            list.add(new GroupItem(CacheHandler.getGroup(gId)));
//                                        } else {
//                                            System.out.println(info.getString("msg"));
//                                            Toast.makeText(getContext(),
//                                                    "群组 " + gId + " 可能已经被删除",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                } catch (JSONException e) {
//                                    System.out.println(e.getMessage());
//                                    Toast.makeText(getContext(),
//                                            R.string.response_parse_failure,
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                                gotResponse.unlock();
//                            },
//                            error -> {
//                                System.out.println(error.getMessage());
//                                Toast.makeText(
//                                        getContext(),
//                                        R.string.request_failed,
//                                        Toast.LENGTH_SHORT).show();
//                                gotResponse.unlock();
//                            }
//                    ) {
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("token", CacheHandler.getToken());
//                            params.put("gIds", notFound.toString());
//                            return params;
//                        }
//                    }
//            );
//        }

        gotResponse.lock();
        GroupAdapter fruitAdapter = new GroupAdapter(list);
        recyclerView.setAdapter(fruitAdapter);
        gotResponse.unlock();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}