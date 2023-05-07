package com.example.animechat;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTService {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/engines/davinci-codex/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;

    public ChatGPTService() {
        client = new OkHttpClient();
    }

    public String getChatResponse(String prompt) {
        String json = "{ \"prompt\": \"" + prompt + "\", \"max_tokens\": 100, \"temperature\": 0.7, \"n\": 1 }";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
