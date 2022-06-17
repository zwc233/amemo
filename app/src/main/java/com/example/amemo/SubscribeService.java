package com.example.amemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class SubscribeService extends Service {

    private SubscribeServiceBinder binder = new SubscribeServiceBinder();

    public WebSocketClient wsClient;

    public SubscribeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class SubscribeServiceBinder extends Binder {
        public SubscribeService getService() {
            return SubscribeService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        URI uri = URI.create(UrlUtils.makeWsUrl(CacheHandler.token));
        System.out.println("WebSocket uri is: " + uri.toString());
        wsClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake handshakeData) {
                System.out.println("WebSocket connection opened.");
            }

            @Override
            public void onMessage(String message) {
                System.out.println(message);
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String type = jsonObject.getString("type");
                    switch (type) {
                        case "New Memo":
                            JSONObject memo = jsonObject.getJSONObject("memo");
                            System.out.println("Memo " + memo.getString("title") +
                                    " created by " + memo.getString("creator") + ".");
                            CacheHandler.saveMemo(memo);
                            // TODO: schedule background reminders
                            break;
                        case "New Member":
                            System.out.println("User " + jsonObject.getString("username") +
                                    " has joined " + jsonObject.getString("group") + ".");
                            CacheHandler.Group group = CacheHandler.getGroup(jsonObject.getString("group"));
                            group.members.add(jsonObject.getString("username"));
                            break;
                        case "Invitation":
                            JSONObject groupObj = jsonObject.getJSONObject("group");
                            System.out.println("User " + jsonObject.getString("username") +
                                    " has invited you to group " + groupObj.getString("name") +
                                    "(" + groupObj.getString("id") + ").");
                            CacheHandler.saveGroup(groupObj);
                            CacheHandler.user.joinedGroups.add(groupObj.getString("id"));
                            break;
                    }
                    Intent intent = new Intent();
                    intent.setAction(type);
                    sendBroadcast(intent);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed due to: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("WebSocket exception: " + ex.getMessage());
            }
        };

        wsClient.connect();

        return START_STICKY;
    }
}