package com.example.amemo.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping("/createMemo")
    public JSONObject createMemo(String token, String groupId, String title, String content, long cycle) {
        
        JSONObject jsonObject = new JSONObject();
        
        try {
            String username = accountService.validate(token);
            User user = accountService.findUserByUsername(username);
            Memo memo = new Memo(title, content, new Date(), cycle);
            Group group = groupService.findGroupById(groupId);
            memoService.createMemo(user, group, memo);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully created memo.");
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
}
