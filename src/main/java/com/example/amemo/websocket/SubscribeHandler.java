package com.example.amemo.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.amemo.services.AccountService;
import com.example.amemo.services.AccountService.AccountException;

@Controller
@ServerEndpoint("/subscribe/{token}")
public class SubscribeHandler {
    
    private static Map<String, Session> user2Session = new ConcurrentHashMap<>();

    static AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        SubscribeHandler.accountService = accountService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        try {
            String username = accountService.validate(token);

            if (user2Session.containsKey(username)) {
                System.out.println("WebSocket: closing old session with " + username + ".");
                try {
                    user2Session.get(username).close();
                } catch (IOException e) {}
            }
            user2Session.put(username, session);

            System.out.println("WebSocket: set up connection with " + username + ".");
        } catch (AccountException e) {
            System.out.println("WebSocket: attempting to set up connection with invalid token.");
            try {
                session.close();
            } catch (IOException ie) {}
        }
    }

    @OnClose
    public void onClose(@PathParam("token") String token) {
        try {
            user2Session.remove(accountService.validate(token));
        } catch (AccountException e) {
            System.out.println("WebSocket: token has expired.");
        }
    }

    public static void sendMessage(Session session, String message) throws IOException, EncodeException {
        if (session != null) {
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }
 
    public static void sendToUser(String username, String message) {
        Session session = user2Session.get(username);
        try {
            sendMessage(session, message);
            System.out.println("WebSocket: sent to " + username + ": " + message);
        } catch (Exception e) {
            System.out.println("WebSocket: failed to send message to " + username + " due to: " + e.getMessage());
        }
    }

    public static void removeSession(String username) {
        try {
            user2Session.get(username).close();
        } catch (Exception e) {}
        user2Session.remove(username);
    }
}
