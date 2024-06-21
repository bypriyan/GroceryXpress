package com.bypriyan.togocart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.databinding.ActivityGoogleMapBinding;
import com.bypriyan.togocart.databinding.ActivitySavedAddressBinding;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.preferenceManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class GoogleMapActivity extends AppCompatActivity {

    public ActivityGoogleMapBinding binding;
    private preferenceManager preferenceManager;
    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private int REQUEST_CODE=111;
    private ConnectivityManager manager;
    private NetworkInfo networkInfo;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private Double selLatitude=0.0, selLongitude=0.0;
    private String city, state, pinCode, area;
    private static GoogleApiClient mGoogleApiClient;
    private String isDelivAddAvail;
    public String nameD, phoneNumD, propertyNameD, propertyLocationD, areasD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(GoogleMapActivity.this, R.color.white));// set status background white
        binding = ActivityGoogleMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new preferenceManager(GoogleMapActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(GoogleMapActivity.this);
        mGoogleApiClient = new GoogleApiClient.Builder(GoogleMapActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
        isDelivAddAvail = preferenceManager.getString(Constant.KEY_IS_D_ADDRESS_AVALIBLE);

        shwoBottomSheet();

//        if (isDelivAddAvail.equals("true")) {
//            loadDeliveryDetails();
//        }

        if (ActivityCompat.checkSelfPermission(GoogleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            loading(true);
            getLocation();
        }else{
            ActivityCompat.requestPermissions(GoogleMapActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(city!= null && state!= null&& pinCode!= null && nameD!= null && phoneNumD != null){
                    preferenceManager.putString(Constant.KEY_IS_D_ADDRESS_AVALIBLE, "true");
                    preferenceManager.putString(Constant.KEY_D_CITY, city);
                    preferenceManager.putString(Constant.KEY_D_STATE, state);
                    preferenceManager.putString(Constant.KEY_D_PINCODE, pinCode);
                    preferenceManager.putString(Constant.KEY_D_RECIVER_NAME, nameD);
                    preferenceManager.putString(Constant.KEY_D_MOBILE_NUMBER, phoneNumD);
                    preferenceManager.putString(Constant.KEY_D_AREA,areasD);
                    preferenceManager.putString(Constant.KEY_D_PROPERTY_NAME, propertyNameD);
                    preferenceManager.putString(Constant.KEY_D_PROPERTY_LOCATION,propertyLocationD);
                    preferenceManager.putString(Constant.KEY_LATITUDE, String.valueOf(selLatitude));
                    preferenceManager.putString(Constant.KEY_LONGITUDE, String.valueOf(selLongitude));
                    Toast.makeText(GoogleMapActivity.this, "Location added successfully", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }else{
                    Toast.makeText(GoogleMapActivity.this, "Please select a delivery location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void locationOn() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        getCurrentLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(GoogleMapActivity.this, 123);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }

    private void shwoBottomSheet() {

        final Dialog dialog = new Dialog(GoogleMapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_save_current_address);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        MaterialButton save = dialog.findViewById(R.id.save);
        TextInputLayout name = dialog.findViewById(R.id.name);
        TextInputLayout phoneNum = dialog.findViewById(R.id.phoneNum);
        TextInputLayout propertyName = dialog.findViewById(R.id.propertyName);
        TextInputLayout propertyLocation = dialog.findViewById(R.id.propertyLocation);
        TextInputLayout area = dialog.findViewById(R.id.area);
        ImageView close = dialog.findViewById(R.id.close);

        if (isDelivAddAvail.equals("true")) {
            name.getEditText().setText(preferenceManager.getString(Constant.KEY_D_RECIVER_NAME));
            phoneNum.getEditText().setText(preferenceManager.getString(Constant.KEY_D_MOBILE_NUMBER));

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid(name, phoneNum, propertyName, propertyLocation, area)){
                    nameD = name.getEditText().getText().toString();
                    phoneNumD = phoneNum.getEditText().getText().toString();
                    propertyNameD = propertyName.getEditText().getText().toString();
                    propertyLocationD = propertyLocation.getEditText().getText().toString();
                    areasD = area.getEditText().getText().toString();

                    if(nameD != null && phoneNumD != null && propertyNameD != null && propertyLocationD != null && areasD != null){
                        getLocation();
                        dialog.dismiss();
                    }

                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private boolean isValid(TextInputLayout name, TextInputLayout phoneNum, TextInputLayout propertyName, TextInputLayout propertyLocation, TextInputLayout area) {
        if (name.getEditText().getText().toString().isEmpty()) {
            name.setError("Empty");
            name.requestFocus();
            return false;
        } else if (propertyName.getEditText().getText().toString().isEmpty()) {
            propertyName.setError("Empty");
            propertyName.requestFocus();
            return false;
        } else if (propertyLocation.getEditText().getText().toString().isEmpty()) {
            propertyLocation.setError("Empty");
            propertyLocation.requestFocus();
            return false;
        } else if (area.getEditText().getText().toString().isEmpty()) {
            area.setError("Empty");
            area.requestFocus();
            return false;
        } else if (phoneNum.getEditText().getText().toString().isEmpty() || phoneNum.getEditText().getText().toString().length() != 10) {
            phoneNum.setError("not valid");
            phoneNum.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void getLocation() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(GoogleMapActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                locationOn();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(GoogleMapActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!= null){
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                            mMap = googleMap;
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Delivery location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                            googleMap.addMarker(markerOptions).showInfoWindow();

                            checkConnection();
                            if(networkInfo.isConnected() && networkInfo.isAvailable()){
                                findAdress(location.getLatitude(), location.getLongitude());
                            }else{
                                Toast.makeText(GoogleMapActivity.this, "please check connection", Toast.LENGTH_SHORT).show();
                            }

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(@NonNull @NotNull LatLng latLng) {
                                    loading(true);
                                    checkConnection();
                                    if(networkInfo.isConnected() && networkInfo.isAvailable()){
                                        binding.area.setText("Loading...");
                                        binding.address.setText("....");
                                        selLatitude = latLng.latitude;
                                        selLongitude = latLng.longitude;
                                        findAdress(selLatitude, selLongitude);
                                    }else{
                                        Toast.makeText(GoogleMapActivity.this, "please check connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                }
            }
        });
    }

    private void findAdress(Double selLatitude, Double selLongitude) {
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(selLatitude, selLongitude, 1);
             city = addresses.get(0).getLocality();
             state = addresses.get(0).getAdminArea();
             pinCode = addresses.get(0).getPostalCode();
             area = addresses.get(0).getFeatureName();

            String mAddress = addresses.get(0).getAddressLine(0);
            binding.area.setText(area);
            binding.address.setText(mAddress);

            if(mAddress!= null){
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(selLatitude, selLongitude);
                markerOptions.position(latLng).title("Delivery location");
                mMap.addMarker(markerOptions);
                this.selLatitude = selLatitude;
                this.selLongitude = selLongitude;
            }else{
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
            loading(false);

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocation();
            }

        }else{
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkConnection(){
        manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
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

}