package com.example.pharmaswift.navigation_bar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pharmaswift.R;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {


    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private EditText editTextMessage;
    private Button buttonSend;
    public List<Message> messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        messages = new ArrayList<>();

        recyclerView = v.findViewById(R.id.recycler_view_chat);
        editTextMessage = v.findViewById(R.id.edit_text_message);
        buttonSend = v.findViewById(R.id.button_send);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);

        buttonSend.setOnClickListener(v1 -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Add the user's message
                messages.add(new Message(messageText, true)); // true = user
                chatAdapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);

                // Simulate multiple bot replies
                String[] botReplies = {
                        "We apologize for the inconvenience.",
                        "Due to a high volume of requests and limited resources, you have been placed in a queue.",
                        "A dedicated pharmacist will attend to you shortly. Thank you for your patience."
                };

                for (String botReply : botReplies) {
                    messages.add(new Message(botReply, false)); // false = bot
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    recyclerView.scrollToPosition(messages.size() - 1);
                }

                // Clear input
                editTextMessage.setText("");
            } else {
                Toast.makeText(getContext(), "Please type a message", Toast.LENGTH_SHORT).show();
            }
        });


        return v;
    }
}