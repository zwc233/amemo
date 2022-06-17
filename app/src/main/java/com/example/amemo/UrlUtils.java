package com.example.amemo;

public class UrlUtils {
    public static String serverIp    = "121.40.84.48";
    public static String httpBaseUrl = "http://" + serverIp + ":8000";
    public static String wsBaseUrl   = "ws://" + serverIp + ":8000/subscribe";

    public static String signInUrl        =  "/signIn";
    public static String signUpUrl        =  "/signUp";
    public static String personalInfoUrl  =  "/personalInfo";
    public static String userInfoUrl      =  "/userInfo";
    public static String createGroupUrl   =  "/createGroup";
    public static String inviteUrl        =  "/invite";
    public static String followUrl        =  "/follow";
    public static String groupInfoUrl     =  "/groupInfo";
    public static String createMemoUrl    =  "/createMemo";
    public static String deleteMemoUrl    =  "/deleteMemo";
    public static String noteMemoUrl      =  "/noteMemo";
    public static String memoInfoUrl      =  "/memoInfo";

    public static String makeHttpUrl(String url) {
        System.out.println(httpBaseUrl + url);
        return httpBaseUrl + url;
    }

    public static String makeWsUrl(String token) {
        System.out.println(wsBaseUrl + "/" + token);
        return wsBaseUrl + "/" + token;
    }
}
