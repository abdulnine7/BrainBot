package com.project.brainbot;

public final class Constants {

    public static final String HOST = "https://www.botlibre.com";
    public static final String PATH = "/rest/api/form-chat?";
    public static final int BOT_ID = 165;
    public static final String API_KEY = ""; // INSERT YOUR OWN API KEY HERE
    public static String CONVERSATION_ID = "";

    public static void setConversationId(String conversationId) {
        CONVERSATION_ID = conversationId;
    }

    //Our Server constants
    // LOCAL    :: "http://192.168.1.8"
    // ONLINE   :: "https://abdullah.000webhostapp.com"

    public static String MY_HOST = "https://abdullah.000webhostapp.com";
    public static final String URL_REGISTRATION = "/brainbot/Register.php";
    public static final String URL_VERIFICATION = "/brainbot/Verify.php";
    public static final String URL_SEND_PUSH = "/brainbot/sendMultiplePush.php";
}
