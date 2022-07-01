package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton btnSend;
    private TextView txtMessages;
    private ScrollView scrollView;
    private EditText edtMessage;
    private String grpName,currentUID,currentUserName,
            message,currentDate,currentTime;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef,groupRef,groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        grpName = getIntent().getExtras().get("grpName").toString();
        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUID = firebaseAuth.getCurrentUser().getUid();
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(grpName);

        InitializeViews();
        RetrieveUserInfo();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageInfoToDb();
                edtMessage.setText("");
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()){
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void InitializeViews() {
        toolbar = findViewById(R.id.toolbargroupActiviy);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(grpName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnSend = findViewById(R.id.imgbtnSendGroupChat);
        txtMessages = findViewById(R.id.txtMessagesGroupChat);
        scrollView = findViewById(R.id.scrollViewGroupChat);
        edtMessage = findViewById(R.id.edtMessageGroupChat);





    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void RetrieveUserInfo() {
        rootRef.child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUserName = snapshot.child("UserName").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void SaveMessageInfoToDb() {
        String messageKey = groupRef.push().getKey();
        message = edtMessage.getText().toString();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Enter the message to be sent", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calendarD = Calendar.getInstance();
            SimpleDateFormat SDF = new SimpleDateFormat("MMM dd,yyyy");
            currentDate = SDF.format(calendarD.getTime());

            Calendar calendarT = Calendar.getInstance();
            SimpleDateFormat STF = new SimpleDateFormat("hh:mm a");
            currentTime = STF.format(calendarT.getTime());

            HashMap<String ,Object> grphashMap = new HashMap<>();
            grphashMap.put("Date",currentDate);
            grphashMap.put("Time",currentTime);
            grphashMap.put("Message",message);
            grphashMap.put("UserName",currentUserName);


            groupRef.child(messageKey).updateChildren(grphashMap);




        }



    }

    private void DisplayMessages(DataSnapshot snapshot) {
        Iterator iterator =snapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String chatDate = ((DataSnapshot)iterator.next()).getValue().toString();
            String chatMessage = ((DataSnapshot)iterator.next()).getValue().toString();
            String chatTime = ((DataSnapshot)iterator.next()).getValue().toString();
            String chatSender = ((DataSnapshot)iterator.next()).getValue().toString();
            txtMessages.append(chatSender + ":\n" + chatMessage + "\n" + chatTime + "   " + chatDate + "\n\n\n");

            scrollView.fullScroll(View.FOCUS_DOWN);


        }

    }

}
