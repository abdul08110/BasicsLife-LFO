package com.hasbro.basicslife_lfo.intro;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hasbro.basicslife_lfo.GpsTracker;
import com.hasbro.basicslife_lfo.MainActivity;
import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.geturl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class stepOne extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    private RequestQueue mRequestQueue;

    private JSONArray storeGpsData;


    private String bio = "", phoneno = "", tmcode = "", user_name = "";
    private String selectedStoreName, selectedStoreCode, selectedGroupName, selectedStrId, selectedCarpetarea;

    private LinearLayout unitListContainer;
    private Button btnConfirmLocation;
    private boolean isFirstMapLoad = true;


    Retrofit retrofit = geturl.getClient();
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stepone);

        unitListContainer = findViewById(R.id.unitListContainer);
//btnConfirmLocation = findViewById(R.id.btnConfirmLocation);

        setUserDetails();
        checkLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle Back Press
        setupBackPressedHandler();
      //  btnConfirmLocation.setEnabled(false);
//btnConfirmLocation.setAlpha(0.5f);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            gpsTracker = new GpsTracker(this);
            if (gpsTracker.canGetLocation()) {
                double lat = gpsTracker.getLatitude();
                double lng = gpsTracker.getLongitude();

                LatLng myLocation = new LatLng(lat, lng);

                // Add marker
                mMap.addMarker(new MarkerOptions()
                        .position(myLocation)
                        .title("You are here"));

                // Move camera
                if (isFirstMapLoad) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                    isFirstMapLoad = false;
                }

            }
        }

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

        String url = retrofit.baseUrl() + "getgpsdata?latitude=" + lat + "&longitude=" + lng + "&tmcode=" + tmcode;
        mRequestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.has("store_list")) {
                    Toast.makeText(this, "No nearby store found", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray storeArray = obj.getJSONArray("store_list");
                unitListContainer.removeAllViews();

                for (int i = 0; i < storeArray.length(); i++) {
                    JSONObject storeObj = storeArray.getJSONObject(i);
                    String strId = storeObj.getString("str_id");
                    String storeName = storeObj.getString("store_name");
                    String groupName = storeObj.getString("group_name");
                    String storeCode = storeObj.getString("store_code");

                    View unitView = LayoutInflater.from(this).inflate(R.layout.unit_option_item, unitListContainer, false);
                    TextView nameText = unitView.findViewById(R.id.unitName);
                    TextView addressText = unitView.findViewById(R.id.unitAddress);
                    ImageView checkIcon = unitView.findViewById(R.id.tickIcon);

                    nameText.setText(storeName);
                    addressText.setText(groupName);

                    checkIcon.setVisibility(View.GONE);
                    unitView.setOnClickListener(v -> {
                        for (int j = 0; j < unitListContainer.getChildCount(); j++) {
                            View child = unitListContainer.getChildAt(j);
                            ImageView tick = child.findViewById(R.id.tickIcon);
                            tick.setVisibility(child == unitView ? View.VISIBLE : View.GONE);

                            child.setBackgroundColor(child == unitView ? Color.parseColor("#E0F7FA") : Color.TRANSPARENT);
                        }

                        // fetch full details now
                        fetchStep0Data(strId);
                    });

                    unitListContainer.addView(unitView);
                }

            } catch (JSONException e) {
                Toast.makeText(this, "Error parsing store list", Toast.LENGTH_SHORT).show();
                Log.e("PARSE_ERROR", e.getMessage());
            } finally {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }, error -> {
            Toast.makeText(this, "Error fetching store list", Toast.LENGTH_LONG).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });

        mRequestQueue.add(request);
    }

    private void fetchStep0Data(String strId) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        System.out.println("Selected StoreID : " + strId);
        String url = retrofit.baseUrl() + "getStep0DataJsonById?str_id=" + strId;

        StringRequest detailRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);

                if (!obj.has("store_info")) {
                    Toast.makeText(this, "Failed to fetch store details", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject storeInfo = obj.getJSONObject("store_info");
                JSONObject gpsInfo = obj.getJSONObject("gps_distance_info");

                JSONObject keyContacts = new JSONObject(); // 🛡️ Safe default
                if (obj.has("key_contacts")) {
                    keyContacts = obj.getJSONObject("key_contacts");
                }
                JSONArray staffList = obj.has("staff_list") ? obj.getJSONArray("staff_list") : new JSONArray();

                // Pass to next step
                Intent intent = new Intent(this, stepTwo.class);
                intent.putExtra("store_info", storeInfo.toString());
                intent.putExtra("gps_info", gpsInfo.toString());
                intent.putExtra("key_contacts", keyContacts.toString());
                intent.putExtra("staff_list", staffList.toString());
                intent.putExtra("tmcode", tmcode);
                intent.putExtra("mobile", phoneno);
                intent.putExtra("name", user_name);
                intent.putExtra("bio", bio);
                startActivity(intent);
                finish();

            } catch (JSONException e) {
                Toast.makeText(this, "Failed to parse store detail", Toast.LENGTH_SHORT).show();
                Log.e("PARSE_ERROR", e.toString());
            } finally {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }, error -> {
            Toast.makeText(this, "Error fetching store detail", Toast.LENGTH_LONG).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });

        mRequestQueue.add(detailRequest);
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

        new SweetAlertDialog(stepOne.this, SweetAlertDialog.WARNING_TYPE)
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

                        Intent it = new Intent(stepOne.this, MainActivity.class);
                        it.putExtra("mobile", phoneno);
                        it.putExtra("name", user_name);
                        it.putExtra("bio", bio);
                        startActivity(it);
                    }
                })
                .show();

    }

}
