package com.example.animechat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {
    private List<Character> characters;
    private OnCharacterClickListener onCharacterClickListener;

    public CharacterAdapter(List<Character> characters, OnCharacterClickListener onCharacterClickListener) {
        this.characters = characters;
        this.onCharacterClickListener = onCharacterClickListener;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_item, parent, false);
        return new CharacterViewHolder(view, onCharacterClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characters.get(position);
        holder.characterName.setText(character.getName());
        holder.characterAbout.setText(character.getAbout());
        Picasso.get().load(character.getImageUrl()).into(holder.characterImage);
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView characterImage;
        TextView characterName;

        TextView characterAbout;
        OnCharacterClickListener onCharacterClickListener;

        public CharacterViewHolder(@NonNull View itemView, OnCharacterClickListener onCharacterClickListener) {
            super(itemView);
            characterImage = itemView.findViewById(R.id.characterImage);
            characterName = itemView.findViewById(R.id.characterName);
            characterAbout = itemView.findViewById(R.id.characterAbout);
            this.onCharacterClickListener = onCharacterClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCharacterClickListener.onCharacterClick(characters.get(getAdapterPosition()));
        }
    }

    public interface OnCharacterClickListener {
        void onCharacterClick(Character character);
    }
}

