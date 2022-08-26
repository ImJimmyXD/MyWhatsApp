package imjimmyxd.simi.mywhatsapp;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText phone, otp;
    Button buttonGenerate, buttonVerify;
    FirebaseAuth mAuth;
    String verificationID;
    ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        buttonGenerate = findViewById(R.id.buttonGenerateOTP);
        buttonVerify =findViewById(R.id.buttonVerifyOTP);
        mAuth = FirebaseAuth.getInstance();
        bar = findViewById(R.id.bar);
        buttonGenerate.setOnClickListener(v -> {
            if(TextUtils.isEmpty(phone.getText().toString()))
            {
                Toast.makeText(MainActivity.this, "Enter Valid Phone No.", Toast.LENGTH_SHORT).show();
            }
            else {
                String number = phone.getText().toString();
                bar.setVisibility(View.VISIBLE);
                sendVerificationCode(number);
            }
        });
        buttonVerify.setOnClickListener(v -> {
            if(TextUtils.isEmpty(otp.getText().toString()))
            {
                Toast.makeText(MainActivity.this, "Wrong OTP Entered", Toast.LENGTH_SHORT).show();
            }
            else
                verifyCode(otp.getText().toString());
        });
    }

    private void sendVerificationCode(String phoneNumber)
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+40"+phoneNumber)  // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential)
        {
            final String code = credential.getSmsCode();
            Toast.makeText(getApplicationContext(),"Verifying code Automatically",LENGTH_LONG).show();
            if(code!=null)
            {
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token)
        {
            super.onCodeSent(s, token);
            verificationID = s;
            Toast.makeText(MainActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
            buttonVerify.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);
        }};
    private void verifyCode(String Code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,Code);
        signInByCredentials(credential);
    }

    private void signInByCredentials(PhoneAuthCredential credential)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((OnCompleteListener<com.google.firebase.auth.AuthResult>) task -> {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    }

                });}

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null)
        {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }}}