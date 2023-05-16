package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink,ForgetPasswordLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth= FirebaseAuth.getInstance();

       initializefield();


       NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              sendUserToRegisterActivity();
           }
       });

       LoginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               AllowUserToLOgin();
           }
       });
       PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent phoneloginIntent = new Intent(LoginActivity.this,PhoneLoginActivity2.class);
               phoneloginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(phoneloginIntent);
           }
       });
    }

    private void AllowUserToLOgin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Email....", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, " Please Enter your password...", Toast.LENGTH_SHORT).show();
        }else {

            loadingBar.setTitle("Sign in ...");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful()){


                                sendUserToMainActivityActivity();
                                Toast.makeText(LoginActivity.this, "logged in successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
    private void sendUserToMainActivityActivity() {
        Intent mainintent = new Intent(LoginActivity.this,MainActivity.class);
        mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainintent);
    }

    private void sendUserToRegisterActivity(){
        Intent registerintent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerintent);
    }
    private void initializefield() {
        LoginButton= findViewById(R.id.login_button);
        PhoneLoginButton= findViewById(R.id.phone_login_button);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.need_new_account_link);
        ForgetPasswordLink= findViewById(R.id.forgot_password_link);
        loadingBar= new ProgressDialog(this);
    }



}