package com.project.brainbot;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "RegInfo";
    private static final String TAG_TOKEN = "token";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PASSWORD = "password";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    public boolean saveLoginDetails(String name, String email, String password) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_NAME, name);
        editor.putString(TAG_EMAIL, email);
        editor.putString(TAG_PASSWORD, password);
        editor.apply();
        return true;
    }

    public boolean setRegistered(boolean status) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Registered", String.valueOf(status));
        editor.apply();
        return true;
    }

    public boolean isRegistered() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String s = sharedPreferences.getString("Registered", null);
        return Boolean.parseBoolean(s);
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_TOKEN, null);
    }

    public String[] getLoginDetails() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String[] details = new String[3];
        details[0] = sharedPreferences.getString(TAG_NAME, null);
        details[1] = sharedPreferences.getString(TAG_EMAIL, null);
        details[2] = sharedPreferences.getString(TAG_PASSWORD, null);

        return details;
    }
}
