package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private EditText edtUsername,edtStatus;
    private AppCompatButton btnUpdate;
    private String userName,status,currentUID;
    private String RUserName,Rstatus,Rprofile;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private Uri imageUri;
    private StorageReference userProfileRef;
    private static final int gallery = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeView();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,gallery);


            }
        });


        RetrieveUserInfo();

    }




    private void initializeView() {
        userProfileRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        profileImage = findViewById(R.id.profileImageSettings);
        edtUsername = findViewById(R.id.edtUserNameSettings);
        edtStatus = findViewById(R.id.edtStatusSettings);
        btnUpdate = findViewById(R.id.btnUpdateSettings);
        mAuth = FirebaseAuth.getInstance();
        currentUID =mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery && resultCode==RESULT_OK && data.getData()!=null) {

            imageUri = data.getData();
//            profileImage.setImageURI(imageUri);

            StorageReference filepath = userProfileRef.child(currentUID + ".jpg");

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = uri.toString();
                            rootRef.child("Users").child(currentUID).child("Profile").setValue(url);


                        }
                    });

                }
            });



        }



    }



    private void UpdateSettings() {
        userName = edtUsername.getText().toString();
        status = edtStatus.getText().toString();

        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Enter your username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(status)){
            Toast.makeText(this, "Enter your status", Toast.LENGTH_SHORT).show();
        }
        else{
            Update();
        }

    }

    private void Update() {
        HashMap hashMap = new HashMap();
        hashMap.put("UserName",userName);
        hashMap.put("Status",status);
        hashMap.put("Profile",Rprofile);

        rootRef.child("Users").child(currentUID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    SendToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                }



            }
        });



    }

    private void SendToMainActivity() {
        Intent i = new Intent(SettingsActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
    private void RetrieveUserInfo() {
        rootRef.child("Users").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("UserName") && snapshot.hasChild("Profile")){
                    Rprofile = snapshot.child("Profile").getValue().toString();
                    RUserName = snapshot.child("UserName").getValue().toString();
                    Rstatus = snapshot.child("Status").getValue().toString();

                    Picasso.get().load(Rprofile).into(profileImage);
                    edtUsername.setText(RUserName);
                    edtStatus.setText(Rstatus);
                }
                else if(snapshot.exists() && snapshot.hasChild("UserName")){
                    RUserName = snapshot.child("UserName").getValue().toString();
                    Rstatus = snapshot.child("Status").getValue().toString();

                    edtUsername.setText(RUserName);
                    edtStatus.setText(Rstatus);
                }
                else{
                    Toast.makeText(SettingsActivity.this, "Update your profile", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}