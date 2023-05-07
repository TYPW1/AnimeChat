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

        chatMessages = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setAdapter(chatAdapter);

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

    private void sendMessage(final String messageText) {
        chatMessages.add(new Message(messageText, true));
        chatInput.setText("");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String response = chatGPTService.getChatResponse(messageText);
                if (response != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        JSONObject choice = choices.getJSONObject(0);
                        JSONObject messageObj = choice.getJSONObject("message");
                        String reply = messageObj.getString("content");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatMessages.add(new Message(reply, false));
                                chatRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessages.add(new Message("Error: Could not get a response from the API.", false));
                            chatRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

}
