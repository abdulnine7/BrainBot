package com.project.brainbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class GlobalChatRoomActivity extends ChatActivity {

    static GlobalChatRoomActivity.MessageUpdateReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.global = true;
        receiver = new MessageUpdateReceiver();
        registerReceiver(receiver, new IntentFilter("message_update"));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void sendPush(Message message) {
        final String data;

        try {
            data = MyJSON.getMessage(message)
                    .replace("[\"self\"", "[\"" + SharedPrefManager.getInstance(this).getLoginDetails()[0] + "\"");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_HOST + Constants.URL_SEND_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("PUSH RESPONSE :", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PUSH ERROR :", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", "BrainBot");
                params.put("message", data);
                params.put("token", SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken());
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    public class MessageUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json_message = intent.getStringExtra("message_json");
            Log.d("Broadcasted Message :", json_message);
            Message message;
            try {
                message = MyJSON.getMessage(json_message);
                updateView(message, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}