package com.example.amemo;

public class UrlUtils {
    public static String httpBaseUrl = "http://localhost:8000";
    public static String wsBaseUrl = "ws://localhost:8000/subscribe";

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

    public static String makeHttpRequest(String url) {
        return httpBaseUrl + url;
    }

    public static String makeWsUrl(String token) {
        return wsBaseUrl + "/" + token;
    }
}
