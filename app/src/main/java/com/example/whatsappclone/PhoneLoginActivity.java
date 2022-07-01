package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private AppCompatButton btnVerify,btnsendCode;
    private EditText edtPhone,edtCode;
    private String phoneNumber;
    private FirebaseAuth mAuth;
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private ProgressDialog pd;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        InitializeViews();

        btnsendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setTitle("Verifying Phone");
                pd.setMessage("please wait");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                phoneNumber = edtPhone.getText().toString();
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(PhoneLoginActivity.this)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                pd.dismiss();
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }


            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                pd.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Enter correct phone number", Toast.LENGTH_SHORT).show();
                btnsendCode.setVisibility(View.VISIBLE);
                edtPhone.setVisibility(View.VISIBLE);

                edtCode.setVisibility(View.INVISIBLE);
                btnVerify.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                pd.dismiss();

                mVerificationId = verificationId;
                mResendToken = token;

                btnsendCode.setVisibility(View.INVISIBLE);
                edtPhone.setVisibility(View.INVISIBLE);

                edtCode.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.VISIBLE);
            }

        };

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.dismiss();
                btnsendCode.setVisibility(View.INVISIBLE);
                edtPhone.setVisibility(View.INVISIBLE);

                String verCode = edtCode.getText().toString();
                if (TextUtils.isEmpty(verCode)){
                    Toast.makeText(PhoneLoginActivity.this, "Enter your verification code", Toast.LENGTH_SHORT).show();
                }
                else{
                    pd.setTitle("Verifying Code");
                    pd.setMessage("please wait");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verCode);

                    signInWithPhoneAuthCredential(credential);

                }
            }
        });


    }

    private void InitializeViews() {
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        btnVerify = findViewById(R.id.btnVerifyPhoneLogin);
        btnsendCode = findViewById(R.id.btnSendCodePhoneLogin);
        edtCode = findViewById(R.id.edtVerificationCodePhoneLogin);
        edtPhone = findViewById(R.id.edtPhoneNumberPhoneLogin);

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {

                            SendUserToMainActivity();
                            pd.dismiss();
                        } else {

                            Toast.makeText(PhoneLoginActivity.this, task.getException().toString() , Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {

        pd.dismiss();
        Intent i = new Intent(PhoneLoginActivity.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

}