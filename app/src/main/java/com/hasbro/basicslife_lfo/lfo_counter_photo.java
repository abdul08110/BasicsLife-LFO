package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hasbro.basicslife_lfo.databinding.LfoCounterPhotoBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class lfo_counter_photo extends AppCompatActivity {

    private LfoCounterPhotoBinding binding;
    private String strcode,grname,storname,strid,tmcode,phoneno,user_name,bio,carea;

    private Bitmap morningPhoto, eveningPhoto;
    private RequestQueue mRequestQueue;

    customProgressBar progressDialog = new customProgressBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = LfoCounterPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Fetch and Initialize Data
        initializeIntentData();
        setHeaderText();
        mRequestQueue = Volley.newRequestQueue(this);
        checkAndLoadImages(strid);
        // Set up activity result launchers
        ActivityResultLauncher<Intent> morningPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                        morningPhoto = (Bitmap) result.getData().getExtras().get("data");
                        binding.imgMorningPhoto.setImageBitmap(morningPhoto);
                    } else {
                        Toast.makeText(this, "Failed to capture photo. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ActivityResultLauncher<Intent> eveningPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                        eveningPhoto = (Bitmap) result.getData().getExtras().get("data");
                        binding.imgEveningPhoto.setImageBitmap(eveningPhoto);
                    } else {
                        Toast.makeText(this, "Failed to capture photo. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Set click listeners
        binding.btnCaptureMorning.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            morningPhotoLauncher.launch(intent);
        });

        binding.btnCaptureEvening.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            eveningPhotoLauncher.launch(intent);
        });

        binding.btnUploadMorning.setOnClickListener(v -> {

            if (morningPhoto != null ) {
                if (morningPhoto.getWidth() < 1 || morningPhoto.getHeight() < 1 ) {
                    Toast.makeText(this, "Invalid photo. Please retake.", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show(getSupportFragmentManager(), "tag");
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    uploadMorningPhotos();
                }
            } else {
                Toast.makeText(this, "Please capture both photos", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnUploadEvening.setOnClickListener(v -> {

            if (eveningPhoto != null) {
                if (eveningPhoto.getWidth() < 1 || eveningPhoto.getHeight() < 1) {
                    Toast.makeText(this, "Invalid photo. Please retake.", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show(getSupportFragmentManager(), "tag");
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    uploadEveningPhotos();
                }
            } else {
                Toast.makeText(this, "Please capture both photos", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadImage(String strid, String type, ImageView imageView) {
        String url =  retrofit.baseUrl()+"/getCounterPhoto?strid=" + strid + "&type=" + type;

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.empty)  // Placeholder while loading
                .error(R.drawable.empty)  // Show default image if file is missing
                .into(imageView);
    }

    private void checkAndLoadImages(String strid) {
        loadImage(strid, "entry", binding.imgMorningPhoto);  // Load entry photo
        loadImage(strid, "exit", binding.imgEveningPhoto);   // Load exit photo
    }

    private void uploadMorningPhotos() {
        String url = retrofit.baseUrl() + "upCounterMorningPhoto";
        mRequestQueue = Volley.newRequestQueue(this);

        try {
            // Create JSONObject for the request body
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("strId", strid); // strid from Intent or local variable
            jsonObject.put("tmid", tmcode); // tmcode from Intent or local variable
            jsonObject.put("entryPhoto", encodeImage(morningPhoto)); // Base64 encoded morning photo

            Log.d("UploadPhotos", "Base64 Entry Photo: " + jsonObject.getString("entryPhoto"));

            // Create JsonObjectRequest
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject, // Request body
                    response -> {
                        try {
                            Log.d("UploadPhotos", "Response: " + response.toString());
                            if (response.has("message") && response.getString("message").equals("Data saved successfully")) {
                                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

                                // Navigate to the next activity
                                Intent intent = new Intent(this, compSaleRecordSaved.class);
                                intent.putExtra("name", user_name);
                                intent.putExtra("mobile", phoneno);
                                intent.putExtra("bio", bio);
                                intent.putExtra("tmcode", tmcode);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(this, "Unexpected response from server.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        try {
                            if (error.networkResponse != null) {
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                int statusCode = error.networkResponse.statusCode;
                                String errorMessage = new String(error.networkResponse.data);
                                System.out.println("errorMessage "+errorMessage);
                                Toast.makeText(this, "Error: " + statusCode + " - " + errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(this, "Error parsing error response", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json"); // Explicitly set Content-Type
                    return headers;
                }
            };

            // Add the request to the queue
            mRequestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(this, "Error Preparing Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadEveningPhotos() {
        String url = retrofit.baseUrl() + "upCounterEveningPhoto";
        mRequestQueue = Volley.newRequestQueue(this);

        try {
            // Create JSONObject for the request body
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("strId", strid); // strid from Intent or local variable
            jsonObject.put("tmid", tmcode); // tmcode from Intent or local variable
            jsonObject.put("exitPhoto", encodeImage(eveningPhoto)); // Base64 encoded evening photo

            Log.d("UploadPhotos", "Base64 Exit Photo: " + jsonObject.getString("exitPhoto"));

            // Create JsonObjectRequest
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject, // Request body
                    response -> {
                        try {
                            Log.d("UploadPhotos", "Response: " + response.toString());
                            if (response.has("message") && response.getString("message").equals("Data saved successfully")) {
                                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

                                // Navigate to the next activity
                                Intent intent = new Intent(this, compSaleRecordSaved.class);
                                intent.putExtra("name", user_name);
                                intent.putExtra("mobile", phoneno);
                                intent.putExtra("bio", bio);
                                intent.putExtra("tmcode", tmcode);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(this, "Unexpected response from server.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        progressDialog.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        try {
                            if (error.networkResponse != null) {
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                int statusCode = error.networkResponse.statusCode;
                                String errorMessage = new String(error.networkResponse.data);
                                System.out.println("errorMessage "+errorMessage);
                                Toast.makeText(this, "Error: " + statusCode + " - " + errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(this, "Error parsing error response", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json"); // Explicitly set Content-Type
                    return headers;
                }
            };

            // Add the request to the queue
            mRequestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(this, "Error Preparing Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap cannot be null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); // Adjust compression quality as needed
        byte[] imageBytes = baos.toByteArray();

        // Log the encoded string length for debugging
        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // NO_WRAP avoids line breaks
        Log.d("EncodeImage", "Base64 Image Length: " + base64Image.length());
        return base64Image;
    }

    private void initializeIntentData() {
        // Fetch Intent Data

        strcode = getIntent().getStringExtra("strcode");
        grname = getIntent().getStringExtra("grname");
        storname = getIntent().getStringExtra("strname");
        strid = getIntent().getStringExtra("strid");
        tmcode = getIntent().getStringExtra("tmcode");
        user_name = getIntent().getStringExtra("name");
        phoneno = getIntent().getStringExtra("mobile");
        bio = getIntent().getStringExtra("bio");
        carea= getIntent().getStringExtra("carea");
        // Pass fetched data to helper methods
        binding.storecode.setText(strcode);
        binding.retailer.setText(grname);
    }
    private void setHeaderText() {
        String todayDate = getFormattedDate(getTodayDate(), "yyyy-MM-dd", "dd-MMM-yyyy");
        SpannableString headerText = new SpannableString(storname + " COUNTER PHOTO UPLOAD " + todayDate);

        headerText.setSpan(new ForegroundColorSpan(Color.RED), 0, storname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.headerTitle.setText(headerText);
    }
    private String getFormattedDate(String dateStr, String inputFormat, String outputFormat) {
        try {
            SimpleDateFormat input = new SimpleDateFormat(inputFormat);
            SimpleDateFormat output = new SimpleDateFormat(outputFormat);
            return output.format(input.parse(dateStr));
        } catch (ParseException e) {
            return "Invalid Date";
        }
    }
    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }
}