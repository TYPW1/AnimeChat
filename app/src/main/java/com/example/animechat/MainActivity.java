package com.example.animechat;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.KeyEvent;


public class MainActivity extends AppCompatActivity {
    private EditText characterNameInput;
    private Button searchButton;
    private RecyclerView charactersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        characterNameInput = findViewById(R.id.character_name_input);
        searchButton = findViewById(R.id.search_button);
        charactersRecyclerView = findViewById(R.id.characters_recycler_view);

        characterNameInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String characterName = characterNameInput.getText().toString();
                    if (!TextUtils.isEmpty(characterName)) {
                        searchForCharacter(characterName);
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a character name.", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String characterName = characterNameInput.getText().toString();
                if (!TextUtils.isEmpty(characterName)) {
                    searchForCharacter(characterName);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a character name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchForCharacter(String characterName) {
        String url = "https://api.jikan.moe/v4/characters?q=" + characterName;
        JikanApiRequest request = new JikanApiRequest(new JikanApiRequest.JikanApiListener() {
            @Override
            public void onRequestCompleted(String result) {
                if (result != null) {
                    parseAndDisplayCharacters(result);
                } else {
                    Toast.makeText(MainActivity.this, "Error fetching character data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        request.execute(url);
    }

    private void parseAndDisplayCharacters(String json) {
        List<Character> characters = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject characterData = dataArray.getJSONObject(i);
                int mal_id = characterData.getInt("mal_id");
                String url = characterData.getString("url");
                String imageUrl = characterData.getJSONObject("images").getJSONObject("webp").getString("image_url");
                String name = characterData.getString("name");
                String about = characterData.getString("about");
                characters.add(new Character(mal_id, url, imageUrl, name, about));
            }

            if (characters.isEmpty()) {
                Toast.makeText(MainActivity.this, "No characters found with that name. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                CharacterAdapter characterAdapter = new CharacterAdapter(characters, new CharacterAdapter.OnCharacterClickListener() {
                    @Override
                    public void onCharacterClick(Character character) {
                        onCharacterSelected(character);
                    }
                });
                charactersRecyclerView.setAdapter(characterAdapter);
                charactersRecyclerView.setVisibility(View.VISIBLE); // Make the RecyclerView visible
            }
            } catch(Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error parsing character data.", Toast.LENGTH_SHORT).show();
            }

    }
    private void onCharacterSelected(Character character) {
        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
        chatIntent.putExtra("character", character);
        startActivity(chatIntent);
    }
}
