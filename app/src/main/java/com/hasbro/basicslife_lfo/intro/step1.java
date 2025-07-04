package com.hasbro.basicslife_lfo.intro;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hasbro.basicslife_lfo.GpsTracker;
import com.hasbro.basicslife_lfo.MainActivity;
import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.SafeClikcListener;
import com.hasbro.basicslife_lfo.Welcome;
import com.hasbro.basicslife_lfo.lfo_sales_view;
import com.hasbro.basicslife_lfo.geturl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class step1 extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    private RequestQueue mRequestQueue;

    private JSONArray storeGpsData;


    private String bio = "", phoneno = "", tmcode = "", user_name = "";
    private String selectedStoreName, selectedStoreCode, selectedGroupName, selectedStrId, selectedCarpetarea;

    private LinearLayout unitListContainer;
    private Button btnConfirmLocation;

    Retrofit retrofit = geturl.getClient();
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step1);

        unitListContainer = findViewById(R.id.unitListContainer);
        btnConfirmLocation = findViewById(R.id.btnConfirmLocation);

        setUserDetails();
        checkLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle Back Press
        setupBackPressedHandler();
        btnConfirmLocation.setEnabled(false);
        btnConfirmLocation.setAlpha(0.5f);
    }

    private void setUserDetails() {
        user_name = getIntent().getStringExtra("name");
        phoneno = getIntent().getStringExtra("mobile");
        bio = getIntent().getStringExtra("bio");
        tmcode = getIntent().getStringExtra("tmcode");
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchStoreData();
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchStoreData();
    }

    private void fetchStoreData() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        gpsTracker = new GpsTracker(this);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }

        double lat = gpsTracker.getLatitude();
        double lng = gpsTracker.getLongitude();

        System.out.println("userLatitude : " +lat);
        System.out.println("userLongitude : " +lng);

        String url = retrofit.baseUrl() + "getgpsdata?latitude=" + lat + "&longitude=" + lng + "&tmcode=" + tmcode;
        mRequestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.has("storegpsdata")) {
                    Toast.makeText(this, "No store data received", Toast.LENGTH_LONG).show();
                    return;
                }

                storeGpsData = obj.getJSONArray("storegpsdata");
                if (storeGpsData.length() == 0) {
                    Toast.makeText(this, "No stores found nearby", Toast.LENGTH_LONG).show();
                    return;
                }

                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));

                unitListContainer.removeAllViews();

                for (int i = 0; i < Math.min(storeGpsData.length(), 3); i++) {
                    JSONArray store = storeGpsData.getJSONArray(i);

                    double storeLat = store.getDouble(4);
                    double storeLng = store.getDouble(5);
                    LatLng storeLoc = new LatLng(storeLat, storeLng);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(storeLoc).title(store.getString(1)));
                    marker.setTag(store);

                    View unitView = LayoutInflater.from(this).inflate(R.layout.unit_option_item, unitListContainer, false);
                    TextView nameText = unitView.findViewById(R.id.unitName);
                    TextView addressText = unitView.findViewById(R.id.unitAddress);
                    ImageView checkIcon = unitView.findViewById(R.id.tickIcon);

                    String storeName = store.getString(1);
                    String storeGroup = store.getString(2);
                    String storeCode = store.getString(0);
                    String strid = store.getString(6);
                    String area = store.getString(7);

                    nameText.setText(storeName);
                    addressText.setText(storeGroup);

                    unitView.setOnClickListener(v -> {
                        selectedStoreName = storeName;
                        selectedGroupName = storeGroup;
                        selectedStoreCode = storeCode;
                        selectedStrId = strid;
                        selectedCarpetarea = area;

                        for (int j = 0; j < unitListContainer.getChildCount(); j++) {
                            View child = unitListContainer.getChildAt(j);
                            ImageView tick = child.findViewById(R.id.tickIcon);
                            tick.setVisibility(child == unitView ? View.VISIBLE : View.GONE);
                        }
                        btnConfirmLocation.setEnabled(true);
                        btnConfirmLocation.setAlpha(1f);
                    });

                    unitListContainer.addView(unitView);
                }

                btnConfirmLocation.setOnClickListener(v -> {
                    Intent intent = new Intent(this, step2.class);
                    intent.putExtra("strname", selectedStoreName);
                    intent.putExtra("strcode", selectedStoreCode);
                    intent.putExtra("grname", selectedGroupName);
                    intent.putExtra("strid", selectedStrId);
                    intent.putExtra("carea", selectedCarpetarea);
                    intent.putExtra("tmcode", tmcode);
                    intent.putExtra("mobile", phoneno);
                    intent.putExtra("name", user_name);
                    intent.putExtra("bio", bio);
                    startActivity(intent);
                    finish();
                });

            } catch (JSONException e) {
                Toast.makeText(this, "Error parsing store data", Toast.LENGTH_SHORT).show();
                Log.e("PARSE_ERROR", e.getMessage());
            } finally {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }, error -> {
            Toast.makeText(this, "Error fetching store data", Toast.LENGTH_LONG).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });

        mRequestQueue.add(request);
    }
    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                    logout();

            }
        });
    }

    private void logout() {

        new SweetAlertDialog(step1.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Sure You Want To Logout...")
                .setConfirmText("Stay")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        Intent it = new Intent(step1.this, MainActivity.class);
                        it.putExtra("mobile", phoneno);
                        it.putExtra("name", user_name);
                        it.putExtra("bio", bio);
                        startActivity(it);
                    }
                })
                .show();

    }

}
