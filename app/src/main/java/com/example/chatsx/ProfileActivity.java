package com.example.chatsx;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chatsx.Model.UserModel;
import com.example.chatsx.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding activityProfileBinding;

    FirebaseStorage firebaseStorage;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    Uri uriImage;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
            requestPermissions(permission, 66);
        }


        activityProfileBinding.cameraMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
                    requestPermissions(permission, 66);
                } else {
                    openCamera();
                }
            }
        });
        activityProfileBinding.backspaceMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        activityProfileBinding.userProfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");  //this type of data bringing from gallery
                galleryActivityResultLauncher.launch(galleryintent);

            }
        });

        activityProfileBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName2 = activityProfileBinding.userProfName.getText().toString();
                String about = activityProfileBinding.userProfAbout.getText().toString();

                HashMap<String, Object> obj = new HashMap<>();
                obj.put("userName", userName2);
                obj.put("About", about);

                firebaseDatabase.getReference().child("User").
                        child(firebaseAuth.getUid()).updateChildren(obj);
                Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });


        firebaseDatabase.getReference().child("User").child(firebaseAuth.getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel userModel = snapshot.getValue(UserModel.class);

                        Picasso.get().load(userModel.getProfileImg()).
                                placeholder(R.drawable.baseline_person).into(activityProfileBinding.userProfImg);

                        activityProfileBinding.userProfName.setText(userModel.getUserName());
                        activityProfileBinding.userProfAbout.setText(userModel.getAbout());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {

                        //Getting user image uri
                        Uri uri = result.getData().getData();
                        activityProfileBinding.userProfImg.setImageURI(uri);


                        //Upload profile img to firebase database
                        final StorageReference storageReference = firebaseStorage.getReference().
                                child("Profile Image").child(firebaseAuth.getUid());

                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        firebaseDatabase.getReference().child("User").
                                                child(firebaseAuth.getUid()).child("profileImg").setValue(uri.toString());
                                        Toast.makeText(ProfileActivity.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                }
            });

    //opens camera so that user can capture image
    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        uriImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new
            ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap uriBitmap = uriToBitmap(uriImage);
                        Bitmap rotate = rotateBitmap(uriBitmap);
                        activityProfileBinding.userProfImg.setImageBitmap(rotate);


                        final StorageReference storageReference = firebaseStorage.getReference().
                                child("Profile Image").child(firebaseAuth.getUid());

                        // Get the data from an ImageView as bytes
                        activityProfileBinding.userProfImg.setDrawingCacheEnabled(true);
                        activityProfileBinding.userProfImg.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) activityProfileBinding.userProfImg.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageReference.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(ProfileActivity.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        firebaseDatabase.getReference().child("User").
                                                child(firebaseAuth.getUid()).child("profileImg").setValue(uri.toString());
                                        Toast.makeText(ProfileActivity.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                }
            });


    // takes URI of the image and returns bitmap
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //rotate image if image captured on samsung devices
    // Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input) {
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = getContentResolver().query(uriImage, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        Log.d("tryOrientation", orientation + "");
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        Bitmap cropped = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        return cropped;
    }

}