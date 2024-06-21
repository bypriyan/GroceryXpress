package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.adapter.AdapterNotification;
import com.bypriyan.togocart.databinding.ActivityCartBinding;
import com.bypriyan.togocart.databinding.ActivityNotificationsBinding;
import com.bypriyan.togocart.models.ModelNotifications;
import com.bypriyan.togocart.utilities.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;
    private ArrayList<ModelNotifications> notificationsArrayList;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(NotificationsActivity.this, R.color.white));// set status background white
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        loadNotifications();
    }

    private void loadNotifications() {
        notificationsArrayList = new ArrayList<>();
        String uid = firebaseAuth.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_MESSAGE_REPLY);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                notificationsArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String senderId = ""+ds.child(Constant.KEY_MESSAGE_SENDER_ID).getValue().toString();

                    ModelNotifications modelNotifications = ds.getValue(ModelNotifications.class);
                    if(senderId.equals(uid)){
                        notificationsArrayList.add(modelNotifications);
                    }
                }

                if(notificationsArrayList.isEmpty()){
                    binding.noNotificationLottie.setVisibility(View.VISIBLE);
                    binding.recyclearNotification.setVisibility(View.GONE);
                }else{
                    binding.noNotificationLottie.setVisibility(View.GONE);
                    binding.recyclearNotification.setVisibility(View.VISIBLE);
                    binding.recyclearNotification.setAdapter(new AdapterNotification(NotificationsActivity.this, notificationsArrayList));
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}