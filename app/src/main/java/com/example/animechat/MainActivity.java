package com.example.animechat;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.KeyEvent;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    private EditText characterNameInput;
    private Button searchButton;
    private RecyclerView charactersRecyclerView;
    private NavigationView navigationView;
    private RecentConversationsHelper recentConversationsHelper;
    private ImageView noResultImage;
    private TextView noResultText;
    private CharacterAdapter characterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DrawerLayout
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        // Initialize NavigationView
        navigationView = findViewById(R.id.nav_view);
        recentConversationsHelper = new RecentConversationsHelper(this);
        characterNameInput = findViewById(R.id.character_name_input);
        searchButton = findViewById(R.id.search_button);
        charactersRecyclerView = findViewById(R.id.characters_recycler_view);
        noResultImage = findViewById(R.id.no_result_image);
        noResultText = findViewById(R.id.no_result_text);




        // Set item click listener for NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Get the list of recent conversations
                List<Character> characters = recentConversationsHelper.getRecentConversations();
                for (Character character : characters) {
                    // If the clicked item's ID matches a character's mal_id, navigate to the chat activity
                    if (menuItem.getItemId() == character.getMal_id()) {
                        onCharacterSelected(character);
                        break;
                    }
                }

                // Close the navigation drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }


        });

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
                    noResultImage.setVisibility(View.GONE);
                    noResultText.setVisibility(View.GONE);
                    searchForCharacter(characterName);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a character name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavigationDrawer();
    }

    // This method updates the NavigationView's menu items based on the recent conversations
    private void updateNavigationDrawer() {
        List<Character> characters = recentConversationsHelper.getRecentConversations();
        Menu menu = navigationView.getMenu();
        menu.clear(); // Remove the old items
        for (int i = 0; i < characters.size(); i++) {
            Character character = characters.get(i);
            // Use the character's mal_id as the item ID, and the character's name as the item title
            menu.add(Menu.NONE, character.getMal_id(), Menu.NONE, character.getName()).setIcon(R.drawable.baseline_recent_actors_24); // Replace ic_recent_conversation with your drawable resource
        }
    }

    private void searchForCharacter(String characterName) {
        String url = "https://api.jikan.moe/v4/characters?q=" + characterName;
        JikanApiRequest request = new JikanApiRequest(new JikanApiRequest.JikanApiListener() {
            @Override
            public void onRequestCompleted(String result) {
                if (result != null) {
                    parseAndDisplayCharacters(result);
                } else {
                    Toast.makeText(MainActivity.this, "Error fetching character data. Please verify your internet connection", Toast.LENGTH_SHORT).show();
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
                noResultImage.setVisibility(View.VISIBLE);
                noResultText.setVisibility(View.VISIBLE);
                charactersRecyclerView.setVisibility(View.GONE);
            } else {
                noResultImage.setVisibility(View.GONE);
                noResultText.setVisibility(View.GONE);
                charactersRecyclerView.setAdapter(new CharacterAdapter(characters, new CharacterAdapter.OnCharacterClickListener() {
                    @Override
                    public void onCharacterClick(Character character) {
                        onCharacterSelected(character);
                    }
                }));
                charactersRecyclerView.setVisibility(View.VISIBLE); // Make the RecyclerView visible
            }
            } catch(Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error parsing character data.", Toast.LENGTH_SHORT).show();
            }

    }
    private void onCharacterSelected(Character character) {
        recentConversationsHelper.saveRecentConversation(character);
        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
        chatIntent.putExtra("character", character);
        startActivity(chatIntent);
    }


}
