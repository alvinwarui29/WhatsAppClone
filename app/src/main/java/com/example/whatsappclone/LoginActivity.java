package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private EditText edtEmail,edtPass;
    private AppCompatButton btnLogin,btnRegisterIntent;
    private TextView txtforgotPass,txtCreateAccount;
    private String email,password;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPass = findViewById(R.id.edtPasswordLogin);
        btnLogin = findViewById(R.id.btnLoginLogin);
        btnRegisterIntent = findViewById(R.id.btnRegisterIntentLogin);

        txtforgotPass = findViewById(R.id.forgotPasswordLogin);

        btnRegisterIntent.setOnClickListener(v -> {
            SendUserToRegister();

        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUser();
            }
        });






    }

    private void SignInUser() {

        email = edtEmail.getText().toString();
        password = edtPass.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please input your email address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else{
            pd.setTitle("Creating your Account");
            pd.setMessage("Kindly wait");
            pd.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        sendToMainActivity();
                        pd.dismiss();
                    }
                    else{
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });

        }


    }

    private void SendUserToRegister() {
        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }


    private void sendToMainActivity() {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}