package com.bypriyan.togocart.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.databinding.ActivitySupportAndAllBinding;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.preferenceManager;

public class SupportAndAllActivity extends AppCompatActivity {

    private ActivitySupportAndAllBinding binding;
    static int PERMISSION_CODE= 100;
    private String supportNumber = "9303844500";
    private String SearchType;
    private preferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(SupportAndAllActivity.this, R.color.white));// set status background white

        binding = ActivitySupportAndAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SearchType = getIntent().getStringExtra("viewType");
        binding.topic.setText(SearchType);
        preferenceManager = new preferenceManager(SupportAndAllActivity.this);

        if(SearchType.equals("Support")){
            binding.support.setVisibility(View.VISIBLE);
        }else{
            binding.support.setVisibility(View.GONE);
        }

        if(SearchType.equals("About Us")){
            binding.aboutUs.setVisibility(View.VISIBLE);
        }else{
            binding.aboutUs.setVisibility(View.GONE);
        }

        if(SearchType.equals("Profile")){
            binding.firstName.getEditText().setText(""+preferenceManager.getString(Constant.KEY_FIRST_NAME));
            binding.lastName.getEditText().setText(""+preferenceManager.getString(Constant.KEY_LAST_NAME));
            binding.profile.setVisibility(View.VISIBLE);
        }else{
            binding.profile.setVisibility(View.GONE);
        }

        binding.contect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.name.getEditText().getText().toString().isEmpty() &&
                        !binding.subject.getEditText().getText().toString().isEmpty()  ){
                    sendMail(binding.name.getEditText().getText().toString());
                }else{
                    Toast.makeText(SupportAndAllActivity.this, "Email field can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SupportAndAllActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(SupportAndAllActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

                }else{
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:"+supportNumber));
                    startActivity(i);
                }
            }
        });

        binding.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWhatappInstalled()){
                    String number = "+91"+supportNumber;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+number+
                            "&text="+"Hello ToGo Cart, I have some query"));
                    startActivity(i);

                }else {

                    Toast.makeText(SupportAndAllActivity.this,"Whatsapp is not installed",Toast.LENGTH_SHORT).show();

                }
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.firstName.getEditText().getText().toString().isEmpty()){
                    Toast.makeText(SupportAndAllActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
                }else if(binding.lastName.getEditText().getText().toString().isEmpty()){
                    Toast.makeText(SupportAndAllActivity.this, "please enter last name", Toast.LENGTH_SHORT).show();
                }else{
                    preferenceManager.putString(Constant.KEY_FIRST_NAME,binding.firstName.getEditText().getText().toString());
                    preferenceManager.putString(Constant.KEY_LAST_NAME,binding.lastName.getEditText().getText().toString());
                    Toast.makeText(SupportAndAllActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMail(String s) {
        String []recipient = {"ashishdhomane0099@gmail.com","ashishdhomane0099@gmail.com"};
        String subject = "ToGo Cart";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, s);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    private boolean isWhatappInstalled(){

        PackageManager packageManager = getPackageManager();
        boolean whatsappInstalled;

        try {

            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;


        }catch (PackageManager.NameNotFoundException e){

            whatsappInstalled = false;

        }

        return whatsappInstalled;

    }
}