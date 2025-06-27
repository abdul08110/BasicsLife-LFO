package com.hasbro.basicslife_lfo.intro;



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
import com.hasbro.basicslife_lfo.R;
import com.hasbro.basicslife_lfo.compSaleRecordSaved;
import com.hasbro.basicslife_lfo.customProgressBar;
import com.hasbro.basicslife_lfo.databinding.LfoCounterPhotoBinding;
import com.hasbro.basicslife_lfo.databinding.Step3Binding;
import com.hasbro.basicslife_lfo.geturl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class step3 extends AppCompatActivity {
    private Step3Binding binding;
    private String strcode,grname,storname,strid,tmcode,phoneno,user_name,bio,carea;

    private Bitmap morningPhoto, eveningPhoto;
    private RequestQueue mRequestQueue;
    Retrofit retrofit = geturl.getClient();
    private List<String> hygieneResponses = new ArrayList<>();

    customProgressBar progressDialog = new customProgressBar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = Step3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Fetch and Initialize Data
        initializeIntentData();
        mRequestQueue = Volley.newRequestQueue(this);
        //checkAndLoadImages(strid);
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


        // Set click listeners
        binding.btnCaptureMorning.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            morningPhotoLauncher.launch(intent);
        });

        binding.btnNextStep.setOnClickListener(v -> {
            goToStep4();
        });

        binding.btnBackStep.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


    }
    private void goToStep4() {
        if (morningPhoto == null) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Photo Required")
                    .setContentText("Please capture the entry photo before continuing.")
                    .setConfirmText("OK")
                    .show();
            return;
        }
        Intent intent = new Intent(step3.this, step4.class);
        intent.putExtra("strcode", strcode);
        intent.putExtra("grname", grname);
        intent.putExtra("strname", storname);
        intent.putExtra("strid", strid);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile", phoneno);
        intent.putExtra("name", user_name);
        intent.putExtra("bio", bio);
        intent.putExtra("carea", carea);
        intent.putStringArrayListExtra("hygieneResponses", new ArrayList<>(hygieneResponses));
        intent.putExtra("entryPhotoCaptured", morningPhoto != null);

        startActivity(intent);
    }

    private void initializeIntentData() {
        Intent intent = getIntent();
        strcode = intent.getStringExtra("strcode");
        grname = intent.getStringExtra("grname");
        storname = intent.getStringExtra("strname");
        strid = intent.getStringExtra("strid");
        tmcode = intent.getStringExtra("tmcode");
        phoneno = intent.getStringExtra("mobile");
        user_name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");
        carea = intent.getStringExtra("carea");
        hygieneResponses = intent.getStringArrayListExtra("hygiene_csv");
        testing push in git
    }

}