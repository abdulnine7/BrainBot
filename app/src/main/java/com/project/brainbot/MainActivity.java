package com.project.brainbot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        Button chat = (Button) findViewById(R.id.chat_button);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });

        Button global_chat = (Button) findViewById(R.id.global_chat_button);
        global_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GlobalChatRoomActivity.class);
                startActivity(i);
            }
        });

        String name = SharedPrefManager.getInstance(this).getLoginDetails()[0];
        String mail = SharedPrefManager.getInstance(this).getLoginDetails()[1];

        TextView logged_in_details = (TextView) findViewById(R.id.signed_in_mail);
        logged_in_details.setText("\uD83D\uDC68 : " + name +"\n\uD83D\uDCE7 : " + mail); //Emoji code
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
