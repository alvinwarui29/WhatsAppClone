package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAccessor tabsAccessor;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }

    private void sendToLoginActivity() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}