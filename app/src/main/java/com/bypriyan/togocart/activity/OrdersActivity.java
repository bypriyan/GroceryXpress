package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.adapter.AdapterOrders;
import com.bypriyan.togocart.databinding.ActivityOrdersBinding;
import com.bypriyan.togocart.models.ModelOrders;
import com.bypriyan.togocart.utilities.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrders> ordersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(OrdersActivity.this, R.color.white));// set status background white
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadOrders();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadOrders() {
        ordersArrayList = new ArrayList<>();
        String authId = firebaseAuth.getCurrentUser().getUid().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_ORDERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ordersArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String orderBy = ""+ds.child(Constant.KEY_ORDERBY).getValue().toString();
                    ModelOrders modelOrders = ds.getValue(ModelOrders.class);
                    if(authId.equals(orderBy))
                        ordersArrayList.add(modelOrders);
                }
                binding.recyclearOrders.setAdapter(new AdapterOrders(OrdersActivity.this, ordersArrayList));

                if(ordersArrayList.size()<=0){
                    binding.noOrderLayout.setVisibility(View.VISIBLE);
                    binding.recyclearOrders.setVisibility(View.GONE);
                }else{
                    binding.noOrderLayout.setVisibility(View.GONE);
                    binding.recyclearOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}