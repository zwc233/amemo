package com.example.amemo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.amemo.models.Group;
import com.example.amemo.models.User;
import com.example.amemo.models.User.ReminderConfig.FollowRecord;

@Service
public class GroupService {
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    AccountService accountService;

    public void createGroup(User user, Group group) throws GroupException {
        try {
            group.owner = user.username;
            mongoTemplate.save(group);
            user.joinedGroups.add(group);
            mongoTemplate.save(user);
        } catch (Exception e) {
            throw new GroupException("401", "Failed to create group due to: " + e.getMessage());
        }
    }

    public void inviteToGroup(User invitor, User invitee, Group group) throws GroupException {
        try {
            if (!invitor.username.equals(group.owner) && !group.admins.contains(invitor.username)) {
                throw new GroupException("400", "Only the owner and adminstrators can send invitations.");
            }
            group.members.add(invitee.id);
            mongoTemplate.save(group);
            invitee.joinedGroups.add(group);
            mongoTemplate.save(invitee);
        } catch (Exception e) {
            throw new GroupException("401", "Failed to invite user due to: " + e.getMessage());
        }
    }

    public void quitGroup(User user, Group group) throws GroupException {
        try {
            user.joinedGroups.remove(group);
            mongoTemplate.save(user);
            if (group.owner.equals(user.username)) {
                List<String> memberIds = new ArrayList<>();
                for (String memberId : group.members) {
                    memberIds.add(memberId); 
                }
                for (String adminId : group.admins) {
                    memberIds.add(adminId);
                }
                for (User member : accountService.findUserByUsername(memberIds)) {
                    member.joinedGroups.remove(group);
                    mongoTemplate.save(member);
                }
                mongoTemplate.remove(group);
            } else {
                group.members.remove(user.id);
                group.members.remove(user.id);
                mongoTemplate.save(group);
            }
        } catch (Exception e) {
            throw new GroupException("401", "Failed to quit group due to: " + e.getMessage());
        }
    }

    public void follow(User follower, User followee, Group group, Boolean specialInterest) throws GroupException {
        try {
            if (specialInterest) {
                follower.reminderConfig.paticularInterests.add(new FollowRecord(followee.id, group.id));
            } else {
                follower.reminderConfig.followedUsers.add(new FollowRecord(followee.id, group.id));
            }
            mongoTemplate.save(follower);
        } catch (Exception e) {
            throw new GroupException("401", "Failed to follow user due to: " + e.getMessage());
        }
    }

    public Group findGroupById(String id) throws GroupException {
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
