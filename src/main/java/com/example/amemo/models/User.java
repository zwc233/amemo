package com.example.amemo.models;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped;
import org.springframework.data.mongodb.core.mapping.Unwrapped.OnEmpty;

@Document("user")
public class User {

    @Id
    public String id;

    @Field("username")
    @Indexed(unique = true)
    public String username;

    @Field("password")
    public String password;

    @Field("joined_groups")
    @DBRef(lazy = true)
    public Set<Group> joinedGroups;

    @Field("created_memos")
    @DBRef(lazy = true)
    public Set<Memo> createdMemos;

    @Unwrapped(onEmpty = OnEmpty.USE_EMPTY)
    public ReminderConfig reminderConfig;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.joinedGroups = new HashSet<>();
        this.createdMemos = new HashSet<>();
        this.reminderConfig = new ReminderConfig();
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        try {
            return username.equals(((User)o).username);
        } catch (Exception e) {
            return false;
        }
    }

    public static class ReminderConfig {
        
        public final static int MUTE = 0, VIBRATE = 1, RING = 2;

        @Field("global_level")
        public int globalLevel;

        @Field("particular_interests")
        public Set<FollowRecord> particularInterests;

        @Field("followed_users")
        public Set<FollowRecord> followedUsers;

        @Field("specially_noted_memos")
        @DBRef(lazy = true)
        public Set<Memo> emphasizedMemos;

        @Field("noted_memos")
        @DBRef(lazy = true)
        public Set<Memo> notedMemos;

        public ReminderConfig() {
            this.globalLevel          =  MUTE;
            this.particularInterests  =  new HashSet<>();
            this.followedUsers        =  new HashSet<>();
            this.emphasizedMemos      =  new HashSet<>();
            this.notedMemos           =  new HashSet<>();
        }

        public static class FollowRecord {
            @Field("user")
            public String userId;

            @Field("group")
            public String groupId;

            @PersistenceCreator
            public FollowRecord(String userId, String groupId) {
                this.userId = userId;
                this.groupId = groupId;
            }

            @Override
            public int hashCode() {
                return userId.hashCode() + groupId.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                try {
                    return userId.equals(((FollowRecord)o).userId) && groupId.equals(((FollowRecord)o).groupId);
                } catch (Exception e) {
                    return false;
                }
            }
        }
    }
}