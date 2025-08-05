package com.hasbro.basicslife_lfo.intro;



import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hasbro.basicslife_lfo.customProgressBar;
import com.hasbro.basicslife_lfo.databinding.StepfiveBinding;
import com.hasbro.basicslife_lfo.geturl;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class stepFive extends AppCompatActivity {
    private StepfiveBinding binding;
    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;
    private String staffListJson;


    private Bitmap morningPhoto, eveningPhoto;
    private RequestQueue mRequestQueue;
    Retrofit retrofit = geturl.getClient();
    private List<String> hygieneResponses = new ArrayList<>();

    customProgressBar progressDialog = new customProgressBar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = StepfiveBinding.inflate(getLayoutInflater());
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
        Intent intent = new Intent(stepFive.this, stepSix.class);
        intent.putExtra("strId", strid);
        intent.putExtra("strname", storname);
        intent.putExtra("strcode", strcode);
        intent.putExtra("grname", grname);
        intent.putExtra("carea", carea);
        intent.putExtra("tmcode", tmcode);
        intent.putExtra("mobile", phoneno);
        intent.putExtra("name", user_name);
        intent.putExtra("bio", bio);

        intent.putExtra("sm_name", smName);
        intent.putExtra("sm_contact", smContact);
        intent.putExtra("sm_email", smEmail);

        intent.putExtra("dm_name", dmName);
        intent.putExtra("dm_contact", dmContact);
        intent.putExtra("dm_email", dmEmail);

        intent.putExtra("wh_name", whName);
        intent.putExtra("wh_contact", whContact);
        intent.putExtra("wh_email", whEmail);

        intent.putExtra("staff_list", staffListJson);
        intent.putStringArrayListExtra("hygiene_csv", new ArrayList<>(hygieneResponses));
        intent.putExtra("entryPhotoCaptured", morningPhoto != null);

        startActivity(intent);

    }

    private void initializeIntentData() {
        Intent intent = getIntent();

        strid = intent.getStringExtra("strId");
        storname = intent.getStringExtra("strname");
        strcode = intent.getStringExtra("strcode");
        grname = intent.getStringExtra("grname");
        carea = intent.getStringExtra("carea");
        tmcode = intent.getStringExtra("tmcode");
        phoneno = intent.getStringExtra("mobile");
        user_name = intent.getStringExtra("name");
        bio = intent.getStringExtra("bio");

        smName = intent.getStringExtra("sm_name");
        smContact = intent.getStringExtra("sm_contact");
        smEmail = intent.getStringExtra("sm_email");

        dmName = intent.getStringExtra("dm_name");
        dmContact = intent.getStringExtra("dm_contact");
        dmEmail = intent.getStringExtra("dm_email");

        whName = intent.getStringExtra("wh_name");
        whContact = intent.getStringExtra("wh_contact");
        whEmail = intent.getStringExtra("wh_email");

        staffListJson = intent.getStringExtra("staff_list");
        hygieneResponses = intent.getStringArrayListExtra("hygiene_csv");

        // Optional debug log
        // Log.d("IntentData", "Received: " + strid + ", " + strcode + ", etc.");
    }

}