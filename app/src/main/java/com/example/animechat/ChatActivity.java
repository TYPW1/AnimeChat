package com.example.animechat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.KeyEvent;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText chatInput;
    private Button sendButton;
    private List<Message> chatMessages;

    private ChatGPTService chatGPTService;

    private Character character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        chatGPTService = new ChatGPTService();

        character = (Character) getIntent().getSerializableExtra("character");
        chatGPTService.setCharacter(character.getName());


        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatInput = findViewById(R.id.chat_input);
        sendButton = findViewById(R.id.send_button);

        chatInput.requestFocus();

        chatMessages = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setAdapter(chatAdapter);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chatInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String messageText = chatInput.getText().toString();
                    if (!messageText.isEmpty()) {
                        sendMessage(messageText);
                        chatInput.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = chatInput.getText().toString();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        chatInput.requestFocus();
    }


    private void sendMessage(final String messageText) {
        chatMessages.add(new Message(messageText, true));
        chatInput.setText("");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String response = chatGPTService.getChatResponse(messageText);
                if (response != null) {
                    final String reply = response;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.add(new Message(reply, false));
                            chatRecyclerView.getAdapter().notifyDataSetChanged();
                            scrollToBottom();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.add(new Message("Error: Could not get a response from the API.", false));
                            chatRecyclerView.getAdapter().notifyDataSetChanged();
                            scrollToBottom();
                        }
                    });
                }
            }
        });
        scrollToBottom();
    }
    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }
}
