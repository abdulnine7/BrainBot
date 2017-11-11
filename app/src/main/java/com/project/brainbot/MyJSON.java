package com.project.brainbot;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MyJSON {
    static List<Message> messageList;

    public static List<Message> getMessageList(Context context, boolean global) {

        messageList = new ArrayList<>();
        String file = "[" + readFromFile(global) + "]";
        Log.d("FILE :", file);

        try {
            JSONArray my_messages = new JSONArray(file);
            for (int i = 0; i < my_messages.length(); i++) {
                Message message = new Message();
                JSONObject json_message = my_messages.getJSONObject(i);
                message.message = json_message.getString("message");
                message.createdAt = json_message.getLong("createdAt");
                message.sender.setNickname(json_message.getJSONArray("sender").getString(0));
                message.sender.setProfileUrl(json_message.getJSONArray("sender").getString(1));

                messageList.add(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageList;
    }

    public static Message getMessage(String json) throws JSONException {
        Message message = new Message();
        JSONObject json_message = new JSONObject(json);
        message.message = json_message.getString("message");
        message.createdAt = json_message.getLong("createdAt");
        message.sender.setNickname(json_message.getJSONArray("sender").getString(0));
        message.sender.setProfileUrl(json_message.getJSONArray("sender").getString(1));
        return message;
    }

    public static String getMessage(Message message) throws JSONException {
        JSONObject json_message = new JSONObject();
        json_message.put("message", message.message);
        json_message.put("createdAt", message.createdAt);

        JSONArray json_sender = new JSONArray();
        json_sender.put(message.sender.getNickname());
        json_sender.put(message.sender.getProfileUrl());
        json_message.put("sender", json_sender);

        return json_message.toString();
    }


    private static String readFromFile(boolean global) {

        File file;
        //Get the text file
        if (global) {
            file = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/BrainBot/", "global_messages.json");
        } else {
            file = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/BrainBot/", "messages.json");
        }

        StringBuilder result = new StringBuilder();

        try {
            if (!file.exists())
                file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(file));
            result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                result.append(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static boolean writeFile(Message message, boolean global) {

        BufferedWriter bw;
        FileWriter fw;
        File folder = new File(Environment.getExternalStorageDirectory().getPath(), "BrainBot");
        boolean check = folder.mkdirs();
        File file;
        Log.e("Folder created :", String.valueOf(check));

        if (global) {
            file = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/BrainBot/", "global_messages.json");
        } else {
            file = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/BrainBot/", "messages.json");
        }

        try {

            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);


            if (readFromFile(global) == "")
                bw.write(getMessage(message));
            else
                bw.write("," + getMessage(message));

            bw.close();
            fw.close();
            return true;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
