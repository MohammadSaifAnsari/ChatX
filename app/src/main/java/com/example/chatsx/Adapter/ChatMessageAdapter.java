package com.example.chatsx.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsx.Model.ChatModel;
import com.example.chatsx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatMessageAdapter extends RecyclerView.Adapter {

    ArrayList<ChatModel> chatList;
    Context context;

    FirebaseDatabase firebaseDatabase;

    FirebaseAuth firebaseAuth;

    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECIEVER_VIEW_TYPE = 2;

    public ChatMessageAdapter(ArrayList<ChatModel> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    public ChatMessageAdapter(ArrayList<ChatModel> chatList, Context context, String recId) {
        this.chatList = chatList;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat_bubble, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciever_chat_bubble, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (chatList.get(position).getuId().equals(firebaseAuth.getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECIEVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chatModel = chatList.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //To delete chat message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sender = firebaseAuth.getUid() + recId;

                                firebaseDatabase.getReference().child("Chats").child(sender)
                                        .child(chatModel.getTextMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });

        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).sendertext.setText(chatModel.getTextMessage());
        } else {
            ((RecieverViewHolder) holder).recievertext.setText(chatModel.getTextMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        TextView recievertext, recievertime;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            recievertext = itemView.findViewById(R.id.recieverText);
            recievertime = itemView.findViewById(R.id.recieverTime);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView sendertext, sendertime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendertext = itemView.findViewById(R.id.senderText);
            sendertime = itemView.findViewById(R.id.senderTime);
        }
    }
}
