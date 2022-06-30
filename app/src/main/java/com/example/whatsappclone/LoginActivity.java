package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private EditText edtEmail,edtPass;
    private AppCompatButton btnLogin,btnRegister;
    private TextView txtforgotPass,txtCreateAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPass = findViewById(R.id.edtPasswordLogin);
        btnLogin = findViewById(R.id.btnLoginLogin);
        btnRegister = findViewById(R.id.btnRegisterIntentLogin);

        txtforgotPass = findViewById(R.id.forgotPasswordLogin);

        btnRegister.setOnClickListener(v -> {
            SendUserToRegister();

        });





    }

    private void SendUserToRegister() {
        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser != null){
            sendToMainActivity();
        }

    }

    private void sendToMainActivity() {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}