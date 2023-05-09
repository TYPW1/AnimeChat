package com.example.animechat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.Gravity;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getText());

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.messageText.getLayoutParams();

        if (message.isUser()) {
            holder.messageText.setBackgroundResource(R.drawable.user_message_background);
            layoutParams.gravity = Gravity.END;
            holder.loadingSpinner.setVisibility(View.GONE); // Hide the spinner for user's messages
        } else {
            holder.messageText.setBackgroundResource(R.drawable.character_message_background);
            layoutParams.gravity = Gravity.START;
            // Show the spinner and hide the message text for character's messages if the message is null
            if (message.getText() == null) {
                holder.loadingSpinner.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.GONE);
            } else {
                holder.loadingSpinner.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
            }
        }

        holder.messageText.setLayoutParams(layoutParams);
    }



    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ProgressBar loadingSpinner;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            loadingSpinner = itemView.findViewById(R.id.loading_spinner);
        }
    }
}

