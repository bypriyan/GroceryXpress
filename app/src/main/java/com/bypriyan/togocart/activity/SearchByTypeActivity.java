package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bypriyan.togocart.models.ModelCartitem;
import com.bypriyan.togocart.showCart.ShowCartItem;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.adapter.AdapterProductsGrid;
import com.bypriyan.togocart.databinding.ActivitySearchByTypeBinding;
import com.bypriyan.togocart.models.ModelProducts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class SearchByTypeActivity extends AppCompatActivity implements ShowCartItem {

    private ActivitySearchByTypeBinding binding;
    String searchType;
    private ArrayList<ModelProducts> productsArrayList;
    private ArrayList<ModelCartitem> cartitemList;
    public double allTotalPrise = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(SearchByTypeActivity.this, R.color.white));// set status background white
        binding = ActivitySearchByTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchType = getIntent().getStringExtra("searchType");
        loading(true);

        binding.searchType.setText(searchType);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        loadProducts(searchType);
                    }
                });
            }
        }, 1000);



        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.searchBtn.setOnClickListener(v -> {
            startActivity(new Intent(SearchByTypeActivity.this, SearchProductActivity.class));
        });

        binding.goToCart.setOnClickListener(v -> {
            startActivity(new Intent(SearchByTypeActivity.this, CartActivity.class));
        });

    }

    private void loadProducts(String category) {
        productsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PRODUCTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                productsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String productCategory = "" + ds.child(Constant.KEY_PRODUCT_CATEGORY).getValue().toString();
                    ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                    if (category.equals(productCategory))
                        productsArrayList.add(modelProducts);
                }
                Collections.reverse(productsArrayList);
                binding.recyclearProducts.setAdapter(new AdapterProductsGrid(SearchByTypeActivity.this, productsArrayList, "SearchByTypeActivity"));
                loading(false);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                loading(false);

            }
        });
    }

    private void loading(boolean isloading) {
        if (isloading) {
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.progressbar.setVisibility(View.GONE);
        }

    }

    @Override
    public void showData() {
        cartitemList = new ArrayList<>();
        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("ITEM_ID", new String[]{"text", "unique"}))
                .addColumn(new Column("ITEM_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("ITEM_NAME", new String[]{"text", "not null"}))
                .addColumn(new Column("ITEM_PRISE_EACH", new String[]{"text", "not null"}))
                .addColumn(new Column("ITEM_PRISE", new String[]{"text", "not null"}))
                .addColumn(new Column("ITEM_QUENTITY", new String[]{"text", "not null"}))
                .addColumn(new Column("ITEM_P_IMG", new String[]{"text", "not null"}))
                .addColumn(new Column("ITEM_P_QUENTITY", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String prise = res.getString(4);
            String cost = res.getString(5);
            String quentity = res.getString(6);
            String pImg = res.getString(7);
            String pQuentity = res.getString(8);

            allTotalPrise = allTotalPrise + Double.parseDouble(cost);

            ModelCartitem modelCartitem = new ModelCartitem("" + id,
                    "" + pId,
                    "" + name,
                    "" + prise,
                    "" + cost,
                    "" + quentity,
                    "" + pImg,
                    "" + pQuentity);

            cartitemList.add(modelCartitem);
        }

        if (cartitemList.isEmpty()) {
            binding.goToCart.setVisibility(View.GONE);
        } else {
            binding.goToCart.setVisibility(View.VISIBLE);
            binding.totalAmount.setText("₹" + allTotalPrise);
            binding.totalItems.setText(cartitemList.size() + " items");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        allTotalPrise = 0.0;
        showData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        allTotalPrise = 0.0;
        showData();
    }
}