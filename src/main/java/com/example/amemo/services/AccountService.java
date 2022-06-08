package com.example.amemo.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.amemo.models.User;
import com.example.amemo.websocket.SubscribeHandler;

@Service
public class AccountService {
    
    @Autowired
    MongoTemplate mongoTemplate;

    HashMap<String, SessionRecord> token2User = new HashMap<>();
    HashMap<String, String> user2Token = new HashMap<>();

    static class SessionRecord {
        String username;
        Date lastSession;

        public SessionRecord(String username, Date lastSession) {
            this.username = username;
            this.lastSession = lastSession;
        }
    }

    public String validate(String token) throws AccountException {
        System.out.println("validate: " + token);
        SessionRecord sessionRecord = token2User.get(token);
        if (sessionRecord == null) {
            throw new AccountException("400", "Token has expired. Please sign in again.");
        } else if (!user2Token.get(sessionRecord.username).equals(token)) {
            token2User.remove(token);
            throw new AccountException("401", "Account had signed in on another device.");
        } else {
            sessionRecord.lastSession = new Date();
            return sessionRecord.username;
        }
    }

    public String signIn(String username, String password) throws AccountException {
        System.out.println("signIn: " + username);
        Query query = new Query(Criteria.where("username").is(username))
                            .addCriteria(Criteria.where("password").is(password));        
        if (mongoTemplate.findOne(query, User.class) == null) {
            throw new AccountException("400", "Incorrect username or password.");
        } else {
            String token = UUID.randomUUID().toString();
            token2User.put(token, new SessionRecord(username, new Date()));
            
            String oldToken = user2Token.get(username);
            if (oldToken != null) {
                token2User.remove(oldToken);
            }
            user2Token.put(username, token);
            
            return token;
        }
    }

    public void signUp(User user) throws AccountException {
        System.out.println("signUp: " + user.username);
        Query query = new Query(Criteria.where("username").is(user.username));
        if (mongoTemplate.findOne(query, User.class) == null) {
            mongoTemplate.save(user);
        } else {
            throw new AccountException("400", "Username already exists. Great minds think alike.");
        }
    }

    public void changePassword(String username, String oldPasswd, String newPasswd) throws AccountException {
        System.out.println("changePassword: " + username);
        Query query =  new Query(Criteria.where("username").is(username))
                            .addCriteria(Criteria.where("password").is(oldPasswd));
        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            throw new AccountException("400", "Incorrect username or password.");
        } else {
            user.password = newPasswd;
            mongoTemplate.save(user);
        }
    }

    public User getPersonalInfo(String username) throws AccountException {
        System.out.println("getPersonalInfo: " + username);
        Query query = new Query(Criteria.where("username").is(username));
        query.fields().exclude("id").exclude("password");
        query.fields().exclude("id", "password");
        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            throw new AccountException("205", "User not found. Account have been deactivated.");
        } else {
            return user;
        }
    }

    public User findUserByUsername(String username) throws AccountException {
        Query query = new Query(Criteria.where("username").is(username));
        query.fields().exclude("id").exclude("password");
        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            throw new AccountException("205", "User not found. Account have been deactivated.");
        } else {
            return user;
        }
    }

    public List<User> findUserByUsername(List<String> usernames) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        for (String username : usernames) {
            criteria.orOperator(Criteria.where("username").is(username));
        }
        query.addCriteria(criteria);
        return mongoTemplate.find(query, User.class);
    }

    public void updateToken(String user) {
        try {
            String token = user2Token.get(user);
            SessionRecord sessionRecord = token2User.get(token);
            sessionRecord.lastSession = new Date();
        } catch (Exception e) {}
    }

    public AccountService() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(() -> {
            final long interval = TimeUnit.DAYS.toMillis(1) * 7;  // clean up tokens every week

            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Token pool cleaner thread interrupted.");
                    return;
                }

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Iterator<Map.Entry<String, SessionRecord>> it = token2User.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, SessionRecord> entry = it.next();
                    if (System.currentTimeMillis() - entry.getValue().lastSession.getTime() > interval) {
                        System.out.println("No interaction with user " + entry.getValue().username +
                            " for a long time. Removing the corresponding token.");
                        user2Token.remove(entry.getValue().username);
                        SubscribeHandler.removeSession(entry.getValue().username);
                        it.remove();
                    }
                }
            }
        });
    }

    public static class AccountException extends Exception {
        public String code;
        public String message;

        public AccountException(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
