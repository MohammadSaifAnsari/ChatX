package com.example.chatsx.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsx.ChatActivity;
import com.example.chatsx.Model.UserModel;
import com.example.chatsx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    ArrayList<UserModel> userList;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    public UserListAdapter(ArrayList<UserModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }


//    public void  filterLists(ArrayList<UserModel>filterlist){
//        this.userList = filterlist;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder holder, int position) {
        UserModel userModel = userList.get(position);

        //Using picasso to download image from firebase and if there is no image default image is set in placeholder
        Picasso.get().load(userModel.getProfileImg()).placeholder(R.drawable.baseline_person).into((ImageView) holder.userImg);

        //To display user name
        holder.userName.setText(userModel.getUserName());


        //To show last message
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase.getReference().child("Chats").
                child(firebaseAuth.getUid() + userModel.getUserId()).
                orderByChild("timestamp").
                limitToLast(1).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                                holder.lastMessage.setText(datasnapshot.child("textMessage").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userId", userModel.getUserId());
                intent.putExtra("userImg", userModel.getProfileImg());
                intent.putExtra("userName", userModel.getUserName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View userImg;
        TextView lastMessage, userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImg = itemView.findViewById(R.id.userProfileImg);
            userName = itemView.findViewById(R.id.userListName);
            lastMessage = itemView.findViewById(R.id.lastMess);
        }
    }

//    public void filterList(String text) {
//        ArrayList<UserModel> searchUserList = new ArrayList<>();
//        for (UserModel user:userList){
//            if (user.getUserName().toLowerCase().contains(text)){
//                searchUserList.add(user);
//            }
//        }
//        if (searchUserList.isEmpty()){
//            Toast.makeText(context, "No User Found", Toast.LENGTH_SHORT).show();
//        }else {
//            notifyDataSetChanged();
//        }
//    }
}
