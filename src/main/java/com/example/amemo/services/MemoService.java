package com.example.amemo.services;

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
import com.example.amemo.services.AccountService.AccountException;
import com.example.amemo.websocket.SubscribeHandler;

@Service
public class MemoService {
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    AccountService accountService;

    public void createMemo(User user, Group group, Memo memo) throws MemoException {
        System.out.println("createMemo: " + user.username + ", " + group.name + ", " + memo.title);
        try {
            memo.creator = user.username;
            memo.group = group.id;
            mongoTemplate.save(memo);
            
            user.createdMemos.add(memo);
            mongoTemplate.save(user);
            
            group.memos.add(memo.id);
            mongoTemplate.save(group);

            Set<String> memberSet = new HashSet<>(group.members);
            memberSet.add(group.owner);
            for (String admin : group.admins) {
                memberSet.add(admin);
            }
            for (String memberName : memberSet) {
                if (!memberName.equals(user.username)) {
                    User member = accountService.getFullUserInfo(memberName);
                    FollowRecord followRecord = new FollowRecord(user.username, group.id);
                    if (member.reminderConfig.particularInterests.contains(followRecord)) {
                        member.reminderConfig.emphasizedMemos.add(memo);                
                    } else if (member.reminderConfig.followedUsers.contains(followRecord)) {
                        member.reminderConfig.notedMemos.add(memo);
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "New Memo");
                    jsonObject.put("memo", memo);
                    SubscribeHandler.sendToUser(memberName, jsonObject.toJSONString());
                }
            }
        } catch (Exception e) {
            throw new MemoException("401", "Failed to create memo due to: " + e.getMessage());
        }
    }

    public void deleteMemo(User user, Group group, Memo memo) throws MemoException {
        System.out.println("deleteMemo: " + user.username + ", " + group.name + memo.title);
        try {
            if (!memo.creator.equals(user.username)) {
                throw new MemoException("400", "Memo can only be deleted by its creator.");
            } else {
                user.createdMemos.remove(memo);
                user.reminderConfig.notedMemos.remove(memo);
                user.reminderConfig.emphasizedMemos.remove(memo);
                mongoTemplate.save(user);

                group.memos.remove(memo.id);
                mongoTemplate.save(group);

                Set<String> memberSet = new HashSet<>(group.members);
                memberSet.add(group.owner);
                for (String admin : group.admins) {
                    memberSet.add(admin);
                }
                for (String memberName : memberSet) {
                    if (!memberName.equals(user.username)) {
                        try {
                            User member = accountService.getFullUserInfo(memberName);
                            member.reminderConfig.notedMemos.remove(memo);
                            member.reminderConfig.emphasizedMemos.remove(memo);
                            mongoTemplate.save(member);

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type", "Delete Memo");
                            jsonObject.put("memo", memo);
                            SubscribeHandler.sendToUser(memberName, jsonObject.toJSONString());
                        } catch (AccountException e) {}
                    }
                }
            }

            mongoTemplate.remove(memo);
        } catch (MemoException e) {
            throw e;
        } catch (Exception e) {
            throw new MemoException("401", "Failed to delete memo due to: " + e.getMessage());
        }
    }

    public void noteMemo(User user, Memo memo, int level) throws MemoException {
        System.out.println("noteMemo: " + user.username + ", " + memo.title +
                (level > 0 ? " (emphasis)" : level == 0 ? " (normal)" : " (none)"));
        try {
            Boolean found = false;
            for (Group group : user.joinedGroups) {
                if (group.id.equals(memo.group)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new MemoException("400", "Users can only note memos created in groups they've joined.");
            }
            if (level > 0) {
                user.reminderConfig.emphasizedMemos.add(memo);
                user.reminderConfig.notedMemos.remove(memo);
            } else if (level == 0) {
                user.reminderConfig.notedMemos.add(memo);
                user.reminderConfig.emphasizedMemos.remove(memo);
            } else {
                user.reminderConfig.emphasizedMemos.remove(memo);
                user.reminderConfig.notedMemos.remove(memo);
            }
            mongoTemplate.save(user);
        } catch (MemoException e) {
            throw e;
        } catch (Exception e) {
            throw new MemoException("401", "Failed to create memo due to: " + e.getMessage());
        }
    }

    public Memo findMemoById(String id) throws MemoException {
        Memo memo = mongoTemplate.findById(id, Memo.class);
        if (memo == null) {
            throw new MemoException("205", "Memo not found. It may have been deleted.");
        } else {
            return memo;
        }
    }

    public List<Memo> findMemoById(List<String> ids) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        for (String id : ids) {
            criteria.orOperator(Criteria.where("username").is(id));
        }
        query.addCriteria(criteria);
        return mongoTemplate.find(query, Memo.class);
    }

    public static class MemoException extends Exception {
        public String code;
        public String message;

        public MemoException(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
