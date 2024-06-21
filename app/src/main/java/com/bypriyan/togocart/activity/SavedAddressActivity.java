package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.adapter.AdapterCouponCodes;
import com.bypriyan.togocart.databinding.ActivitySavedAddressBinding;
import com.bypriyan.togocart.models.ModelCoupons;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.preferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavedAddressActivity extends AppCompatActivity {

    public ActivitySavedAddressBinding binding;
    private preferenceManager preferenceManager;
    private String[] pinCodes;
    private String isDelivAddAvail, fromActivity, totalAnount;
    private Double totalAmount = 0.0, delFees = 0.0;
    public AlertDialog alertDialog;
    public String orderTotalAmountFees, couponCodeId="noCouponCode", couponCodeDiscount ="0";
    //
    private ArrayList<ModelCoupons> couponsArrayList;
    private AdapterCouponCodes adapterCouponCodes;
    private ExecutorService executorService;

    private static final int LOCATION_REQUST_CODE = 100;
    private String[] locationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(SavedAddressActivity.this, R.color.white));// set status background white
        binding = ActivitySavedAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new preferenceManager(SavedAddressActivity.this);
        String pinCode = preferenceManager.getString(Constant.KEY_PIN_CODES);
        fromActivity = getIntent().getStringExtra("fromWhich");
        isDelivAddAvail = preferenceManager.getString(Constant.KEY_IS_D_ADDRESS_AVALIBLE);
        pinCodes = pinCode.split(",");
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        executorService = Executors.newSingleThreadExecutor();

        showButtons(fromActivity);
        loadDeliveryDetails();


        orderTotalAmountFees = binding.allTotalPriseTv.getText().toString().replace("₹", "");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                loadCouponCodes();
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        binding.currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedAddressActivity.this, GoogleMapActivity.class));
            }
        });

        binding.addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedAddressActivity.this, GoogleMapActivity.class));
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                if (isValid()) {
                    String pin = binding.pinCode.getEditText().getText().toString();
                    if (isPinCodeValid(pin)) {
                        saveDeliveryAddress();
                        onBackPressed();
                    } else {
                        loading(false);
                        showUnavilPinDialog(pin);
                    }

                } else {
                    loading(false);
                }
            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                if (isValid()) {
                    String pin = binding.pinCode.getEditText().getText().toString();
                    if (isPinCodeValid(pin)) {
                        saveDeliveryAddress();
                        Intent intent = new Intent(SavedAddressActivity.this, PymentActivity.class);
                        intent.putExtra(Constant.KEY_TOTAL_PRISE, binding.allTotalPriseTv.getText().toString().replace("₹", ""));
                        intent.putExtra(Constant.KEY_SUB_TOTAL, binding.sTotalTv.getText().toString().replace("₹", ""));
                        intent.putExtra(Constant.KEY_DELIVERY_FEES, binding.dFeeTv.getText().toString());
                        intent.putExtra(Constant.KEY_COUPON_CODE_DISCOUNT, binding.couponDiscount.getText().toString());
                        intent.putExtra(Constant.KEY_COUPON_ID, couponCodeId);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showUnavilPinDialog(pin);
                    }

                } else {
                    loading(false);
                }
            }
        });

        binding.discountCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDisCouponDialog();
            }
        });
    }

    private void loadDeliveryDetails() {
        this.isDelivAddAvail = preferenceManager.getString(Constant.KEY_IS_D_ADDRESS_AVALIBLE);
        if (isDelivAddAvail.equals("true")) {
            binding.deliveryAddressInfo.setVisibility(View.VISIBLE);
            binding.currentLocation.setVisibility(View.VISIBLE);
            binding.addDeliveryAddress.setVisibility(View.GONE);
            if (fromActivity.equals("CartActivity")) {
                binding.continueFrame.setVisibility(View.VISIBLE);
            }

            binding.name.getEditText().setText(preferenceManager.getString(Constant.KEY_D_RECIVER_NAME));
            binding.pinCode.getEditText().setText(preferenceManager.getString(Constant.KEY_D_PINCODE));
            binding.propertyName.getEditText().setText(preferenceManager.getString(Constant.KEY_D_PROPERTY_NAME));
            binding.propertyLocation.getEditText().setText(preferenceManager.getString(Constant.KEY_D_PROPERTY_LOCATION));
            binding.area.getEditText().setText(preferenceManager.getString(Constant.KEY_D_AREA));
            binding.city.getEditText().setText(preferenceManager.getString(Constant.KEY_D_CITY));
            binding.state.getEditText().setText(preferenceManager.getString(Constant.KEY_D_STATE));
            binding.phoneNum.getEditText().setText(preferenceManager.getString(Constant.KEY_D_MOBILE_NUMBER));


        } else {
            binding.deliveryAddressInfo.setVisibility(View.GONE);
            binding.currentLocation.setVisibility(View.GONE);
            binding.addDeliveryAddress.setVisibility(View.VISIBLE);
            binding.continueFrame.setVisibility(View.GONE);
            Toast.makeText(this, "please click on select location to add details", Toast.LENGTH_LONG).show();
        }

    }

    private void loadCouponCodes() {
        couponsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_COUPON_CODES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                couponsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCoupons modelCoupons = ds.getValue(ModelCoupons.class);
                    couponsArrayList.add(modelCoupons);
                }
                adapterCouponCodes = new AdapterCouponCodes(SavedAddressActivity.this, couponsArrayList, "true");

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void showDisCouponDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_coupon_codes, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        ImageView close = view.findViewById(R.id.close);
        ImageView backBtn = view.findViewById(R.id.backBtn);
        RecyclerView recyclearCouponCodes = view.findViewById(R.id.recyclearCouponCodes);
        EditText couopnCodeEd = view.findViewById(R.id.couopnCodeEd);
        TextView applyBtn = view.findViewById(R.id.applyBtn);

        alertDialog = builder.create();
        recyclearCouponCodes.setAdapter(adapterCouponCodes);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        applyBtn.setOnClickListener(v -> {
            if (couopnCodeEd.getText().toString().isEmpty()) {
                couopnCodeEd.setError("Empty");
                couopnCodeEd.requestFocus();
            } else {
                String couponCode = couopnCodeEd.getText().toString().toUpperCase().trim();
                checkCouponCode(couponCode);
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void checkCouponCode(String couponCode) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COUPON_CODES);
        Query query = reference.orderByChild("couponCode").equalTo(couponCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds: snapshot.getChildren()){
                        ModelCoupons modelCoupons = ds.getValue(ModelCoupons.class);
                        String id = modelCoupons.getCouponId();
                        String couponCode = modelCoupons.getCouponCode();
                        String couponDescription = modelCoupons.getCouponDescription();
                        String couponDiscountPercentage = modelCoupons.getCouponDiscount();
                        String couponMinimumOrder = modelCoupons.getCouponMinimumOrder();
                        String couponTimeUse = modelCoupons.getCouponTimeUsage();

                        if(couponTimeUse.equals("Multitime Use")){
                            if(isValid(couponMinimumOrder)){
                                int discountPer = Integer.parseInt(couponDiscountPercentage);
                                double orderTotalAmount = Double.parseDouble(orderTotalAmountFees);
                                double discountPrise = Math.ceil(orderTotalAmount*discountPer/100);
                                String totalAmountAterDiscount = String.valueOf(orderTotalAmount-discountPrise);
                                binding.couponDiscount.setText("-₹"+discountPrise);
                                binding.allTotalPriseTv.setText("₹"+totalAmountAterDiscount);
                                binding.couponCode.setText(couponCode + " " + "applied");
                                binding.couponCodeSaved.setText("You saved ₹" + discountPrise + " with this coupon");
                                couponCodeId = id;
                                alertDialog.dismiss();
                            }else{
                                Toast.makeText(SavedAddressActivity.this, "Minimum cart total amount to apply this coupon code is " + couponMinimumOrder, Toast.LENGTH_LONG).show();
                            }
                        }else if(couponTimeUse.equals("Single time use")){
                            checkCouponIsUsed(id, couponCode, couponDescription, couponDiscountPercentage, couponMinimumOrder);
                        }
                    }

                } else {
                    Toast.makeText(SavedAddressActivity.this, "Invalid coupon code ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private boolean isValid(String minimumOrderPrice) {

        String totalAmount = binding.sTotalTv.getText().toString().replace("₹", "");
        Double totalPrise = Double.parseDouble(totalAmount);
        Double minOrderAmountPrise = Double.parseDouble(minimumOrderPrice);
        if (totalPrise >= minOrderAmountPrise) {
            return true;
        } else {
            return false;
        }

    }

    private void checkCouponIsUsed(String id, String couponCode, String couponDescription, String couponDiscountPercentage, String couponMinimumOrder) {
        String uid = FirebaseAuth.getInstance().getUid().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COUPON_CODES).child(id).child(Constant.KEY_COUPON_USED_USERS);

        Query query = reference.orderByChild(Constant.KEY_UID).equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(SavedAddressActivity.this, "You already used this Coupon Code", Toast.LENGTH_LONG).show();
                }else{
                    if(isValid(couponMinimumOrder)){
                        int discountPer = Integer.parseInt(couponDiscountPercentage);
                        double orderTotalAmount = Double.parseDouble(orderTotalAmountFees);
                        double discountPrise = Math.ceil(orderTotalAmount*discountPer/100);
                        String totalAmountAterDiscount = String.valueOf(orderTotalAmount-discountPrise);
                        binding.couponDiscount.setText("-₹"+discountPrise);
                        binding.allTotalPriseTv.setText("₹"+totalAmountAterDiscount);
                        binding.couponCode.setText(couponCode + " " + "applied");
                        binding.couponCodeSaved.setText("You saved ₹" + discountPrise + " with this coupon");
                        couponCodeId = id;
                        alertDialog.dismiss();
                    }else{
                        Toast.makeText(SavedAddressActivity.this, "Minimum cart total amount to apply this coupon code is " + couponMinimumOrder, Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void showButtons(String fromActivity) {
        if (fromActivity.equals("CartActivity")) {
            binding.saveDelAddFrame.setVisibility(View.GONE);
            binding.continueFrame.setVisibility(View.VISIBLE);
            binding.title.setText("Delivery Details");
            binding.checkoutDet.setVisibility(View.VISIBLE);
            String amount = getIntent().getStringExtra(Constant.KEY_TOTAL_PRISE);
            totalAmount = Double.parseDouble(amount);

            binding.sTotalTv.setText("₹" + totalAmount);
            if (totalAmount <= 499) {
                binding.dFeeTv.setText("₹40");
                Double totl = totalAmount + 40;
                binding.allTotalPriseTv.setText("₹" + totl);
                totalAnount = "" + totl;
            } else {
                binding.dFeeTv.setTextColor(getResources().getColor(R.color.green));
                binding.dFeeTv.setText("Free");
                binding.allTotalPriseTv.setText("₹" + totalAmount);
                totalAnount = "" + totalAmount;
            }
        } else {
            binding.saveDelAddFrame.setVisibility(View.VISIBLE);
            binding.continueFrame.setVisibility(View.GONE);
            binding.title.setText("Delivery Address");
            binding.checkoutDet.setVisibility(View.GONE);
        }
    }

    private void saveDeliveryAddress() {
        preferenceManager.putString(Constant.KEY_IS_D_ADDRESS_AVALIBLE, "true");
        preferenceManager.putString(Constant.KEY_D_RECIVER_NAME, "" + binding.name.getEditText().getText().toString());
        preferenceManager.putString(Constant.KEY_D_PINCODE, "" + binding.pinCode.getEditText().getText().toString());
        preferenceManager.putString(Constant.KEY_D_PROPERTY_NAME, "" + binding.propertyName.getEditText().getText().toString());
        preferenceManager.putString(Constant.KEY_D_PROPERTY_LOCATION, "" + binding.propertyLocation.getEditText().getText().toString());
        preferenceManager.putString(Constant.KEY_D_AREA, "" + binding.area.getEditText().getText().toString());
        preferenceManager.putString(Constant.KEY_D_CITY, "" + binding.city.getEditText().getText().toString());
        preferenceManager.putString(Constant.KEY_D_STATE, "" + binding.state.getEditText().getText().toString());
        preferenceManager.putString(Constant.KEY_D_MOBILE_NUMBER, "" + binding.phoneNum.getEditText().getText().toString());

        Toast.makeText(this, "Delivery Address Saved Successfully", Toast.LENGTH_SHORT).show();

        loading(false);
    }

    private boolean isPinCodeValid(String pinCode) {
        boolean avil = false;
        for (int i = 0; i < pinCodes.length; i++) {
            if (pinCodes[i].equals(pinCode)) {
                avil = true;
                break;
            } else {
                avil = false;
            }
        }
        return avil;
    }

    private boolean isValid() {
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            binding.name.setError("Empty");
            binding.name.requestFocus();
            Toast.makeText(this, "please click on select location to add detail", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.pinCode.getEditText().getText().toString().isEmpty() || binding.pinCode.getEditText().getText().toString().length() != 6) {
            binding.pinCode.setError("not valid");
            binding.pinCode.requestFocus();
            Toast.makeText(this, "please click on select location to add detail", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.propertyName.getEditText().getText().toString().isEmpty()) {
            binding.propertyName.setError("Empty");
            binding.propertyName.requestFocus();
            Toast.makeText(this, "please click on select location to add detail", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.propertyLocation.getEditText().getText().toString().isEmpty()) {
            binding.propertyLocation.setError("Empty");
            binding.propertyLocation.requestFocus();
            Toast.makeText(this, "please click on select location to add detail", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.area.getEditText().getText().toString().isEmpty()) {
            binding.area.setError("Empty");
            binding.area.requestFocus();
            return false;
        } else if (binding.city.getEditText().getText().toString().isEmpty()) {
            binding.city.setError("Empty");
            binding.city.requestFocus();
            Toast.makeText(this, "please click on select location to add detail", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.state.getEditText().getText().toString().isEmpty()) {
            binding.state.setError("Empty");
            binding.state.requestFocus();
            Toast.makeText(this, "please click on select location to add detail", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.phoneNum.getEditText().getText().toString().isEmpty() || binding.phoneNum.getEditText().getText().toString().length() != 10) {
            binding.phoneNum.setError("not valid");
            binding.phoneNum.requestFocus();
            Toast.makeText(this, "please click on select location to add detail", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isloading) {
        if (isloading) {
            binding.save.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.save.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void showUnavilPinDialog(String pinCode) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_invalid_pincode, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(view);

        MaterialButton button = view.findViewById(R.id.ok);
        TextView textTv = view.findViewById(R.id.textTv);

        textTv.setText("Oops! our services are currently not available to this " + pinCode + " Address. we will start our services to this " + pinCode + " address Soon.");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    @Override
    protected void onResume() {
        loadDeliveryDetails();
        super.onResume();
    }
}