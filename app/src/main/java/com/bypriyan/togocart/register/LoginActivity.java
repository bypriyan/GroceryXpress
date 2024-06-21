package com.bypriyan.togocart.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.MainActivity;
import com.bypriyan.togocart.databinding.ActivityLoginBinding;
import com.bypriyan.togocart.utilities.Constant;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private EditText t1;
    private Button b1;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.white));// set status background white
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        t1 = findViewById(R.id.mobile);
        b1 = findViewById(R.id.otpBtn);

        PhoneNumber = t1.getText().toString();

        binding.otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                if (t1.getText().toString().isEmpty()) {
                    t1.setError("Empty");
                    loading(false);
                    return;
                } else if (t1.getText().toString().trim().length() != 10) {
                    t1.setError("please enter valid number");
                    loading(false);
                    return;
                } else {
                    otpSend();
                }
            }
        });

        binding.terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://togocart.blogspot.com/2022/07/terms-conditions.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://togocart.blogspot.com/2022/07/privacy-policy.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

    }

    private void loading(boolean isloading){
        if(isloading){
            binding.otpBtn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.otpBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }

    private void otpSend() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                loading(false);
                Toast.makeText(LoginActivity.this, "successfully OTP send to your number", Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onVerificationFailed (FirebaseException e){
                loading(false);

                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent (@NonNull String verificationId,
                                    @NonNull PhoneAuthProvider.ForceResendingToken token){

                loading(false);
                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                intent.putExtra("phoneNumber", t1.getText().toString() );
                intent.putExtra("verificationId",verificationId);
                startActivity(intent);

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+t1.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

}