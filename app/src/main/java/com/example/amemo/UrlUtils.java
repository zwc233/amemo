package com.example.amemo;

public class UrlUtils {
    public static String serverIp = "192.168.103.25";
    public static String httpBaseUrl = "http://" + serverIp + ":8000";
    public static String wsBaseUrl = "ws://" + serverIp + ":8000/subscribe";

    public static String signInUrl = "/signIn";
    public static String signUpUrl = "/signUp";
    public static String personalInfoUrl = "/personalInfo";
    public static String userInfoUrl = "/userInfo";
    public static String createGroupUrl = "/createGroup";
    public static String inviteUrl = "/invite";
    public static String followUrl = "/follow";
    public static String createMemoUrl = "/createMemo";
    public static String deleteMemoUrl = "/deleteMemo";
    public static String noteMemoUrl = "/noteMemo";
    public static String unnoteMemoUrl = "/unnoteMemo";

    public static String makeHttpUrl(String url) {
        System.out.println(httpBaseUrl + url);
        return httpBaseUrl + url;
    }

    public static String makeWsUrl(String token) {
        System.out.println(wsBaseUrl + "/" + token);
        return wsBaseUrl + "/" + token;
    }
}
