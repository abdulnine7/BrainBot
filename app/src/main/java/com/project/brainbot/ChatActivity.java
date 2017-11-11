package com.project.brainbot;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ChatActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    protected RecyclerView mMessageRecycler;
    protected MessageListAdapter mMessageAdapter;
    protected List<Message> messageList;
    public boolean global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = MyJSON.getMessageList(this, global);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

        final Button send = (Button) findViewById(R.id.button_chatbox_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView text = (TextView) findViewById(R.id.edittext_chatbox);
                String s = text.getText().toString();
                text.setText("");
                mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount());

                if (!Objects.equals(s, "")) {
                    sendRequest(s);
                    Message my_message = makeMyMessage(s);
                    updateView(my_message, true);
                    MyJSON.writeFile(my_message, global);
                    if (global)
                        sendPush(my_message);
                }
            }
        });

        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startVoiceInput();
                return false;
            }
        });

        mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, I am BrainBot\n How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    TextView text = (TextView) findViewById(R.id.edittext_chatbox);
                    text.setText(result.get(0));
                }
                break;
            }

        }
    }

    protected void updateView(Message message, boolean self) {
        messageList.add(message);
        mMessageAdapter.updateNotify();
        mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);

        if (!self)
            playTone();
    }

    public int sendRequest(final String s) {

        String url = getRequestURL(s);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Response", response);
                parseXML(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        });

        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        return 0;
    }

    public String getRequestURL(String message) {
        String url;
        String string_message = message.replace(" ", "+");

        url = Constants.HOST + Constants.PATH + "application=" + Constants.API_KEY +
                "&instance=" + Constants.BOT_ID + "&message=" + string_message;

        if (!Objects.equals(Constants.CONVERSATION_ID, ""))
            url = url + "&conversation=" + Constants.CONVERSATION_ID;

        //Log.d("URL ::", url);
        return url;
    }

    public void parseXML(String response) {
        String text_message;
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response));
            Document doc = dBuilder.parse(is);

            doc.getDocumentElement().normalize();

            Element eElement = doc.getDocumentElement();
            Constants.setConversationId(eElement.getAttribute("conversation"));
            //Log.d("CONVERSATION ::", eElement.getAttribute("conversation"));

            text_message = eElement.getElementsByTagName("message").item(0).getTextContent();

            Message brainbot_message = makeBBMessage(text_message);
            updateView(brainbot_message, false);
            MyJSON.writeFile(brainbot_message, global);

            if (global)
                sendPush(brainbot_message);


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    private Message makeBBMessage(String text_message) {
        Message message = new Message();
        message.message = text_message;
        message.createdAt = System.currentTimeMillis();
        message.sender.setNickname("BrainBot");
        message.sender.setProfileUrl("www.google.com/brainbot");
        return message;
    }

    private Message makeMyMessage(String text_message) {
        Message message = new Message();
        message.message = text_message;
        message.createdAt = System.currentTimeMillis();
        message.sender.setNickname("self");
        message.sender.setProfileUrl("www.google.com/self");
        return message;
    }

    public void playTone() {

        try {
            AssetFileDescriptor descriptor = getAssets().openFd("update_tone.wav");
            MediaPlayer player = new MediaPlayer();

            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();

            player.setDataSource(descriptor.getFileDescriptor(), start, end);
            player.prepare();

            player.setVolume(1.0f, 1.0f);
            player.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void sendPush(Message message) {
    }
}