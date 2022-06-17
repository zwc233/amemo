package com.example.amemo.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.example.amemo.models.Group;
import com.example.amemo.models.Memo;
import com.example.amemo.models.User;
import com.example.amemo.models.User.ReminderConfig.FollowRecord;
import com.example.amemo.websocket.SubscribeHandler;

@Service
public class GroupService {
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    AccountService accountService;

    public void createGroup(User user, Group group) throws GroupException {
        System.out.println("createGroup: " + user.username + ", " + group.name);
        try {
            group.owner = user.username;
            mongoTemplate.save(group);
            user.joinedGroups.add(group);
            System.out.println(user.id);
            mongoTemplate.save(user);
        } catch (Exception e) {
            throw new GroupException("401", "Failed to create group due to: " + e.getMessage());
        }
    }

    public void inviteToGroup(User invitor, User invitee, Group group) throws GroupException {
        System.out.println("inviteToGroup: " + invitor.username + ", " + invitee.username + ", " + group.name);
        try {
            if (!invitor.username.equals(group.owner) && !group.admins.contains(invitor.username)) {
                throw new GroupException("400", "Only the owner and adminstrators can send invitations.");
            } else if (invitor.equals(invitee)) {
                throw new GroupException("402", "You can't invite yourself!");
            } else if (group.members.contains(invitee.username)) {
                throw new GroupException("403", "User already in group!");
            }
            group.members.add(invitee.username);
            mongoTemplate.save(group);
            invitee.joinedGroups.add(group);
            mongoTemplate.save(invitee);

            Set<String> memberSet = new HashSet<>(group.members);
            memberSet.add(group.owner);
            for (String admin : group.admins) {
                memberSet.add(admin);
            }
            for (String memberName : memberSet) {
                if (memberName.equals(invitee.username)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "Invitation");
                    jsonObject.put("username", invitor.username);
                    jsonObject.put("group", group);
                    SubscribeHandler.sendToUser(memberName, jsonObject.toJSONString());
                } else if (!memberName.equals(invitor.username)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "New Member");
                    jsonObject.put("username", invitee.username);
                    jsonObject.put("group", group.id);
                    SubscribeHandler.sendToUser(memberName, jsonObject.toJSONString());
                }
            }
        } catch (GroupException e) {
            throw e;
        } catch (Exception e) {
            throw new GroupException("401", "Failed to invite user due to: " + e.getMessage());
        }
    }

    public void quitGroup(User user, Group group) throws GroupException {
        System.out.println("quitGroup: " + user.username + ", " + group.name);
        try {
            user.joinedGroups.remove(group);
            for (FollowRecord followRecord : user.reminderConfig.followedUsers) {
                if (followRecord.groupId.equals(group.id)) {
                    user.reminderConfig.followedUsers.remove(followRecord);
                }
            }
            mongoTemplate.save(user);

            if (group.owner.equals(user.username)) {
                List<String> memberNames = new ArrayList<>();
                for (String memberName : group.members) {
                    memberNames.add(memberName); 
                }
                for (String adminName : group.admins) {
                    memberNames.add(adminName);
                }
                for (User member : accountService.findUserByUsername(memberNames)) {
                    member.joinedGroups.remove(group);
                    mongoTemplate.save(member);
                }
                mongoTemplate.remove(group);
            } else {
                group.members.remove(user.username);
                group.members.remove(user.username);
                mongoTemplate.save(group);
            }
        } catch (Exception e) {
            throw new GroupException("401", "Failed to quit group due to: " + e.getMessage());
        }
    }

    public void follow(User follower, User followee, Group group, int level) throws GroupException {
        System.out.println("follow: " + follower.username + ", " + followee.username + ", " +
                group.name + (level > 0 ? " (special interest)" : level == 0 ? " (normal)" : " (none)"));
        try {
            if (follower.equals(followee)) {
                throw new GroupException("400", "You can't follow yourself!");
            } else if (!followee.username.equals(group.owner) && !group.admins.contains(followee.username) &&
                    !group.members.contains(followee.username)) {
                throw new GroupException("401", "Followee not in group!");
            }
            FollowRecord newRecord = new FollowRecord(followee.username, group.id);
            if (level > 0) {
                follower.reminderConfig.particularInterests.add(newRecord);
                follower.reminderConfig.followedUsers.remove(newRecord);
                for (Memo memo : followee.createdMemos) {
                    follower.reminderConfig.notedMemos.remove(memo);
                    if (memo.group.equals(group.id)) {
                        follower.reminderConfig.emphasizedMemos.add(memo);
                    }
                }
            } else if (level == 0) {
                follower.reminderConfig.followedUsers.add(newRecord);
                follower.reminderConfig.particularInterests.remove(newRecord);
                for (Memo memo : followee.createdMemos) {
                    follower.reminderConfig.emphasizedMemos.remove(memo);
                    if (memo.group.equals(group.id)) {
                        follower.reminderConfig.notedMemos.add(memo);
                    }
                }
            } else {
                follower.reminderConfig.particularInterests.remove(newRecord);
                follower.reminderConfig.followedUsers.remove(newRecord);
                for (Memo memo : followee.createdMemos) {
                    follower.reminderConfig.emphasizedMemos.remove(memo);
                    follower.reminderConfig.notedMemos.remove(memo);
                }
            }
            mongoTemplate.save(follower);
            mongoTemplate.save(followee);
        } catch (GroupException e) {
            throw e;
        } catch (Exception e) {
            throw new GroupException("401", "Failed to follow user due to: " + e.getMessage());
        }
    }

    public Group findGroupById(String id) throws GroupException {
        System.out.println("findGroupById: " + id);
        Group group = mongoTemplate.findById(id, Group.class);
        if (group == null) {
            throw new GroupException("205", "Group not found.");
        } else {
            return group;
        }
    }

    public List<Group> findGroupById(List<String> ids) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        for (String id : ids) {
            criteria.orOperator(Criteria.where("username").is(id));
        }
        query.addCriteria(criteria);
        return mongoTemplate.find(query, Group.class);
    }

    public static class GroupException extends Exception {
        public String code;
        public String message;
        
        public GroupException(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
