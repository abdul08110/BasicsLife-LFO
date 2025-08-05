package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.customProgressBar;
import com.hasbro.basicslife_lfo.databinding.StepthirteenBinding;
import com.hasbro.basicslife_lfo.geturl;
import com.hasbro.basicslife_lfo.pojo.GRNModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

public class stepThirteen extends AppCompatActivity {

    private StepthirteenBinding binding;

    private String strcode, grname, storname, strid, tmcode, phoneno, user_name, bio, carea;
    private String smName, smContact, smEmail;
    private String dmName, dmContact, dmEmail;
    private String whName, whContact, whEmail;
    private String staffListJson;
    private boolean entryPhotoCaptured;
    private ArrayList<String> hygieneResponses;
    private HashMap<String, String> onFloorData;

    private ArrayList<?> grnList; // Optional: cast as ArrayList<GRNModel> if needed

    private Bitmap exitPhoto;
    Retrofit retrofit = geturl.getClient();
    customProgressBar progressDialog = new customProgressBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StepthirteenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeIntentData();

        // Launcher to capture exit photo
        ActivityResultLauncher<Intent> exitPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                        exitPhoto = (Bitmap) result.getData().getExtras().get("data");
                        binding.imgEveningPhoto.setImageBitmap(exitPhoto);
                    } else {
                        Toast.makeText(this, "Failed to capture photo. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Capture Exit Photo
        binding.btnCaptureMorning.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            exitPhotoLauncher.launch(intent);
        });

        // BACK button
        binding.btnBackStep.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // NEXT button
        binding.btnNextStep.setOnClickListener(v -> {
            if (exitPhoto == null) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Photo Required")
                        .setContentText("Please capture the EXIT photo before continuing.")
                        .setConfirmText("OK")
                        .show();
                return;
            }

            Intent intent = new Intent(stepThirteen.this, stepFourteen.class);

            // Pass everything forward
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
            intent.putExtra("entryPhotoCaptured", entryPhotoCaptured);
            intent.putExtra("hygiene_csv", hygieneResponses);
            intent.putExtra("onFloorData", onFloorData);

            // Optional: pass exitPhoto if you're serializing it, or just store it globally
            if (exitPhoto != null) {
                String filePath = saveBitmapToFile(exitPhoto, "exit_photo.jpg");
                if (filePath != null) {
                    intent.putExtra("exitPhotoPath", filePath);
                }
            }

            // Optional: Pass GRN List again if needed
             intent.putExtra("grnList", grnList);

            startActivity(intent);
        });
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
        entryPhotoCaptured = intent.getBooleanExtra("entryPhotoCaptured", false);
        hygieneResponses = intent.getStringArrayListExtra("hygiene_csv");
        onFloorData = (HashMap<String, String>) intent.getSerializableExtra("onFloorData");

        // Optional: receive GRN list if passed
        grnList = (ArrayList<GRNModel>) getIntent().getSerializableExtra("grnList");




    }

    private String saveBitmapToFile(Bitmap bitmap, String filename) {
        File file = new File(getCacheDir(), filename); // Cache directory (internal)

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); // Use JPEG for smaller size
            out.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
