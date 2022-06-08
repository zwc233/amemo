package com.example.amemo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.amemo.models.Group;
import com.example.amemo.models.Memo;
import com.example.amemo.models.User;

@Service
public class MemoService {
    
    @Autowired
    MongoTemplate mongoTemplate;

    public void createMemo(User user, Group group, Memo memo) throws MemoException {
        try {
            memo.creator = user.id;
            memo.group = group.id;
            mongoTemplate.save(memo);
            user.createdMemos.add(memo);
            mongoTemplate.save(user);
        } catch (Exception e) {
            throw new MemoException("401", "Failed to create memo due to: " + e.getMessage());
        }
    }

    public void deleteMemo(User user, Group group, Memo memo) throws MemoException {
        ;
    }

    public void noteMemo(User user, Memo memo, Boolean emphasis) throws MemoException {
        try {
            mongoTemplate.save(memo);
            if (emphasis) {
                user.reminderConfig.emphasizedMemos.add(memo);
            } else {
                user.reminderConfig.notedMemos.add(memo);
            }
            mongoTemplate.save(user);
        } catch (Exception e) {
            throw new MemoException("401", "Failed to create memo due to: " + e.getMessage());
        }
    }

    public void unnoteMemo(User user, Memo memo) throws MemoException {
        try {
            user.reminderConfig.emphasizedMemos.remove(memo);
            user.reminderConfig.notedMemos.remove(memo);
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
