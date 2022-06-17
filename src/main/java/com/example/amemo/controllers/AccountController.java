package com.example.amemo.controllers;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.amemo.models.User;
import com.example.amemo.services.AccountService;
import com.example.amemo.services.AccountService.AccountException;

@RestController
public class AccountController {
    @Autowired
    AccountService accountService;

    @CrossOrigin
    @RequestMapping("/signUp")
    public JSONObject signUp(String username, String password) {
        JSONObject jsonObject = new JSONObject();

        try {
            accountService.signUp(new User(username, password));
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully created an account!");
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/signIn")
    public JSONObject signIn(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        
        try {
            String token = accountService.signIn(username, password);
            User user = accountService.findUserByUsername(username);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully signed in!");
            jsonObject.put("token", token);
            jsonObject.put("info", user);
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }
        
        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/changePassword")
    public JSONObject changePassword(String token, String oldPasswd, String newPasswd) {
        JSONObject jsonObject = new JSONObject();
        
        try {
            String username = accountService.validate(token);
            accountService.changePassword(username, oldPasswd, newPasswd);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully changed password.");
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/changeGlobalLevel")
    public JSONObject changeGlobalLevel(String token, int newLevel) {
        JSONObject jsonObject = new JSONObject();

        try {
            String username = accountService.validate(token);
            User user = accountService.getFullUserInfo(username);
            accountService.changeGlobalLevel(user, newLevel);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully changed global level.");
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }
        
        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/personalInfo")
    public JSONObject getPersonalInfo(String token) {
        JSONObject jsonObject = new JSONObject();

        try {
            String username = accountService.validate(token);
            User user = accountService.findUserByUsername(username);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully got personal info.");
            jsonObject.put("info", user);
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping("/userInfo")
    public JSONObject getUserInfo(String token, String uIds) {
        JSONObject jsonObject = new JSONObject();
        String[] userIds = uIds.split(":");

        try {
            accountService.validate(token);
            ArrayList<JSONObject> users = new ArrayList<>();
            int numFound = 0;
            for (String userId : userIds) {
                JSONObject tmpObject = new JSONObject();
                tmpObject.put("id", userId);
                try {
                    User user = accountService.findUserByUsername(userId);
                    tmpObject.put("code", "200");
                    tmpObject.put("msg", "Found.");
                    tmpObject.put("info", user);
                    numFound++;
                } catch (AccountException e) {
                    tmpObject.put("code", e.code);
                    tmpObject.put("msg", e.message);
                }
                users.add(tmpObject);
            }
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Found " + numFound + ".");
            jsonObject.put("result", users);
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }
        
        return jsonObject;
    }
}
