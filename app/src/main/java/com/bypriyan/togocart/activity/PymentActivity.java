package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.databinding.ActivityPymentBinding;
import com.bypriyan.togocart.models.ModelCartitem;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.FcmNotificationsSender;
import com.bypriyan.togocart.utilities.preferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;
import papaya.in.sendmail.SendMail;

public class PymentActivity extends AppCompatActivity implements PaymentResultListener {

    private ActivityPymentBinding binding;
    private preferenceManager preferenceManager;
    private EasyDB easyDB;
    private String totalAmount, propertyName, propertyLocation, area, city, reciverName, longitude, latitude;
    private ArrayList<ModelCartitem> cartitemList;
    private FirebaseAuth firebaseAuth;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private String timeStamp = "";
    int cartSize = 0;
    String cartCount="4";
    private String subTotal, deliveryFees, couponCodeDiscount, couponCodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(PymentActivity.this, R.color.white));// set status background white
        binding = ActivityPymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new preferenceManager(PymentActivity.this);
        Checkout.preload(PymentActivity.this);

        totalAmount = getIntent().getStringExtra(Constant.KEY_TOTAL_PRISE);
        subTotal = getIntent().getStringExtra(Constant.KEY_SUB_TOTAL);
        deliveryFees = getIntent().getStringExtra(Constant.KEY_DELIVERY_FEES);
        couponCodeDiscount = getIntent().getStringExtra(Constant.KEY_COUPON_CODE_DISCOUNT);
        couponCodeId = getIntent().getStringExtra(Constant.KEY_COUPON_ID);

        propertyName = preferenceManager.getString(Constant.KEY_D_PROPERTY_NAME);
        reciverName = preferenceManager.getString(Constant.KEY_D_RECIVER_NAME);
        propertyLocation = preferenceManager.getString(Constant.KEY_D_PROPERTY_LOCATION);
        longitude = preferenceManager.getString(Constant.KEY_LONGITUDE);
        latitude = preferenceManager.getString(Constant.KEY_LATITUDE);
        area = preferenceManager.getString(Constant.KEY_D_AREA);
        city = preferenceManager.getString(Constant.KEY_D_CITY);
        firebaseAuth = FirebaseAuth.getInstance();

        loadTime();
        LoadCart();

        easyDB = EasyDB.init(PymentActivity.this, "ITEMS_DB")
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

        binding.itemCount.setText("" + cartCount() + " items");
        cartCount = ""+cartCount();
        binding.totalAmount.setText("Bill Amount: ₹" + totalAmount);
        binding.nameOfReciver.setText(reciverName);
        binding.delivlocation.setText(propertyName + ", " + propertyLocation + ", " + area + ", " + city);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.cashOnDeliveryOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceOrderDialog();
            }
        });

        binding.payOnlineOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PymentActivity.this, OnlinePaymentActivity.class));
                Double enteredAmount = Double.parseDouble(totalAmount);
                Double finalAmount = enteredAmount * 100;
                timeStamp = "" + System.currentTimeMillis();
                startPayment(String.valueOf(finalAmount));
            }
        });

        cartSize = cartitemList.size() - 1;

    }

    private void loadTime() {

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 19 && timeOfDay < 24) {
            // tommarow
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = c.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String tomorrowAsString = dateFormat.format(tomorrow);
            binding.estimateDeliveryTime.setText("10:30 Am " + tomorrowAsString);

        } else if (timeOfDay >= 0 && timeOfDay < 8) {
            //today
            Date today = c.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String todayAsString = dateFormat.format(today);
            binding.estimateDeliveryTime.setText("10:30 Am " + todayAsString);

        } else {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(System.currentTimeMillis() + Long.parseLong(preferenceManager.getString(Constant.KEY_EXPECTED_DELIVERYTIME)));
            String expDeliveryTime = DateFormat.format("hh:mm aa dd MMM", calendar).toString();
            binding.estimateDeliveryTime.setText("" + expDeliveryTime);
        }

    }

    public int cartCount() {
        int count = easyDB.getAllData().getCount();
        if (count <= 0) {
            onBackPressed();
            Toast.makeText(this, "your cart is empty", Toast.LENGTH_SHORT).show();
        }
        return count;
    }

    private void showPlaceOrderDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.place_order_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        MaterialButton ok = view.findViewById(R.id.okBtn);
        TextView cancelBtn = view.findViewById(R.id.cancelBtn);
        LinearLayout buttonsLinear = view.findViewById(R.id.buttonsLinear);
        ProgressBar progressBar = view.findViewById(R.id.progressbarD);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setGravity(Gravity.CENTER);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                buttonsLinear.setVisibility(View.GONE);
                timeStamp = "" + System.currentTimeMillis();
                submitOrder("Cash on delivery", timeStamp);
                alertDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void LoadCart() {
        cartitemList = new ArrayList<>();
        EasyDB easyDB = EasyDB.init(PymentActivity.this, "ITEMS_DB")
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

    }

