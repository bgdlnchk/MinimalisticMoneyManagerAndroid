package com.bogdanlonchuk.minimalisticmoney.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdanlonchuk.minimalisticmoney.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditPass;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        registerUser();
    }

    //This method work when you registered
    private void registerUser(){
        mEditEmail = findViewById(R.id.email_reg);
        mEditPass = findViewById(R.id.password_reg);
        Button mBtnRegistration = findViewById(R.id.btn_reg);
        TextView mSignIn = findViewById(R.id.signin_here);

        mBtnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail= mEditEmail.getText().toString().trim();
                String mPass= mEditPass.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    mEditEmail.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(mPass)){
                    mEditPass.setError("Password Required");
                }

                mProgressDialog.setMessage("Processing");
                mProgressDialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Complete",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Go to Sign In page
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
}
