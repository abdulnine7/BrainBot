package com.project.brainbot;

import android.content.Context;
import android.os.Environment;
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

    public static List<Message> getMessageList(Context context) {

        messageList = new ArrayList<>();
        String file = "[" + readFromFile() + "]";
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
            //Log.d("SIZE :", String.valueOf(messageList.size()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageList;
    }


    private static String readFromFile() {

        //Get the text file
        File file = new File(Environment.getExternalStorageDirectory().getPath()
                + "/BrainBot/", "messages.json");

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

    public static boolean writeFile(Message message, Context context) {

        BufferedWriter bw;
        FileWriter fw;
        File folder = new File(Environment.getExternalStorageDirectory().getPath(), "BrainBot");
        boolean check = folder.mkdirs();
        Log.e("Folder created :", String.valueOf(check));

        File file = new File(Environment.getExternalStorageDirectory().getPath()
                + "/BrainBot/", "messages.json");

        try {

            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            //Creating JSON Object to write to file
            JSONObject json_message = new JSONObject();
            json_message.put("message", message.message);
            json_message.put("createdAt", message.createdAt);

            JSONArray json_sender = new JSONArray();
            json_sender.put(message.sender.getNickname());
            json_sender.put(message.sender.getProfileUrl());
            json_message.put("sender", json_sender);

            if (readFromFile() == "")
                bw.write(json_message.toString());
            else
                bw.write("," + json_message.toString());

            bw.close();
            fw.close();
            return true;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String readFromAssetFile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }
}
