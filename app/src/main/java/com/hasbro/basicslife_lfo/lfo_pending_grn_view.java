package com.hasbro.basicslife_lfo;

import android.app.DatePickerDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.hasbro.basicslife_lfo.databinding.LfoPendingGrnViewBinding;



public class lfo_pending_grn_view extends AppCompatActivity {
    private LfoPendingGrnViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LfoPendingGrnViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeIntentData();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }
    private void initializeIntentData() {
        // Fetch Intent Data
        String invNo = getIntent().getStringExtra("invNo");
        String poNo = getIntent().getStringExtra("poNo");
        String lrNo = getIntent().getStringExtra("lrNo");
        String qty = getIntent().getStringExtra("qty");
        String transport = getIntent().getStringExtra("transport");


        // Pass fetched data to helper methods
        binding.invno.setText(invNo);
        binding.pono.setText(poNo);
        binding.lrno.setText(lrNo);
        binding.qty.setText(qty);
        binding.transport.setText(transport);

        binding.grndate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(lfo_pending_grn_view.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Set the selected date to the TextInputEditText
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        binding.grndate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();


        });
    }
}