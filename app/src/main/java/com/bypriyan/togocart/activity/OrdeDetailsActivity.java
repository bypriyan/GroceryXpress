package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.adapter.AdapterOrderItem;
import com.bypriyan.togocart.databinding.ActivityOrdeDetailsBinding;
import com.bypriyan.togocart.models.ModelOrderItems;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.FcmNotificationsSender;
import com.bypriyan.togocart.utilities.preferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class OrdeDetailsActivity extends AppCompatActivity {

    private ActivityOrdeDetailsBinding binding;
    private String fromActivity = "", timeStamp = "";
    private String orderNumber, area, city, mobileNumber, orderBy, orderCost, orderId, orderTime, orderStatus, expDelTime,
            phoneNumber, pinCode, propertyName, propertyLocation, reciverName, state, paymentStatus, deliveryPerson;

    private String subTotal, deliveryFees, couponCodeDiscount;

    private ArrayList<ModelOrderItems> orderItemList;
    private preferenceManager preferenceManager;
    private EasyDB easyDB;
    private String delPersonName, delPersonNumber, cartCount;
    static int PERMISSION_CODE= 100;
    private int progress =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(OrdeDetailsActivity.this, R.color.white));// set status background white
        binding = ActivityOrdeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fromActivity = getIntent().getStringExtra("fromActivity");
        timeStamp = getIntent().getStringExtra(Constant.KEY_TIMESTAMP);
        orderStatus = getIntent().getStringExtra(Constant.KEY_ORDERSTATUS);
        preferenceManager = new preferenceManager(OrdeDetailsActivity.this);

        orderStatusAnimation(orderStatus);

        loadProductDetails(timeStamp);
        loadItems(timeStamp);

        if (fromActivity.equals("paymentActivity")) {

            easyDB = EasyDB.init(OrdeDetailsActivity.this, "ITEMS_DB")
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

            int count = cartCount();
            cartCount = ""+count;


            ProgressDialog progressDialog = new ProgressDialog(OrdeDetailsActivity.this);
            String title ="Please Wait", message = "uploading cart items..";

            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(count);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setIcon(R.drawable.togo_notification);
            progressDialog.show();
            loadCartCount(timeStamp, progressDialog);
//            Toast.makeText(this, ""+cartCount, Toast.LENGTH_SHORT).show();
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupport();
            }
        });

        binding.calSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupport();
            }
        });

        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrderDialog("Canceled");
            }
        });

        binding.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber(delPersonNumber);
            }
        });

    }

    public void callNumber(String number){
        if (ContextCompat.checkSelfPermission(OrdeDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(OrdeDetailsActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

        }else{
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:"+number));
            startActivity(i);
        }
    }

    private void cancelOrderDialog(String orderStatus) {

        AlertDialog.Builder builder = new AlertDialog.Builder(OrdeDetailsActivity.this);

        String message = "Are you sure you want to Cancel this order?";
        builder.setMessage(Html.fromHtml("<font color='#1b1e28'>" + message + "</font>"));
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(OrdeDetailsActivity.this, "Order " + orderStatus, Toast.LENGTH_SHORT).show();
                        sendOrderStatus(orderStatus, deliveryPerson, timeStamp);
                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.white);
        alertDialog.show();

    }

    public void sendOrderStatus(String orderStatus, String deliveryPersonUid, String timeStamp) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_ORDERSTATUS, orderStatus);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_ORDERS);
        reference.child(timeStamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(OrdeDetailsActivity.this, "Order Accepted", Toast.LENGTH_SHORT).show();
                binding.cancelBtn.setVisibility(View.GONE);
                loadProductDetails(timeStamp);
                orderStatusAnimation("Canceled");
                sendNotificationToAll( "Order is " + orderStatus, "Order Id: " + timeStamp, timeStamp);
                if(!deliveryPersonUid.equals("noPerson")){
                    sendNotificationToDeliveryPerson(deliveryPersonUid,"Order is" + orderStatus, "Order Id: " + timeStamp, timeStamp );
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(OrdeDetailsActivity.this, "" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotificationToDeliveryPerson(String uid, String title, String description, String orderId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_TOKENS_DELIVERY_BOY);
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String token = ""+snapshot.child("token").getValue().toString();
                sendNotification(token,title, description, orderId);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(OrdeDetailsActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendNotificationToAll(String title, String description, String orderId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_TOKENS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String token = ""+ds.child("token").getValue().toString();
                    sendNotification(token,title, description, orderId);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(OrdeDetailsActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void sendNotification(String token, String title, String description, String orderId){
        Toast.makeText(this, "send Notification", Toast.LENGTH_SHORT).show();
        String fcmKey= preferenceManager.getString(Constant.KEY_FCM_SERVER_KEY);
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, title, description ,OrdeDetailsActivity.this, fcmKey);
        notificationsSender.SendNotifications();
    }

    private void orderStatusAnimation(String orderStatus) {
        if (orderStatus.equals("Order placed")) {
            binding.lottieAnimationView.setAnimation(R.raw.order_placed);
            binding.cancelBtn.setVisibility(View.VISIBLE);
        } else if (orderStatus.equals("Accepted")) {
            binding.lottieAnimationView.setAnimation(R.raw.accepted);
            binding.cancelBtn.setVisibility(View.VISIBLE);
        } else if (orderStatus.equals("Processed")) {
            binding.lottieAnimationView.setAnimation(R.raw.processed);
            binding.cancelBtn.setVisibility(View.VISIBLE);
        } else if (orderStatus.equals("Out for delivery")) {
            binding.lottieAnimationView.setAnimation(R.raw.out_for_delivery);
            binding.cancelBtn.setVisibility(View.GONE);
        } else if (orderStatus.equals("Delivered")) {
            binding.lottieAnimationView.setAnimation(R.raw.delivered);
            binding.cancelBtn.setVisibility(View.GONE);
        } else if (orderStatus.equals("Failed")) {
            binding.lottieAnimationView.setAnimation(R.raw.failed);
            binding.cancelBtn.setVisibility(View.GONE);
        } else if (orderStatus.equals("Canceled")) {
            binding.lottieAnimationView.setAnimation(R.raw.canceled);
            binding.cancelBtn.setVisibility(View.GONE);
        }
    }

    public void callSupport() {
        Intent intent = new Intent(OrdeDetailsActivity.this, SupportAndAllActivity.class);
        intent.putExtra("viewType", "Support");
        startActivity(intent);
    }

    private void loadItems(String timeStamp) {
        orderItemList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_ORDERS);
        reference.keepSynced(true);
        reference.child(timeStamp).child(Constant.KEY_ITEMS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                orderItemList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelOrderItems modelCartitem = ds.getValue(ModelOrderItems.class);
                    orderItemList.add(modelCartitem);
                }
                binding.recyclearCartItem.setAdapter(new AdapterOrderItem(OrdeDetailsActivity.this, orderItemList));
                binding.totalItems.setText("" + orderItemList.size());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadProductDetails(String timeStamp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_ORDERS);
        reference.child(timeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                orderId = "" + snapshot.child(Constant.KEY_ORDERID).getValue();
                orderNumber = "" + snapshot.child(Constant.KEY_ORDER_NUMBER).getValue();
                area = "" + snapshot.child(Constant.KEY_D_AREA).getValue();
                city = "" + snapshot.child(Constant.KEY_D_CITY).getValue();
                mobileNumber = "" + snapshot.child(Constant.KEY_D_MOBILE_NUMBER).getValue();
                orderBy = "" + snapshot.child(Constant.KEY_ORDERBY).getValue();
                orderCost = "" + snapshot.child(Constant.KEY_ORDERCOST).getValue();
                orderTime = "" + snapshot.child(Constant.KEY_ORDERTIME).getValue();
                orderStatus = "" + snapshot.child(Constant.KEY_ORDERSTATUS).getValue();
                phoneNumber = "" + snapshot.child(Constant.KEY_PHONE_NUMBER).getValue();
                pinCode = "" + snapshot.child(Constant.KEY_D_PINCODE).getValue();
                propertyName = "" + snapshot.child(Constant.KEY_D_PROPERTY_NAME).getValue();
                propertyLocation = "" + snapshot.child(Constant.KEY_D_PROPERTY_LOCATION).getValue();
                reciverName = "" + snapshot.child(Constant.KEY_D_RECIVER_NAME).getValue();
                state = "" + snapshot.child(Constant.KEY_D_STATE).getValue();
                paymentStatus = "" + snapshot.child(Constant.KEY_PAYMENTSTATUS).getValue();
                deliveryPerson = ""+snapshot.child(Constant.KEY_DELIVERY_PERSON).getValue();

                subTotal = ""+snapshot.child(Constant.KEY_SUB_TOTAL).getValue();
                deliveryFees = ""+snapshot.child(Constant.KEY_DELIVERY_FEES).getValue();
                couponCodeDiscount = ""+snapshot.child(Constant.KEY_COUPON_CODE_DISCOUNT).getValue().toString();

                Long time = Long.parseLong(orderTime);

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(time);
                String dateTime = DateFormat.format("dd MMM yyyy hh:mm aa", cal).toString();



                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                calendar.setTimeInMillis(time+Long.parseLong(preferenceManager.getString(Constant.KEY_EXPECTED_DELIVERYTIME)));
                String expDeliveryTime = DateFormat.format("hh:mm aa dd MMM", calendar).toString();

                if(deliveryPerson.equals("noPerson")){
                    binding.deliveryPersonName.setText("Waiting..");
                    binding.call.setVisibility(View.GONE);
                }else {
                    binding.call.setVisibility(View.VISIBLE);
                    loadDeliveryPersonDetails(deliveryPerson);
                }

                binding.dateTime.setText(dateTime);
                binding.orderStatus.setText(orderStatus);
                binding.orderNumber.setText(orderNumber);
                binding.paymentMode.setText(paymentStatus);
                binding.orderId.setText(orderId);
                binding.orderBy.setText(reciverName);
                binding.totalAmount.setText("₹" + orderCost);
                binding.phoneNum.setText(mobileNumber);
                binding.anotherPhoneNum.setText(phoneNumber);
                binding.reciverAddress.setText(propertyName + ", " + propertyLocation + ", " + area + ", " + city);

                //
                binding.sTotalTv.setText("₹" +subTotal);
                binding.dFeeTv.setText(deliveryFees);
                if(couponCodeDiscount.equals("noCouponCode")){
                    binding.couponDiscount.setText("No coupon code");
                }else{
                    binding.couponDiscount.setText(couponCodeDiscount);
                }
                binding.allTotalPriseTv.setText("₹" + orderCost);

                orderStatusAnimation(orderStatus);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(OrdeDetailsActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDeliveryPersonDetails(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_DELIVERY_BOYS);
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                delPersonName = ""+snapshot.child("name").getValue().toString();
                delPersonNumber = ""+snapshot.child("mobileNumber").getValue().toString();

                binding.deliveryPersonName.setText(delPersonName);
                binding.delPersonPhoneNumber.setText("+91 "+delPersonNumber);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void showSucessDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.order_sucessfull_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        MaterialButton ok = view.findViewById(R.id.okBtn);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        ok.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void loadCartCount(String timeStamp, ProgressDialog progressDialog) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_ORDERS);
        reference.child(timeStamp).child(Constant.KEY_ITEMS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String count = ""+snapshot.getChildrenCount();
                int firebaseProgress = Integer.parseInt(count);
                progressDialog.setProgress(firebaseProgress);
                if(cartCount.equals(count)){
                    deleteCartData();
                    progressDialog.dismiss();
                    showSucessDialog();
                }else{}
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    public void deleteCartData() {
        easyDB.deleteAllDataFromTable();
    }

    public int cartCount() {
        int count = easyDB.getAllData().getCount();
        if (count <= 0) {
            onBackPressed();
            Toast.makeText(this, "your cart is empty", Toast.LENGTH_SHORT).show();
        }
        return count;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromActivity.equals("paymentActivity"))
            startActivity(new Intent(this, MainActivity.class));
    }
}