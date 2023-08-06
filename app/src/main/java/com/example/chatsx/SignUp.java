package com.example.chatsx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatsx.Model.UserModel;
import com.example.chatsx.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding activitySignUpBinding;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(activitySignUpBinding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //creating progress dialog
        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");


        activitySignUpBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to show progress dialog
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(activitySignUpBinding.textEmail.getText().toString(),
                        activitySignUpBinding.textpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            UserModel userModel = new UserModel(activitySignUpBinding.textusername.getText().toString(),
                                    activitySignUpBinding.textEmail.getText().toString(),
                                    activitySignUpBinding.textpassword.getText().toString());

                            //to get user uid from firebase authentication
                            String uid = task.getResult().getUser().getUid();

                            //Creating user data in real time database
                            firebaseDatabase.getReference().child("User").child(uid).setValue(userModel);

                            Toast.makeText(SignUp.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        activitySignUpBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });
    }
}