//    public void startPayment(String famount) {
//
//        String samount = String.valueOf(famount);
//        int amount = Math.round(Float.parseFloat(samount) * 100);
//
//        Checkout checkout = new Checkout();
//        checkout.setKeyID("rzp_live_16KDRkBw95pNkO");
//        checkout.setImage(R.drawable.togo_cart_icon);
//
//        final Activity activity = this;
//        try {
//            JSONObject options = new JSONObject();
//            options.put("name", "ToGo Cart");
//            options.put("description", "");
//            options.put("order_id","!232" );
//            options.put("send_sms_hash", true);
//            options.put("allow_rotation", false);
//            options.put("currency", "INR");
//            options.put("amount", amount);
//
////            JSONObject retryObj = new JSONObject();
////            retryObj.put("enabled", true);
////            retryObj.put("max_count", 4);
////            options.put("retry", retryObj);
//
//            JSONObject preFill = new JSONObject();
//            preFill.put("contact", "" + preferenceManager.getString(Constant.KEY_PHONE_NUMBER));
//            options.put("prefill", preFill);
//            checkout.open(activity, options);
//        } catch (Exception e) {
//            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//            e.printStackTrace();
//        }
//
//    }

    public void startPayment(String price) {

        Checkout checkout = new Checkout();

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", "TOGO Cart");
            options.put("description", "Payment for Order");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", false);

            //You can omit the image option to fetch the image from dashboard
            options.put("currency", "INR");
            options.put("amount", price);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "email");
            preFill.put("contact", "" + preferenceManager.getString(Constant.KEY_PHONE_NUMBER));

            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }

    }

    private void submitOrder(String paymentStatus, String timeStamp) {

        Random random = new Random();
        int orderNumber = random.nextInt(99999 - 1111) + 1111;

        String cost = totalAmount;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_ORDERID, "" + timeStamp);
        hashMap.put(Constant.KEY_ORDERTIME, "" + timeStamp);
        hashMap.put(Constant.KEY_ORDERSTATUS, "Order placed");
        hashMap.put(Constant.KEY_ORDERCOST, "" + cost);
        hashMap.put(Constant.KEY_ORDERBY, "" + firebaseAuth.getUid());
        hashMap.put(Constant.KEY_ORDER_NUMBER, "" + Integer.toString(orderNumber));
        hashMap.put(Constant.KEY_PAYMENTSTATUS, "" + paymentStatus);
        hashMap.put(Constant.KEY_ESTIMATE_D_TIME, "" + binding.estimateDeliveryTime.getText().toString());
        //delivery Address
        hashMap.put(Constant.KEY_D_RECIVER_NAME, "" + reciverName);
        hashMap.put(Constant.KEY_DELIVERY_PERSON, "noPerson");
        hashMap.put(Constant.KEY_D_PROPERTY_NAME, "" + propertyName);
        hashMap.put(Constant.KEY_D_PROPERTY_LOCATION, "" + propertyLocation);
        hashMap.put(Constant.KEY_D_AREA, "" + area);
        hashMap.put(Constant.KEY_D_PINCODE, "" + preferenceManager.getString(Constant.KEY_D_PINCODE));
        hashMap.put(Constant.KEY_D_CITY, "" + city);
        hashMap.put(Constant.KEY_D_STATE, "" + preferenceManager.getString(Constant.KEY_D_STATE));
        hashMap.put(Constant.KEY_D_MOBILE_NUMBER, "" + preferenceManager.getString(Constant.KEY_D_MOBILE_NUMBER));
        hashMap.put(Constant.KEY_PHONE_NUMBER, "" + preferenceManager.getString(Constant.KEY_PHONE_NUMBER));
        hashMap.put(Constant.KEY_LATITUDE, "" + latitude);
        hashMap.put(Constant.KEY_LONGITUDE, "" + longitude);
        //
        hashMap.put(Constant.KEY_SUB_TOTAL, subTotal);
        hashMap.put(Constant.KEY_DELIVERY_FEES, deliveryFees);
        hashMap.put(Constant.KEY_COUPON_CODE_DISCOUNT, couponCodeDiscount);
        hashMap.put(Constant.KEY_COUPON_ID, couponCodeId);


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_ORDERS);
        reference.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        uploadCartItem();
                        Toast.makeText(PymentActivity.this, "Order Placed Successfully", Toast.LENGTH_LONG).show();

                        sendNotificationToAll("New Order Alert", "Order Id: " + timeStamp, timeStamp);
                        //send sms

                        //sending email
                        SendMail mail = new SendMail("togocart0099@gmail.com", "znbkepuwktmjkucn",
                                "togocart0099@gmail.com",
                                "New Order Alert",
                                "Hi ToGo Cart,\nYou have received a new order on your Application. Below are the order details.\n\n\n" +

                                        "Delivery Details-:\n\n" +
                                        "Total Item(s) - " + cartitemList.size() + "\n" +
                                        "Order Value - " + cost + "\n" +
                                        "Payment Mode - " + paymentStatus + "\n" +
                                        "Order No - " + Integer.toString(orderNumber) + "\n" +
                                        "Order Id - " + timeStamp + "\n" +
                                        "Delivery Location - " + propertyName + ", " + propertyLocation + ", " + area + ", " + city + "(" + preferenceManager.getString(Constant.KEY_D_PINCODE) + "), " + preferenceManager.getString(Constant.KEY_D_STATE) + "\n\n\n" +

                                        "Customer Details-:\n\n" +
                                        "Name - " + reciverName + "\n" +
                                        "Delivery Location - " + propertyName + ", " + propertyLocation + ", " + area + ", " + city + "(" + preferenceManager.getString(Constant.KEY_D_PINCODE) + "), " + preferenceManager.getString(Constant.KEY_D_STATE) + "\n" +
                                        "Phone No(1). - " + preferenceManager.getString(Constant.KEY_PHONE_NUMBER) + "\n" +
                                        "Phone No(2). - " + preferenceManager.getString(Constant.KEY_D_MOBILE_NUMBER) + "\n");
                        mail.execute();

                        if (couponCodeId.equals("noCouponCode")) {
                            Intent intent = new Intent(PymentActivity.this, OrdeDetailsActivity.class);
                            intent.putExtra("fromActivity", "paymentActivity");
                            intent.putExtra(Constant.KEY_TIMESTAMP, timeStamp);
                            intent.putExtra(Constant.KEY_ORDERSTATUS, "Order placed");
                            intent.putExtra("cartCount", ""+cartCount);
                            startActivity(intent);
                            finish();
                        } else {
                            setCouopnCodeUsed(couponCodeId);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(PymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    int i = 0;
    DatabaseReference referenceCart = FirebaseDatabase.getInstance().getReference(Constant.KEY_ORDERS);

    private void uploadCartItem() {
        if (cartSize < i) {
            return;
        }
        String id = cartitemList.get(i).getId();
        String pId = cartitemList.get(i).getpId();
        String name = cartitemList.get(i).getName();
        String prise = cartitemList.get(i).getPrice();
        String cost = cartitemList.get(i).getCost();
        String quentity = cartitemList.get(i).getQuentity();
        String pImg = cartitemList.get(i).getpImg();
        String pQuentity = cartitemList.get(i).getpQuentity();

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("pId", pId);
        hashMap1.put("name", name);
        hashMap1.put("cost", cost);
        hashMap1.put("price", prise);
        hashMap1.put("quantity", quentity);
        hashMap1.put("pImg", pImg);
        hashMap1.put("pQuantity", pQuentity);

        referenceCart.child(timeStamp).child(Constant.KEY_ITEMS).push().setValue(hashMap1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        i++;
                        uploadCartItem();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                uploadCartItem();
            }
        });
//            try {
//                Thread.sleep(1500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

    }

    private void setCouopnCodeUsed(String couponCodeId) {
        String uid = "" + firebaseAuth.getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID, uid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COUPON_CODES);
        reference.child(couponCodeId).child(Constant.KEY_COUPON_USED_USERS)
                .child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(PymentActivity.this, OrdeDetailsActivity.class);
                intent.putExtra("fromActivity", "paymentActivity");
                intent.putExtra(Constant.KEY_TIMESTAMP, timeStamp);
                intent.putExtra(Constant.KEY_ORDERSTATUS, "Order placed");
                intent.putExtra("cartCount", ""+cartCount);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendNotificationToAll(String title, String description, String orderId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_TOKENS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String token = "" + ds.child("token").getValue().toString();
                    sendNotification(token, title, description, orderId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(PymentActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void sendNotification(String token, String title, String description, String orderId) {
        String fcmKey = preferenceManager.getString(Constant.KEY_FCM_SERVER_KEY);
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, title, description, PymentActivity.this, fcmKey);
        notificationsSender.SendNotifications();
    }

    public void deleteCartData() {
        easyDB.deleteAllDataFromTable();
    }

    @Override
    public void onPaymentSuccess(String s) {
        submitOrder("paid online", timeStamp);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment failed ", Toast.LENGTH_SHORT).show();
    }

}