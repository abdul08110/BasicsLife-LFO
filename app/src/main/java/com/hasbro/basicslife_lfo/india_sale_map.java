package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import android.Manifest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import androidx.activity.OnBackPressedCallback;


public class india_sale_map extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private HashMap<String, String> stateDataMap = new HashMap<>();
    private RequestQueue mRequestQueue;
    private StringRequest  mStringRequest ;



    static JSONArray storeTarAchData =new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_india_sale_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
       // fetchDataFromApi();

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        googleMap.setMyLocationEnabled(true);

        // Define India's boundary for restricting camera movement
        final LatLngBounds INDIA_BOUNDS = new LatLngBounds(
                new LatLng(8.0, 68.0),  // Southwest bound (bottom-left)
                new LatLng(37.0, 97.0)  // Northeast bound (top-right)
        );

        mMap.setLatLngBoundsForCameraTarget(INDIA_BOUNDS);
        // Set minimum and maximum zoom levels
        mMap.setMinZoomPreference(5.0f);  // Prevent zooming out too much
        mMap.setMaxZoomPreference(10.0f); // Adjust to suit your needs

        // Add India's GeoJSON layer (if required)
        loadIndiaGeoJsonLayer(googleMap);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(currentLocation).title("I am here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5));
                mMap.addMarker(markerOptions);

            }
        });

    }

    private void loadIndiaGeoJsonLayer(GoogleMap googleMap) {

        new Thread(() -> {
            try {
                GeoJsonLayer layer = new GeoJsonLayer(googleMap, R.raw.states_india, getApplicationContext());
                layer.getDefaultPolygonStyle().setStrokeWidth(2f);
                runOnUiThread(() -> {
                    try {
                        for (GeoJsonFeature feature : layer.getFeatures()) {

                            GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
                            String stateCode = feature.getProperty("st_nm");
                            fetchDataFromApi(new FetchDataCallback() {
                                @Override
                                public void onDataFetched(HashMap<String, String> dataMap) {
                                    String data = dataMap.get(stateCode); // Get the value for the current stateCode

                                    // Check the data and set the style accordingly
                                    if (data != null) {
                                        if (data.equals("Green")) {
                                            style.setFillColor(0x5500FF00); // Green with transparency
                                            style.setStrokeColor(0xFF00FF00); // Green outline
                                        } else if (data.equals("Blue")) {
                                            style.setFillColor(0x550000FF); // Blue with transparency
                                            style.setStrokeColor(0xFF0000FF); // Blue outline
                                        } else if (data.equals("Red"))  {
                                            style.setFillColor(0x55FF0000); // Red with transparency
                                            style.setStrokeColor(0xFFFF0000); // Red outline
                                        }else {
                                            style.setStrokeColor(0xff444444);
                                        }
                                        feature.setPolygonStyle(style); // Apply the style
                                    }
                                }
                            });

                        }

                        layer.addLayerToMap();
                        layer.setOnFeatureClickListener(feature -> {
                            Toast.makeText(this, "Clicked on: " + feature.getProperty("st_nm"), Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchDataFromApi(FetchDataCallback callback) {
        // Replace with your logic to check pending tasks
        String apiUrl = "" + retrofit.baseUrl() + "getTargetAchData";
        mRequestQueue = Volley.newRequestQueue(this);
        HashMap<String, String> resultMap = new HashMap<>();
        // String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Directly parse JSON and log the result
                try {
                    // Parse the response string into a JSONObject
                    JSONObject jsonResponse = new JSONObject(response);

                    // Iterate over the keys of the JSONObject and put them in the HashMap
                    Iterator<String> iterator = jsonResponse.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = jsonResponse.getString(key);
                        resultMap.put(key, value);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onDataFetched(resultMap);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error: " + error.getMessage());
                    }
                });

        // Add the request to the RequestQueue
        mRequestQueue.add(mStringRequest);

     }
    interface FetchDataCallback {
        void onDataFetched(HashMap<String, String> dataMap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Retry initialization if permission granted
                onMapReady(mMap);
            }
        }
    }
    private int getFillColor(String salesLevel) {
        switch (salesLevel) {
            case "high":
                return 0x5500FF00; // Semi-transparent green
            case "low":
                return 0x55FF0000; // Semi-transparent red
            default:
                return 0x55CCCCCC; // Gray for undefined
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}