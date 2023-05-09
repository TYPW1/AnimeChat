package com.example.animechat;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentConversationsHelper {
    private static final String PREFERENCES_FILE = "animechat_recent_conversations";
    private static final String RECENT_CONVERSATIONS_KEY = "recent_conversations";

    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public RecentConversationsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public void saveRecentConversation(Character character) {
        List<Character> characters = getRecentConversations();
        // Remove the character if it already exists in the list (to avoid duplicates)
        characters.remove(character);
        // Add the character to the start of the list
        characters.add(0, character);
        // Convert the list to JSON and save it
        String json = gson.toJson(characters);
        sharedPreferences.edit().putString(RECENT_CONVERSATIONS_KEY, json).apply();
    }

    public List<Character> getRecentConversations() {
        String json = sharedPreferences.getString(RECENT_CONVERSATIONS_KEY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Character>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            // Return an empty list if there are no recent conversations
            return new ArrayList<>();
        }
    }
}
