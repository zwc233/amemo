package com.example.amemo.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.example.amemo.models.Group;
import com.example.amemo.models.Memo;
import com.example.amemo.models.User;
import com.example.amemo.services.AccountService;
import com.example.amemo.services.GroupService;
import com.example.amemo.services.MemoService;
import com.example.amemo.services.AccountService.AccountException;
import com.example.amemo.services.GroupService.GroupException;
import com.example.amemo.services.MemoService.MemoException;

@RestController
public class MemoController {
    
    @Autowired
    AccountService accountService;

    @Autowired
    GroupService groupService;
    
    @Autowired
    MemoService memoService;

    @CrossOrigin
    @RequestMapping("/createMemo")
    public JSONObject createMemo(String token, String groupId, String title, String content, long when, long cycle) {
        JSONObject jsonObject = new JSONObject();
        
        try {
            String username = accountService.validate(token);
            User user = accountService.getFullUserInfo(username);
            Memo memo = new Memo(title, content, when, cycle);
            Group group = groupService.findGroupById(groupId);
            memoService.createMemo(user, group, memo);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully created memo!");
            jsonObject.put("id", memo.id);
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        } catch (GroupException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        } catch (MemoException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/deleteMemo")
    public JSONObject deleteMemo(String token, String groupId, String memoId) {
        JSONObject jsonObject = new JSONObject();

        try {
            String username = accountService.validate(token);
            User user = accountService.getFullUserInfo(username);
            Group group = groupService.findGroupById(groupId);
            Memo memo = memoService.findMemoById(memoId);
            memoService.deleteMemo(user, group, memo);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully deleted memo!");
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        } catch (GroupException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        } catch (MemoException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/noteMemo")
    public JSONObject noteMemo(String token, String memoId, int level) {
        JSONObject jsonObject = new JSONObject();

        try {
            String username = accountService.validate(token);
            User user = accountService.getFullUserInfo(username);
            Memo memo = memoService.findMemoById(memoId);
            memoService.noteMemo(user, memo, level);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully noted memo!");
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        } catch (MemoException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/memoInfo")
    public JSONObject getMemoInfo(String token, String mIds) {
        JSONObject jsonObject = new JSONObject();
        String[] memoIds = mIds.split(":");

        try {
            accountService.validate(token);
            ArrayList<JSONObject> groups = new ArrayList<>();
            int numFound = 0;
            for (String memoId : memoIds) {
                JSONObject tmpObject = new JSONObject();
                tmpObject.put("id", memoId);
                try {
                    Memo memo = memoService.findMemoById(memoId);
                    tmpObject.put("code", "200");
                    tmpObject.put("msg", "Found.");
                    tmpObject.put("info", memo);
                    numFound++;
                } catch (MemoException e) {
                    tmpObject.put("code", e.code);
                    tmpObject.put("msg", e.message);
                }
                groups.add(tmpObject);
            }
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Found " + numFound + ".");
            jsonObject.put("result", groups);
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }
        
        return jsonObject;
    }
}
