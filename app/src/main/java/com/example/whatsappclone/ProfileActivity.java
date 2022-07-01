package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiveUserID,senderUID,currentState;
    private TextView txtUserName,txtStatus;
    private CircleImageView profilePic;
    private AppCompatButton btnsend,btnDecline;
    private DatabaseReference userRef,chartRequestRef,contactRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        contactRef = FirebaseDatabase.getInstance().getReference("Contacts");
        btnDecline = findViewById(R.id.btnDeclineMessageProfileActivity);
        chartRequestRef = FirebaseDatabase.getInstance().getReference("Chat_Request");
        auth = FirebaseAuth.getInstance();
        senderUID = auth.getCurrentUser().getUid();
        currentState = "new";
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        btnsend = findViewById(R.id.btnSendMessageProfileActivity);
        profilePic = findViewById(R.id.profilePicProfileActivity);
        txtStatus = findViewById(R.id.txtUserStatusProfileActivity);
        txtUserName = findViewById(R.id.txtUserNameProfileActivity);
        receiveUserID = getIntent().getExtras().get("ID").toString();

        RetrieveUserInfo();

    }

    private void RetrieveUserInfo() {
        userRef.child(receiveUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()&& snapshot.hasChild("Profile")){
                    String image = snapshot.child("Profile").getValue().toString();
                    String name = snapshot.child("UserName").getValue().toString();
                    String status = snapshot.child("Status").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.ic_baseline_person_24).into(profilePic);

                    txtUserName.setText(name);
                    txtUserName.setText(status);
                    ManageChatRequest();
                }
                else{
                    String name = snapshot.child("UserName").getValue().toString();
                    String status = snapshot.child("Status").getValue().toString();

                    txtUserName.setText(name);
                    txtUserName.setText(status);
                    ManageChatRequest();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void ManageChatRequest() {
        if(! senderUID.equals(receiveUserID)){

            chartRequestRef.child(senderUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(receiveUserID)){
                        String type = snapshot.child(receiveUserID).child("request_type").getValue().toString();

                        if (type.equals("sent")){
                            currentState = "request_sent";
                            btnsend.setText("Cancel request");
                        }
                        else if (type.equals("recieved")){
                            currentState = "request_received";
                            btnsend.setText("Accept Chat Request");
                            btnDecline.setVisibility(View.VISIBLE);
                            btnDecline.setEnabled(true);

                            btnDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CancelRequest();
                                }
                            });

                        }
                    }
                    else{
                        contactRef.child(senderUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(receiveUserID)){
                                    currentState = "friends";
                                    btnsend.setText("Remove Contact");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            if (!senderUID.equals(receiveUserID)){

                btnsend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnsend.setEnabled(false);
                        if (currentState.equals("new")){
                            SendChatRequest();
                        }
                        else if (currentState.equals("request_sent")){
                            CancelRequest();
                        }
                        else if (currentState.equals("request_received")){
                            AcceptRequest();
                        }
                        else if(currentState.equals("friends")){
                            RemoveContact();
                        }
                    }
                });

            }

        }
        else{
            btnsend.setVisibility(View.INVISIBLE);
        }

    }

    private void RemoveContact() {

        contactRef.child(senderUID).child(receiveUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            contactRef.child(receiveUserID).child(senderUID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                btnsend.setEnabled(true);
                                                currentState = "new";
                                                btnsend.setText("Send Request");

                                                btnDecline.setVisibility(View.INVISIBLE);
                                                btnDecline.setEnabled(false);

                                            }

                                        }
                                    });

                        }

                    }
                });


    }

    private void AcceptRequest() {

        contactRef.child(senderUID).child(receiveUserID)
                .child("Contacts").setValue("Saved").addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    contactRef.child(receiveUserID).child(senderUID)
                                            .child("Contacts").setValue("Saved").addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                chartRequestRef.child(senderUID)
                                                                        .child(receiveUserID).removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()){
                                                                                    chartRequestRef.child(receiveUserID)
                                                                                            .child(senderUID).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()){
                                                                                                        btnsend.setEnabled(true);
                                                                                                        currentState = "friends";
                                                                                                        btnsend.setText("Remove Contact");

                                                                                                        btnDecline.setEnabled(false);
                                                                                                        btnDecline.setVisibility(View.INVISIBLE);


                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    }
                                            );
                                }
                            }
                        }
                );


    }


    private void CancelRequest() {

        chartRequestRef.child(senderUID).child(receiveUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            chartRequestRef.child(senderUID).child(receiveUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                btnsend.setEnabled(true);
                                                currentState = "new";
                                                btnsend.setText("Send Request");

                                                btnDecline.setVisibility(View.INVISIBLE);
                                                btnDecline.setEnabled(false);

                                            }

                                        }
                                    });

                        }

                    }
                });

    }

    private void SendChatRequest() {
        chartRequestRef.child(senderUID).child(receiveUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    chartRequestRef.child(receiveUserID).child(senderUID).child("request_type").setValue("recieved")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        btnsend.setEnabled(true);
                                        currentState = "request_sent";
                                        btnsend.setText("Cancel request");
                                    }


                                }
                            });
                    ;
                }
            }
        });



    }
}