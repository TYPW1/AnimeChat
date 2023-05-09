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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class ChatGPTService {
    private static final String API_KEY = "";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    private String characterName;

    private List<Message> messageHistory;


    private String insertMultipleEmojis(String text, String[] selectedEmojis, int numberOfEmojis) {
        Random random = new Random();
        for (int i = 0; i < numberOfEmojis; i++) {
            int randomIndex = random.nextInt(selectedEmojis.length);
            String emoji = selectedEmojis[randomIndex];

            int insertPosition;
            do {
                insertPosition = random.nextInt(text.length() + 1);
            } while (insertPosition > 0 && text.charAt(insertPosition - 1) != ' ');

            text = text.substring(0, insertPosition) + emoji + text.substring(insertPosition);
        }
        return text;
    }


    private String insertEmojiBasedOnExpression(String text) {
        String[] positiveEmojis = {"😀", "😄", "😆", "😊"};
        String[] negativeEmojis = {"😞", "😔", "😢", "😩"};
        String[] neutralEmojis = {"😐", "😶", "😑", "🙂"};
        String[] surpriseEmojis = {"😲", "😯", "😧", "😮"};
        String[] fallbackEmojis = {"😀"};

        String[] positiveKeywords = {"happy", "excited", "glad", "joy"};
        String[] negativeKeywords = {"sad", "angry", "upset", "disappointed"};
        String[] neutralKeywords = {"neutral", "calm", "okay"};
        String[] surpriseKeywords = {"surprised", "shocked", "amazed"};

        String[] selectedEmojis = null;

        for (String keyword : positiveKeywords) {
            if (text.toLowerCase().contains(keyword)) {
                selectedEmojis = positiveEmojis;
                break;
            }
        }

        if (selectedEmojis == null) {
            for (String keyword : negativeKeywords) {
                if (text.toLowerCase().contains(keyword)) {
                    selectedEmojis = negativeEmojis;
                    break;
                }
            }
        }

        if (selectedEmojis == null) {
            for (String keyword : neutralKeywords) {
                if (text.toLowerCase().contains(keyword)) {
                    selectedEmojis = neutralEmojis;
                    break;
                }
            }
        }

        if (selectedEmojis == null) {
            for (String keyword : surpriseKeywords) {
                if (text.toLowerCase().contains(keyword)) {
                    selectedEmojis = surpriseEmojis;
                    break;
                }
            }
        }

        if (selectedEmojis == null) {
            selectedEmojis = fallbackEmojis;
        }

        Random random = new Random();
        int numberOfEmojis = random.nextInt(4) + 1; // Randomly choose between 1 and 4 emojis
        return insertMultipleEmojis(text, selectedEmojis, numberOfEmojis);
    }



    public void setCharacter(String characterName) {
        this.characterName = characterName;
    }

    public void addToMessageHistory(Message message) {
        messageHistory.add(message);
    }


    public ChatGPTService() {
        client = new OkHttpClient();
        messageHistory = new ArrayList<>();
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

        for (Message msg : messageHistory) {
            JSONObject historyMessage = new JSONObject();
            try {
                historyMessage.put("role", msg.isUser() ? "user" : "assistant");
                historyMessage.put("content", msg.getText());
                messages.put(historyMessage);
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
            requestBody.put("max_tokens", 1000); // Limit the response length
            requestBody.put("temperature", 0.7); // Control the randomness of the output
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

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject choice = choices.getJSONObject(0);
            JSONObject responseMessage = choice.getJSONObject("message");
            String content = responseMessage.getString("content");

            if (new Random().nextInt(100) < 50) { // 20% chance of inserting an emoji
                content = insertEmojiBasedOnExpression(content);
            }

            return content;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
