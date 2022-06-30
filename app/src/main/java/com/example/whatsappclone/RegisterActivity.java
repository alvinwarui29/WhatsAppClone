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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private EditText edtEmail,edtPass;
    private AppCompatButton btnLoginIntent,btnRegister;
    private TextView txtforgotPass,txtCreateAccount;
    private String email,password,User;
    private ProgressDialog pd;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);
        edtEmail = findViewById(R.id.edtEmailRegister);
        edtPass = findViewById(R.id.edtPasswordRegister);
        btnLoginIntent = findViewById(R.id.btnLoginIntentRegister);
        btnRegister = findViewById(R.id.btnRegisterRegister);
        mAuth = FirebaseAuth.getInstance();
        btnLoginIntent.setOnClickListener(v -> SendToLoginActivity());
        btnRegister.setOnClickListener(v -> {
            CreateAccount();

        });

    }

    private void CreateAccount() {
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
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        User = mAuth.getCurrentUser().getUid();
                        databaseReference.child("Users").child(User).setValue("");
                        sendToMainActivity();
                        pd.dismiss();
                    }
                    else{
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }








    }

    private void SendToLoginActivity() {
        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
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
        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}