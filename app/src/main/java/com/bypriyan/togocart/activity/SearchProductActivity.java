package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bypriyan.togocart.adapter.AdapterCategory;
import com.bypriyan.togocart.databinding.ActivitySearchProductBinding;
import com.bypriyan.togocart.models.ModelCartitem;
import com.bypriyan.togocart.models.ModelCategories;
import com.bypriyan.togocart.showCart.ShowCartItem;
import com.bypriyan.togocart.showCart.ShowCategories;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.adapter.AdapterProductsGrid;
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

public class SearchProductActivity extends AppCompatActivity implements ShowCartItem {

    private ActivitySearchProductBinding binding;
    private ArrayList<ModelProducts> productsArrayList;
    private AdapterProductsGrid adapterProducts;
    boolean isLoad = false;
    private ArrayList<ModelCartitem> cartitemList;
    public double allTotalPrise = 0.0;
    private ArrayList<ModelCategories> categoriesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(SearchProductActivity.this, R.color.white));// set status background white
        binding = ActivitySearchProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoriesArrayList = new ArrayList<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        showData();

        executorService.execute(new Runnable() {
            public void run() {
                ShowCategories showCategories = new ShowCategories(categoriesArrayList);
                int i=showCategories.getCategoriesArraylist().size();
                //
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (i > 0) {
                            binding.recyclearCategories.setAdapter(new AdapterCategory(SearchProductActivity.this, categoriesArrayList));
                        }
                    }
                });
                //
            }
        });
        executorService.shutdown();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.goToCart.setOnClickListener(v -> {
            startActivity(new Intent(SearchProductActivity.this, CartActivity.class));
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchView.clearFocus();
                binding.trendingRelativeL.setVisibility(View.GONE);

                if(!isLoad){
                    loading(true);
                    loadProducts();
                }
                try{
                    adapterProducts.getFilter().filter(query.trim());
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void loadProducts() {
        productsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PRODUCTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                productsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                    productsArrayList.add(modelProducts);
                }
                Collections.reverse(productsArrayList);
                adapterProducts = new AdapterProductsGrid(SearchProductActivity.this, productsArrayList, "SearchProductActivity");
                binding.recyclearProducts.setAdapter(adapterProducts);
                try{
                    adapterProducts.getFilter().filter(binding.searchView.getQuery().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                loading(false);
                isLoad = true;
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

        Cursor res= easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String prise = res.getString(4);
            String cost = res.getString(5);
            String quentity = res.getString(6);
            String pImg = res.getString(7);
            String pQuentity = res.getString(8);

            allTotalPrise = allTotalPrise+Double.parseDouble(cost);

            ModelCartitem modelCartitem = new ModelCartitem(""+id,
                    ""+pId,
                    ""+name,
                    ""+prise,
                    ""+cost,
                    ""+quentity,
                    ""+pImg,
                    ""+pQuentity);

            cartitemList.add(modelCartitem);
        }

        if(cartitemList.isEmpty()){
            binding.goToCart.setVisibility(View.GONE);
        }else{
            binding.goToCart.setVisibility(View.VISIBLE);
            binding.totalAmount.setText("₹"+allTotalPrise);
            binding.totalItems.setText(cartitemList.size()+" items");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        allTotalPrise=0.0;
        showData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        allTotalPrise=0.0;
        showData();
    }

}