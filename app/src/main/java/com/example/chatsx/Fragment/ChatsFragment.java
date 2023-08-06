package com.example.chatsx.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatsx.Adapter.UserListAdapter;
import com.example.chatsx.MainActivity;
import com.example.chatsx.Model.UserModel;
import com.example.chatsx.R;
import com.example.chatsx.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {


    FragmentChatsBinding fragmentChatsBinding;
    ArrayList<UserModel> userLists = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    UserListAdapter adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentChatsBinding = FragmentChatsBinding.inflate(inflater, container, false);


        //Setting user list in fragment with adapter
        adapter = new UserListAdapter(userLists, getContext());
        fragmentChatsBinding.chatsRecyclerView.setAdapter(adapter);

        //Setting linear layout manager with recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        fragmentChatsBinding.chatsRecyclerView.setLayoutManager(linearLayoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    userModel.setUserId(dataSnapshot.getKey());

                    if (!userModel.getUserId().equals(firebaseAuth.getUid())) {
                        userLists.add(userModel);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        Bundle bundle = this.getArguments();
//        String strtext = bundle.getString("mText");
//        Log.d("qwertyqwerty",strtext);
        return fragmentChatsBinding.getRoot();
    }


//    public void search(String text){
//        firebaseDatabase.getReference().child("User").
//                addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        userLists.clear();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                        {
//                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
//                            String student = userModel.getUserName();
//                            if (text.toLowerCase().equals(student.toLowerCase())){
//                                userLists.add(userModel);
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }


//    public void filterList(String text) {
//        ArrayList<UserModel> searchUserList = new ArrayList<>();
//        for (UserModel user:userLists){
//            if (user.getUserName().toLowerCase().contains(text)){
//                searchUserList.add(user);
//            }
//        }
//        if (searchUserList.isEmpty()){
//            Toast.makeText(getContext(), "No User Found", Toast.LENGTH_SHORT).show();
//        }else {
//            adapter.filterLists(userLists);
//            adapter.notifyDataSetChanged();
//        }
//    }
}