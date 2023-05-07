package com.example.animechat;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText chatInput;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatInput = findViewById(R.id.chat_input);
        sendButton = findViewById(R.id.send_button);

        // Implement your chat functionality with GPT API
    }
}
