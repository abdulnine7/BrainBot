package com.project.brainbot;

/**
 * Created by qwerty on 2/10/17.
 */

public final class Constants {

    public static final String HOST = "https://www.botlibre.com";
    public static final String PATH = "/rest/api/form-chat?";
    public static final int BOT_ID = 165;
    public static final String API_KEY = "8912163173704478096";
    public static String CONVERSATION_ID = "";

    public static void setConversationId(String conversationId) {
        CONVERSATION_ID = conversationId;
    }

    //Our Server constants
    public static final String URL_REGISTRATION = "192.168.1.18/brainbot/Register.php";
}
