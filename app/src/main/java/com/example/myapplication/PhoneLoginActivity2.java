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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity2 extends AppCompatActivity {
    private Button SendVerificationCodeButton, VerifyButton;
    private EditText InputPhoneNumber, InputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login2);

        mAuth = FirebaseAuth.getInstance();
        SendVerificationCodeButton = findViewById(R.id.send_verification_code_button);
        VerifyButton = findViewById(R.id.verify_button);
        InputPhoneNumber = findViewById(R.id.phone_number_input);
        InputVerificationCode = findViewById(R.id.verification_code_input);
        loadingBar= new ProgressDialog(this);


        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String phoneNumber = InputPhoneNumber.getText().toString();


                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneLoginActivity2.this, "Please enter your number first...", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("phone verification");
                    loadingBar.setMessage("please be patient as we are verifying your phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneLoginActivity2.this,
                            callbacks

                    );




                    VerifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                            InputPhoneNumber.setVisibility(View.INVISIBLE);

                            String verificationcode = InputVerificationCode.getText().toString();
                            if (TextUtils.isEmpty(verificationcode)){
                                Toast.makeText(PhoneLoginActivity2.this, "please write verification code first...", Toast.LENGTH_SHORT).show();
                            }else {
                                loadingBar.setTitle("Code verification");
                                loadingBar.setMessage("please be patient as we are verifying your verification code...");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();

                                PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mVerificationId,verificationcode);
                                signInWithPhoneAuthCredential(credential);
                            }
                        }
                    });
                    callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }


                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {


                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity2.this, "Invalid Phone Number Please Enter valid phone with the country code...", Toast.LENGTH_SHORT).show();

                            SendVerificationCodeButton.setVisibility(View.VISIBLE);
                            InputPhoneNumber.setVisibility(View.VISIBLE);

                            VerifyButton.setVisibility(View.INVISIBLE);
                            InputVerificationCode.setVisibility(View.INVISIBLE);
                        }
                        public void onCodeSent(String verificationId,
                                               PhoneAuthProvider.ForceResendingToken token) {

                            mVerificationId = verificationId;
                            mResendToken = token;
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity2.this, "code has been send successfully...", Toast.LENGTH_SHORT).show();


                            SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                            InputPhoneNumber.setVisibility(View.INVISIBLE);

                            VerifyButton.setVisibility(View.VISIBLE);
                            InputVerificationCode.setVisibility(View.VISIBLE);
                        }
                    };
                }
            }
        });


    }
    public void  signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           loadingBar.dismiss();
                           Toast.makeText(PhoneLoginActivity2.this, "Congratulations! you are logged in successfully...", Toast.LENGTH_SHORT).show();
                       SendUserToMainActivity();
                       }else{
                           String message = task.toString();
                           Toast.makeText(PhoneLoginActivity2.this, "Error " + message, Toast.LENGTH_SHORT).show();
                       }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PhoneLoginActivity2.this,MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}

