package com.example.amemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CacheHandler {
    public static String token;
    public static User user;
    public static HashMap<String, Memo> memos = new HashMap<>();
    public static HashMap<String, Group> groups = new HashMap<>();

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        CacheHandler.token = token;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(JSONObject userObj) throws JSONException {
        user = new User(userObj);
    }

    public static Memo getMemo(String id) {
        return memos.get(id);
    }

    public static void saveMemo(JSONObject memoObj) throws JSONException {
        Memo memo = new Memo(memoObj);
        memos.put(memo.id, memo);
    }

    public static void addMemos(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            Memo memo = new Memo(jsonArray.getJSONObject(i));
            memos.put(memo.id, memo);
        }
    }

    public static void removeMemo(String id) {
        memos.remove(id);
    }

    public static Group getGroup(String id) {
        return groups.get(id);
    }

    public static void saveGroup(JSONObject groupObj) throws JSONException {
        Group group = new Group(groupObj);
        groups.put(group.id, group);
    }

    public static void addGroups(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            Group group = new Group(jsonArray.getJSONObject(i));
            groups.put(group.id, group);
        }
    }

    public void removeGroup(String id) {
        groups.remove(id);
    }

    public static void init(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        token = jsonObject.getString("token");
        user = new User(jsonObject.getJSONObject("info"));
        addMemos(jsonObject.getJSONArray("createdMemos"));
        addMemos(jsonObject.getJSONObject("reminderConfig").getJSONArray("notedMemos"));
        addMemos(jsonObject.getJSONObject("reminderConfig").getJSONArray("emphasizedMemos"));
        addGroups(jsonObject.getJSONArray("joinedGroups"));
    }

    public static class Memo {
        public String id;
        public String creator;
        public String group;
        public String title;
        public String content;
        public long when;

        public Memo(JSONObject jsonObject) throws JSONException {
            this.id = jsonObject.getString("id");
            this.creator = jsonObject.getString("creator");
            this.group = jsonObject.getString("group");
            this.title = jsonObject.getString("title");
            this.content = jsonObject.getString("content");
            this.when = jsonObject.getLong("when");
        }

        @Override
        public boolean equals(Object o) {
            try {
                return id.equals(((Memo)o).id);
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class Group {
        public String id;
        public String name;
        public String description;
        public String owner;
        public Set<String> admins = new HashSet<>();
        public Set<String> members = new HashSet<>();
        public Set<String> memos = new HashSet<>();

        public Group(JSONObject groupObj) throws JSONException {
            this.id = groupObj.getString("id");
            this.name = groupObj.getString("name");
            this.description = groupObj.getString("description");
            this.owner = groupObj.getString("owner");

            JSONArray admins = groupObj.getJSONArray("admins");
            for (int i = 0; i < admins.length(); i++) {
                this.admins.add(admins.getString(i));
            }

            JSONArray members = groupObj.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                this.members.add(members.getString(i));
            }

            JSONArray memos = groupObj.getJSONArray("memos");
            for (int i = 0; i < memos.length(); i++) {
                this.memos.add(memos.getString(i));
            }
        }

        @Override
        public boolean equals(Object o) {
            try {
                return id.equals(((Group)o).id);
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class User {
        public String username;
        public int globalLevel;
        public Set<String> createdMemos = new HashSet<>();
        public Set<String> joinedGroups = new HashSet<>();
        public Set<FollowRecord> particularInterests = new HashSet<>();
        public Set<FollowRecord> followedUsers = new HashSet<>();
        public Set<String> emphasizedMemos = new HashSet<>();
        public Set<String> notedMemos = new HashSet<>();

        public static class FollowRecord {
            public String username;
            public String groupId;
            public FollowRecord(String username, String groupId) {
                this.username = username;
                this.groupId = groupId;
            }
        }

        public User(JSONObject userObj) throws JSONException {
            JSONObject cfg = userObj.getJSONObject("reminderConfig");

            this.username = userObj.getString("username");
            this.globalLevel = cfg.getInt("globalLevel");

            JSONArray createdMemos = userObj.getJSONArray("createdMemos");
            for (int i = 0; i < createdMemos.length(); i++) {
                this.createdMemos.add(createdMemos.getJSONObject(i).getString("id"));
            }

            JSONArray joinedGroups = userObj.getJSONArray("joinedGroups");
            for (int i = 0; i < joinedGroups.length(); i++) {
                this.joinedGroups.add(joinedGroups.getJSONObject(i).getString("id"));
            }

            JSONArray particularInterests = cfg.getJSONArray("particularInterests");
            for (int i = 0; i < particularInterests.length(); i++) {
                JSONObject interest = particularInterests.getJSONObject(i);
                this.particularInterests.add(new FollowRecord(
                        interest.getString("userId"),
                        interest.getString("groupId")
                ));
            }

            JSONArray followedUsers = cfg.getJSONArray("particularInterests");
            for (int i = 0; i < followedUsers.length(); i++) {
                JSONObject followed = followedUsers.getJSONObject(i);
                this.followedUsers.add(new FollowRecord(
                        followed.getString("userId"),
                        followed.getString("groupId")
                ));
            }

            JSONArray emphasizedMemos = cfg.getJSONArray("emphasizedMemos");
            for (int i = 0; i < emphasizedMemos.length(); i++) {
                JSONObject emphasized = emphasizedMemos.getJSONObject(i);
                this.emphasizedMemos.add(emphasized.getString("id"));
                CacheHandler.saveMemo(emphasized);
            }

            JSONArray notedMemos = cfg.getJSONArray("notedMemos");
            for (int i = 0; i < notedMemos.length(); i++) {
                JSONObject noted = notedMemos.getJSONObject(i);
                this.notedMemos.add(noted.getString("id"));
                CacheHandler.saveMemo(noted);
            }
        }
    }
}

