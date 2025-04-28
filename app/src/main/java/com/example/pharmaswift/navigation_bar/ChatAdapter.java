package com.example.pharmaswift.navigation_bar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmaswift.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_box, parent, false);  // Your chat_item.xml layout
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.isUser()) {
            holder.chatTextViewRight.setVisibility(View.VISIBLE);
            holder.chatTextViewLeft.setVisibility(View.GONE);
            holder.chatTextViewRight.setText(message.getText());
        } else {
            holder.chatTextViewLeft.setVisibility(View.VISIBLE);
            holder.chatTextViewRight.setVisibility(View.GONE);
            holder.chatTextViewLeft.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatTextViewLeft, chatTextViewRight;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatTextViewLeft = itemView.findViewById(R.id.chatTextViewLeft);
            chatTextViewRight = itemView.findViewById(R.id.chatTextViewRight);
        }
    }
}
