package com.example.animechat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTService {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    private String characterName;

    public void setCharacter(String characterName) {
        this.characterName = characterName;
    }


    public ChatGPTService() {
        client = new OkHttpClient();
    }

    public String getChatResponse(String message) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();



        JSONArray messages = new JSONArray();

        if (characterName != null) {
            JSONObject systemMessage = new JSONObject();
            try {
                systemMessage.put("role", "system");
                systemMessage.put("content", "You are " + characterName + ". " +
                                                        "Act exactly like the character. " +
                                                        "use all expressions exactly as the character.");
                messages.put(systemMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject userMessage = new JSONObject();
        try {
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, requestBody.toString());
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
