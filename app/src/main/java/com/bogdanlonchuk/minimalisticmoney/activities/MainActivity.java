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


public class MainActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditPass;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        mProgressDialog = new ProgressDialog(this);
        loginUser();
    }

    //This method work is user logged in
    private void loginUser(){
        mEditEmail = findViewById(R.id.email_login);
        mEditPass = findViewById(R.id.password_login);
        Button mBtnLogin = findViewById(R.id.btn_login);
        TextView mForgotPassword = findViewById(R.id.forget_password);
        TextView mSignUp = findViewById(R.id.signup_reg);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
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
                    return;
                }
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mProgressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                        }else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to Registration page
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to Reset Password Page
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
            }
        });
    }
}
