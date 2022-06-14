package com.example.amemo.controllers;

import com.alibaba.fastjson.JSONObject;

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
    public JSONObject getUserInfo(String token, String username) {
        JSONObject jsonObject = new JSONObject();

        try {
            accountService.validate(token);
            User user = accountService.findUserByUsername(username);
            jsonObject.put("code", "200");
            jsonObject.put("msg", "Successfully got user info.");
            jsonObject.put("info", user);
        } catch (AccountException e) {
            jsonObject.put("code", e.code);
            jsonObject.put("msg", e.message);
        }

        return jsonObject;
    }
}
