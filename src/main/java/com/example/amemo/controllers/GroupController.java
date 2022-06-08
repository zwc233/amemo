package com.example.amemo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.example.amemo.models.Group;
import com.example.amemo.models.User;
import com.example.amemo.services.AccountService;
import com.example.amemo.services.GroupService;
import com.example.amemo.services.AccountService.AccountException;
import com.example.amemo.services.GroupService.GroupException;

@RestController
public class GroupController {
    
    @Autowired
    AccountService accountService;

    @Autowired
    GroupService groupService;

    @RequestMapping("/createGroup")
    public JSONObject createGroup(String token, String name, String description) {
        
        JSONObject jsonObject = new JSONObject();

        try {
            String username = accountService.validate(token);
            User user = accountService.findUserByUsername(username);
            Group group = new Group(name, description);
            groupService.createGroup(user, group);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully created group!");
            jsonObject.put("id", group.id);
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        } catch (GroupException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }

    @RequestMapping("/follow")
    public JSONObject follow(String token, String followeeName, String groupId, int level) {
        
        JSONObject jsonObject = new JSONObject();

        try {
            String username = accountService.validate(token);
            User follower = accountService.findUserByUsername(username);
            User followee = accountService.findUserByUsername(followeeName);
            Group group = groupService.findGroupById(groupId);
            groupService.follow(follower, followee, group, level != 0);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully followed user!");
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        } catch (GroupException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }
}
