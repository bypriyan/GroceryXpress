package com.bypriyan.togocart.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.adapter.AdapterCartItem;
import com.bypriyan.togocart.databinding.ActivityCartBinding;
import com.bypriyan.togocart.models.ModelCartitem;
import com.bypriyan.togocart.showCart.ConnectionReceiver;
import com.bypriyan.togocart.utilities.Constant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CartActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    private ActivityCartBinding binding;
    private EasyDB easyDB;
    private ArrayList<ModelCartitem> cartitemList;
    private AdapterCartItem adapterCartitem;

    public double allTotalPrise = 0.0;
    public TextView  sTotalTv, dFeeTv, allTotalPriseTv, totalCostTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(CartActivity.this, R.color.white));// set status background white

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        easyDB = EasyDB.init(CartActivity.this, "ITEMS_DB")
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

        dFeeTv = findViewById(R.id.dFeeTv);
        sTotalTv = findViewById(R.id.sTotalTv);
        allTotalPriseTv = findViewById(R.id.allTotalPriseTv);
        totalCostTv = findViewById(R.id.totalCostTv);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showData();

        sTotalTv.setText("₹ "+allTotalPrise);
        dFeeTv.setText("₹ "+40);
        allTotalPriseTv.setText("₹"+(allTotalPrise+40));
        totalCostTv.setText("₹"+allTotalPrise);

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, SavedAddressActivity.class);
                intent.putExtra("fromWhich","CartActivity");
                intent.putExtra(Constant.KEY_TOTAL_PRISE, ""+binding.totalCostTv.getText().toString().replace("₹",""));
                startActivity(intent);
            }
        });

        checkConnection();

    }

    private void checkConnection() {

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionReceiver(), intentFilter);
        ConnectionReceiver.Listener = this;

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {

        if (!isConnected) {
            binding.nes.setVisibility(View.GONE);
            binding.lottieAnimationView.setVisibility(View.VISIBLE);
            binding.lottieAnimationView.setAnimation(R.raw.no_internet_lottie);
            binding.linearLayout.setVisibility(View.GONE);
        }

    }

    public int cartCount(){
        int count = easyDB.getAllData().getCount();
        return count;
    }


    public void starActiv(String searchType){
        Intent intent = new Intent(CartActivity.this, SearchByTypeActivity.class);
        intent.putExtra("searchType", searchType);
        startActivity(intent);
        finish();
    }

    public void deleteCartData() {
        easyDB.deleteAllDataFromTable();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showData() {

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

        adapterCartitem = new AdapterCartItem(this, cartitemList);
        binding.cartItemRv.setAdapter(adapterCartitem);

        if(cartitemList.isEmpty()){
            binding.nes.setVisibility(View.GONE);
            binding.lottieAnimationView.setVisibility(View.VISIBLE);
            binding.linearLayout.setVisibility(View.GONE);
        }else{
            binding.lottieAnimationView.setVisibility(View.GONE);
            binding.nes.setVisibility(View.VISIBLE);
            binding.linearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
    }

}