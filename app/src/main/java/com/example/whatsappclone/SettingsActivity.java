package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        RetrieveUserInfo();

    }




    private void initializeView() {
        profileImage = findViewById(R.id.profileImageSettings);
        edtUsername = findViewById(R.id.edtUserNameSettings);
        edtStatus = findViewById(R.id.edtStatusSettings);
        btnUpdate = findViewById(R.id.btnUpdateSettings);
        mAuth = FirebaseAuth.getInstance();
        currentUID =mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

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
                if (snapshot.exists() && snapshot.hasChild("UserName") && snapshot.hasChild("Profile_Image")){
                    RUserName = snapshot.child("UserName").getValue().toString();
                    Rstatus = snapshot.child("Status").getValue().toString();
                    Rprofile = snapshot.child("Profile_Image").getValue().toString();

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