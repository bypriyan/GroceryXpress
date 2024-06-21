package com.bypriyan.togocart.ui.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.CartActivity;
import com.bypriyan.togocart.activity.MainActivity;
import com.bypriyan.togocart.activity.OrdersActivity;
import com.bypriyan.togocart.activity.SavedAddressActivity;
import com.bypriyan.togocart.activity.SupportAndAllActivity;
import com.bypriyan.togocart.databinding.FragmentProfileBinding;
import com.bypriyan.togocart.register.LoginActivity;
import com.bypriyan.togocart.register.OtpActivity;
import com.bypriyan.togocart.ui.home.HomeFragment;
import com.bypriyan.togocart.utilities.CartItems;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.preferenceManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private preferenceManager preferenceManager;
    private String phoneNumber="";
    private FirebaseAuth firebaseAuth;
    private EasyDB easyDB;
    private CartItems cartItems;
    private int cartItemCount=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        preferenceManager = new preferenceManager(getActivity());

        phoneNumber = preferenceManager.getString(Constant.KEY_PHONE_NUMBER);
        firebaseAuth = FirebaseAuth.getInstance();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.shareImg.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        //
        cartItems = new CartItems(getActivity());
        cartItemCount = cartItems.loadCartItemsCount();

        easyDB = EasyDB.init(getActivity(), "ITEMS_DB")
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

        binding.phoneNum.setText("+91 "+phoneNumber);

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://togocart.blogspot.com/2022/07/privacy-policy.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        binding.termsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://togocart.blogspot.com/2022/07/terms-conditions.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        binding.contectUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), SupportAndAllActivity.class);
                intent.putExtra("viewType", "Support");
                startActivity(intent);
            }
        });

        binding.aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), SupportAndAllActivity.class);
                intent.putExtra("viewType", "About Us");
                startActivity(intent);
            }
        });

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), SupportAndAllActivity.class);
                intent.putExtra("viewType", "Profile");
                startActivity(intent);
            }
        });

        binding.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SavedAddressActivity.class);
                intent.putExtra("fromWhich","profileFrag");
                startActivity(intent);
            }
        });

        binding.orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OrdersActivity.class));
            }
        });

        binding.rateUsOnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.bypriyan.togocart")));
            }
        });

        binding.shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(bitmap);
            }
        });

        if(cartItemCount<=0){
            binding.above.setVisibility(View.GONE);
        }else{
            binding.above.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    private void shareImage(Bitmap bitmap) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        Uri bmpUri;
        String Text = "Hey, i'm using this app for buying vegetables, fruits and groceries, it's awesome, try this...\n\n https://play.google.com/store/apps/details?id=com.bypriyan.togocart&hl=en";
        bmpUri = saveImg(bitmap, getContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "NEW APP");
        intent.putExtra(Intent.EXTRA_TEXT, Text);
        startActivity(Intent.createChooser(intent, "invite to ToGo cart App"));

    }

    private Uri saveImg(Bitmap image, Context context) {

        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_images.jpeg");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    "com.bypriyan.togocart"+".provider",file);
        } catch (Exception e) {
            Log.d("check", "saveImg: "+e);
        }
        return uri;

    }


    public void deleteCartData() {
        easyDB.deleteAllDataFromTable();
    }

    private void logOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message = "Are you sure you want to LogOut?";
        builder.setMessage(Html.fromHtml("<font color='#1b1e28'>" + message + "</font>"));
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(getActivity(), "Signing out", Toast.LENGTH_SHORT).show();
                        preferenceManager.putString(Constant.KEY_PHONE_NUMBER,"");
                        preferenceManager.putString(Constant.KEY_ITEM_ID,""+1);
                        preferenceManager.putString(Constant.KEY_FIRST_NAME,"");
                        preferenceManager.putString(Constant.KEY_LAST_NAME,"");
                        preferenceManager.putString(Constant.KEY_LONGITUDE,"0.0");
                        preferenceManager.putString(Constant.KEY_LATITUDE,"0.0");
                        deleteCartData();
                        firebaseAuth.signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();

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

}