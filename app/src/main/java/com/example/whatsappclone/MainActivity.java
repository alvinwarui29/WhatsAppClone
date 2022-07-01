package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAccessor tabsAccessor;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        viewPager = findViewById(R.id.view_main_pager);
        tabLayout = findViewById(R.id.main_tabs);
        mtoolbar = findViewById(R.id.appbarlayout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("WhatsApp");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabsAccessor = new TabsAccessor(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessor);
        tabLayout.setupWithViewPager(viewPager) ;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null){
            sendToLoginActivity();
        }
        else{
            verifyExistence();
        }

    }

    private void verifyExistence() {
        String currentUID = mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("UserName").exists()){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendToLoginActivity() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item.getItemId() == R.id.SettingsMenu){
            sendToSettingsActivity();

        }
        else if (item.getItemId() == R.id.LogoutMenu){
            mAuth.signOut();
            sendToLoginActivity();

        }
        else if (item.getItemId() == R.id.FindFriendsMenu){

        }
        else if (item.getItemId() == R.id.CreateGroupMenu){
            RequestNewGroup();

        }
        return true;

    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter Group Name : ");
        final EditText groupName = new EditText(MainActivity.this);
        groupName.setHint("e.g the stars");
        builder.setView(groupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String grpName = groupName.getText().toString();
                if (TextUtils.isEmpty(grpName)){
                    Toast.makeText(MainActivity.this, "Kindly Enter Group Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    CreateNewGroup(grpName);

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.show();



    }

    private void CreateNewGroup(String grpName) {
        rootRef.child("Groups").child(grpName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, grpName + "Created successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void sendToSettingsActivity() {
        Intent i = new Intent(MainActivity.this,SettingsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}