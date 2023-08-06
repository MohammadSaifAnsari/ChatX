package com.example.chatsx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chatsx.Adapter.ChatMessageAdapter;
import com.example.chatsx.Model.ChatModel;
import com.example.chatsx.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding activityChatBinding;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final String senderId = firebaseAuth.getUid();
        String recieverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String userImg = getIntent().getStringExtra("userImg");
        Log.d("SAIF", userName);


        final String senderChatId = senderId + recieverId;
        final String recieverChatId = recieverId + senderId;

        activityChatBinding.userName1.setText(userName);
        Picasso.get().load(userImg).placeholder(R.drawable.baseline_person).into(activityChatBinding.userImg1);

        activityChatBinding.backspaceMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<ChatModel> chatLists = new ArrayList<>();

        final ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(chatLists, this, recieverId);
        activityChatBinding.rv3.setAdapter(chatMessageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        activityChatBinding.rv3.setLayoutManager(linearLayoutManager);


        //Displaying message to sender
        firebaseDatabase.getReference().child("Chats").child(senderChatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //To show data in database
                chatLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    chatModel.setTextMessageId(dataSnapshot.getKey());
                    chatLists.add(chatModel);
                }
                chatMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        activityChatBinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = activityChatBinding.messageET.getText().toString();

                if (message.isEmpty()) {
                    activityChatBinding.messageET.setError("Enter text");
                    return;
                }

                final ChatModel chatModel = new ChatModel(senderId, message);
                chatModel.setTimestamp(new Date().getTime());
                activityChatBinding.messageET.setText("");

                //sender message
                firebaseDatabase.getReference().child("Chats").child(senderChatId).push().setValue(chatModel).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                //reciever message
                                firebaseDatabase.getReference().child("Chats").child(recieverChatId).push().setValue(chatModel).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });
    }
}