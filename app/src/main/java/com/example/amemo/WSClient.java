package com.example.amemo;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class WSClient extends WebSocketClient {

    public WSClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Log.e("WSClient", "connection opened.");
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.getString("type");
            if (type.equals("Create Memo")) {
                JSONObject memo = jsonObject.getJSONObject("memo");
                Log.e("WSClient", "memo " + memo.getString("title") +
                        " created by " + memo.getString("creator") + ".");
                // TODO: schedule background reminders
            } else if (type.equals("Delete Memo")) {
                JSONObject memo = jsonObject.getJSONObject("memo");
                Log.e("WSClient", "memo " + memo.getString("title") + "deleted.");
                // TODO: re-schedule background reminders
            }
        } catch (JSONException e) {
            Log.e("WSClient", "failed to parse json:", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("WSClient", "connection closed due to: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("WSClient", "encountered an exception: ", ex);
    }
}
