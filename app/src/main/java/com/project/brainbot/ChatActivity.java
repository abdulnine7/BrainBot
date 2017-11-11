package com.project.brainbot;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = MyJSON.getMessageList(this);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

        Button send = (Button) findViewById(R.id.button_chatbox_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView text = (TextView) findViewById(R.id.edittext_chatbox);
                String s = text.getText().toString();
                text.setText("");
                mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount());

                if (!Objects.equals(s, "")) {
                    sendRequest(s);
                    updateView(s, true);
                }
            }
        });
    }

    private void updateView(String text_message, boolean self) {
        Message m = new Message();
        m.message = text_message;
        m.createdAt = System.currentTimeMillis();
        if (self) {
            m.sender.setNickname("self");
            m.sender.setProfileUrl("www.google.com/self");
        } else {
            m.sender.setNickname("BrainBot");
            m.sender.setProfileUrl("www.google.com/brainbot");
        }
        messageList.add(m);
        MyJSON.writeFile(m, this);
        mMessageAdapter.updateNotify();
        mMessageRecycler.scrollBy(0, 500);
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
            updateView(text_message, false);
            playTone();


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

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
}