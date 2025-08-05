package com.hasbro.basicslife_lfo.intro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.hasbro.basicslife_lfo.MainActivity;
import com.hasbro.basicslife_lfo.databinding.SteptwoBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class stepTwo extends AppCompatActivity {

    private SteptwoBinding binding;
    private String bio = "", phoneno = "", tmcode = "", user_name = "",strId="",carpetarea="",staffListJson="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SteptwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnProceedStep1.setOnClickListener(v -> {
            if (!validateInputs()) return;

            Intent intent = new Intent(this, stepThree.class);

            // Carry previously stored user/store details if needed (assuming you already have)
            intent.putExtra("tmcode", tmcode);
            intent.putExtra("mobile", phoneno);
            intent.putExtra("name", user_name);
            intent.putExtra("bio", bio);

            intent.putExtra("strname", binding.txtStoreName.getText().toString().trim());
            intent.putExtra("strcode", binding.txtStoreCode.getText().toString().trim());
            intent.putExtra("grname", binding.txtGroupName.getText().toString().trim());
            intent.putExtra("strId", strId);
            intent.putExtra("carea", carpetarea);

            // Add Key Contact fields
            intent.putExtra("sm_name", binding.edtSmName.getText().toString().trim());
            intent.putExtra("sm_contact", binding.edtSmContact.getText().toString().trim());
            intent.putExtra("sm_email", binding.edtSmEmail.getText().toString().trim());

            intent.putExtra("dm_name", binding.edtDmName.getText().toString().trim());
            intent.putExtra("dm_contact", binding.edtDmContact.getText().toString().trim());
            intent.putExtra("dm_email", binding.edtDmEmail.getText().toString().trim());

            intent.putExtra("wh_name", binding.edtWhName.getText().toString().trim());
            intent.putExtra("wh_contact", binding.edtWhContact.getText().toString().trim());
            intent.putExtra("wh_email", binding.edtWhEmail.getText().toString().trim());

            intent.putExtra("staff_list", staffListJson);

            Log.d("Step0", "Going to Step3 with StoreID: " + strId + ", CarpetArea: " + carpetarea);

            // Proceed
            startActivity(intent);
        });

        storeDataLoad();
        setupBackPressedHandler();
        setUserDetails();
    }

    private void storeDataLoad() {
        try {
            String storeInfoStr = getIntent().getStringExtra("store_info");
            String gpsInfoStr = getIntent().getStringExtra("gps_info");
            String keyContactsStr = getIntent().getStringExtra("key_contacts");
            String staffListStr = getIntent().getStringExtra("staff_list");


            if (storeInfoStr == null || gpsInfoStr == null || staffListStr == null) {
                Toast.makeText(this, "Incomplete data received", Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject storeInfo = new JSONObject(storeInfoStr);
            JSONObject gpsInfo = new JSONObject(gpsInfoStr);
            JSONObject keyContacts = keyContactsStr != null ? new JSONObject(keyContactsStr) : new JSONObject(); // handle optional
            JSONArray staffList = new JSONArray(staffListStr);

            carpetarea = gpsInfo.optString("carpet_area", "");
            strId = storeInfo.optString("str_id", "");
            // Store Info
            binding.txtStoreName.setText(storeInfo.optString("store_name", ""));
            binding.txtGroupName.setText(storeInfo.optString("group_name", ""));
            binding.txtStoreCode.setText(storeInfo.optString("store_code", ""));

            // Key Contacts
            binding.edtSmName.setText(keyContacts.optString("sm_name", ""));
            binding.edtSmContact.setText(keyContacts.optString("sm_contact", ""));
            binding.edtSmEmail.setText(keyContacts.optString("sm_email", ""));

            binding.edtDmName.setText(keyContacts.optString("dm_name", ""));
            binding.edtDmContact.setText(keyContacts.optString("dm_contact", ""));
            binding.edtDmEmail.setText(keyContacts.optString("dm_email", ""));

            binding.edtWhName.setText(keyContacts.optString("inv_wh_name", ""));
            binding.edtWhContact.setText(keyContacts.optString("wh_contact", ""));
            binding.edtWhEmail.setText(keyContacts.optString("inv_wh_email", ""));

            // Staff List
            if (staffList.length() >= 1) {
                JSONObject staff1 = staffList.getJSONObject(0);
                binding.etStaff1Name.setText(staff1.optString("emp_name", ""));
                binding.etStaff1Code.setText(staff1.optString("emp_code", ""));
                binding.etStaff1Contact.setText(staff1.optString("emp_contact", ""));
                binding.etStaff1Pf.setText(staff1.optString("pf_number", ""));
                binding.etStaff1Esi.setText(staff1.optString("esi_number", ""));
                binding.etStaff1Gross.setText(staff1.optString("gross_pay", ""));
            }

            if (staffList.length() >= 2) {
                JSONObject staff2 = staffList.getJSONObject(1);
                binding.etStaff2Name.setText(staff2.optString("emp_name", ""));
                binding.etStaff2Code.setText(staff2.optString("emp_code", ""));
                binding.etStaff2Contact.setText(staff2.optString("emp_contact", ""));
                binding.etStaff2Pf.setText(staff2.optString("pf_number", ""));
                binding.etStaff2Esi.setText(staff2.optString("esi_number", ""));
                binding.etStaff2Gross.setText(staff2.optString("gross_pay", ""));
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing data", Toast.LENGTH_LONG).show();
            Log.e("step0", "JSON error: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Patterns
        String contactRegex = "^[0-9]{10}$";
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String nameRegex = "^[a-zA-Z ]{1,30}$";

        // Validate SM
        if (!binding.edtSmName.getText().toString().trim().matches(nameRegex)) {
            binding.edtSmName.setError("Only letters and spaces allowed (max 30)");
            return false;
        }
        if (!binding.edtSmContact.getText().toString().trim().matches(contactRegex)) {
            binding.edtSmContact.setError("Enter valid 10-digit contact number");
            return false;
        }
        if (!binding.edtSmEmail.getText().toString().trim().isEmpty() &&
                !binding.edtSmEmail.getText().toString().trim().matches(emailRegex)) {
            binding.edtSmEmail.setError("Enter a valid email address");
            return false;
        }

        // Validate DM
        if (!binding.edtDmName.getText().toString().trim().matches(nameRegex)) {
            binding.edtDmName.setError("Only letters and spaces allowed (max 30)");
            return false;
        }
        if (!binding.edtDmContact.getText().toString().trim().matches(contactRegex)) {
            binding.edtDmContact.setError("Enter valid 10-digit contact number");
            return false;
        }
        if (!binding.edtDmEmail.getText().toString().trim().isEmpty() &&
                !binding.edtDmEmail.getText().toString().trim().matches(emailRegex)) {
            binding.edtDmEmail.setError("Enter a valid email address");
            return false;
        }

        // Validate WH
        if (!binding.edtWhName.getText().toString().trim().matches(nameRegex)) {
            binding.edtWhName.setError("Only letters and spaces allowed (max 30)");
            return false;
        }
        if (!binding.edtWhContact.getText().toString().trim().matches(contactRegex)) {
            binding.edtWhContact.setError("Enter valid 10-digit contact number");
            return false;
        }
        if (!binding.edtWhEmail.getText().toString().trim().isEmpty() &&
                !binding.edtWhEmail.getText().toString().trim().matches(emailRegex)) {
            binding.edtWhEmail.setError("Enter a valid email address");
            return false;
        }

        return true;
    }



    private void setUserDetails() {
        user_name = Optional.ofNullable(getIntent().getStringExtra("name")).orElse("");
        phoneno = Optional.ofNullable(getIntent().getStringExtra("mobile")).orElse("");
        bio = Optional.ofNullable(getIntent().getStringExtra("bio")).orElse("");
        tmcode = Optional.ofNullable(getIntent().getStringExtra("tmcode")).orElse("");
        staffListJson = getIntent().getStringExtra("staff_list");

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

        new SweetAlertDialog(stepTwo.this, SweetAlertDialog.WARNING_TYPE)
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

                        Intent it = new Intent(stepTwo.this, MainActivity.class);
                        it.putExtra("mobile", phoneno);
                        it.putExtra("name", user_name);
                        it.putExtra("bio", bio);
                        startActivity(it);
                    }
                })
                .show();


    }
}
