package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hasbro.basicslife_lfo.databinding.LfoEmployeeDetailsBinding;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class lfo_Employee_Details extends AppCompatActivity {
    private LfoEmployeeDetailsBinding binding;
    private String employeeDetailsJson;
    private String employeeCode;
    private String monthName;
    private int yearnumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LfoEmployeeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize data
        initializeIntentData();

        // Parse employee details
        parseEmployeeDetails();
        // Initialize the API Service
        binding.Payslip.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException, IOException {
               // showPayslipPopup();
            }
        });

    }
    private void showPayslipPopup() {
        // Get the last three months
        List<String> months = getLastThreeMonths();

        // Convert months to an array for AlertDialog
        String[] monthArray = months.toArray(new String[0]);

        // Build and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Month for Payslip");
        builder.setItems(monthArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Download the PDF for the selected month
                String selectedMonth = months.get(which);
                String[] parts = selectedMonth.split(" ");
                 monthName = parts[0]; // e.g., "December"
                 yearnumber = Integer.parseInt(parts[1]); // e.g., 2024

                // Convert month name to month number
                int monthNumber = getMonthNumber(monthName);
                System.out.println("month "+selectedMonth);

                fetchSalarySlip(employeeCode,monthNumber,yearnumber);
            }
        });

        builder.create().show();
    }

    private void fetchSalarySlip(String empcode,int month,int year) {

        String url = retrofit.baseUrl() + "getSalarySlipDetail?empcode=" + empcode + "&month=" + month+ "&year=" + year;

        // Create a Volley request
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("response" +response);
                            if (response.length() > 0){
                                JSONArray getSalaryDetail = response.getJSONArray(empcode);
                                System.out.println("getSalaryDetail" +getSalaryDetail);
                                if (getSalaryDetail.length() > 0) {
                                    // Extract fields using indices
                                    String associateName = (String) getSalaryDetail.get(0);
                                    String designation = (String) getSalaryDetail.get(1);
                                    String esiNo = getSalaryDetail.get(2) == null || getSalaryDetail.get(2).equals("") ? "N/A" : (String) getSalaryDetail.get(2);
                                    String uanNo = getSalaryDetail.get(3) == null || getSalaryDetail.get(3).equals("") ? "N/A" : (String) getSalaryDetail.get(3);
                                    String fixedGrossPay = (String) getSalaryDetail.get(4);
                                    String grossPay = (String) getSalaryDetail.get(5);
                                    String salaryDays = (String) getSalaryDetail.get(6);
                                    String totalEarned = (String) getSalaryDetail.get(7);
                                    String otherEarnings = (String) getSalaryDetail.get(8);
                                    String grossSalary = (String) getSalaryDetail.get(9);
                                    String esi = (String) getSalaryDetail.get(10);
                                    String providentFund = (String) getSalaryDetail.get(11);
                                    String otherDeduction = (String) getSalaryDetail.get(12);
                                    String garmentsPurchased = (String) getSalaryDetail.get(13);
                                    String professionalTax = (String) getSalaryDetail.get(14);
                                    String totalDeductions = (String) getSalaryDetail.get(15);
                                    String netPay = (String) getSalaryDetail.get(16);
                                    String basicPay = (String) getSalaryDetail.get(17);
                                    String extraDays = (String) getSalaryDetail.get(18);
                                    String totalExtra = (String) getSalaryDetail.get(19);


                                    // Call method to generate PDF
                                    generateSalarySlipPDF(empcode, associateName, designation, esiNo, uanNo, fixedGrossPay, grossPay,
                                            salaryDays, totalEarned, otherEarnings, grossSalary, esi, providentFund, otherDeduction,
                                            garmentsPurchased, professionalTax, totalDeductions, netPay, basicPay,extraDays,totalExtra);
                                }
                            }else{
                                norecordfound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to fetch salary slip", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add request to the queue
        requestQueue.add(jsonObjectRequest);
    }


    private void generateSalarySlipPDF(String empCode, String associateName, String designation, String esiNo, String uanNo,
                                       String fixedGrossPay, String grossPay, String salaryDays, String totalEarned,
                                       String otherEarnings, String grossSalary, String esi, String providentFund,
                                       String otherDeduction, String garmentsPurchased, String professionalTax,
                                       String totalDeductions, String netPay, String basicPay, String extraDays, String totalExtra) {
        String dest = getApplicationContext().getFilesDir().getPath() + "/Salary_Slip_" + empCode + ".pdf";


        try {
            // Use MediaStore API to save the PDF in the Downloads folder
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Salary_Slip_" + empCode + ".pdf");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

            if (uri != null) {
                // Open an output stream to the file
                OutputStream outputStream = getContentResolver().openOutputStream(uri);

                if (outputStream != null) {
                    // Initialize PdfWriter with the output stream
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf);

                    // Add Company Header
                    document.add(new Paragraph("HASBRO CLOTHING PVT LTD")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(16));
                    document.add(new Paragraph("#23 B, ALAPAKKAM MAIN ROAD, MADURAVOYAL\nCHENNAI, TAMILNADU\nINDIA - 600095")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(12));

                    // Add Title
                    document.add(new Paragraph("\nPayslip for the Month "+monthName+"-"+yearnumber)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(14));
                    Table table1 = new Table(2); // Two columns
                    table1.setWidth(UnitValue.createPercentValue(100)); // Set table to full width


                    table1.addCell(new Cell().add(new Paragraph("Code : " + empCode).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("Name : " + associateName).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("Department : SALES").setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("Extra Days : " + extraDays).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("Designation : " + designation).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("Loss of Pay Days : 0").setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("Fixed Gross Pay : " + fixedGrossPay).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("Salary Days : " + salaryDays).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("UAN Number : " + uanNo).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

                    table1.addCell(new Cell().add(new Paragraph("ESI Number : " + esiNo).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

// Add the table to the document
                    document.add(table1);


                    // Add Earnings and Deductions Table
                    Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
                    table.addHeaderCell(new Cell().add(new Paragraph("Earnings").setBackgroundColor(ColorConstants.LIGHT_GRAY)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Amount (Rs.)").setBackgroundColor(ColorConstants.LIGHT_GRAY)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Deductions").setBackgroundColor(ColorConstants.LIGHT_GRAY)));
                    table.addHeaderCell(new Cell().add(new Paragraph("Amount (Rs.)").setBackgroundColor(ColorConstants.LIGHT_GRAY)));

                    // Add Rows
                    table.addCell("Basic").setBorder(Border.NO_BORDER);
                    table.addCell(basicPay).setBorder(Border.NO_BORDER);
                    table.addCell("Provident Fund").setBorder(Border.NO_BORDER);
                    table.addCell(providentFund).setBorder(Border.NO_BORDER);

                    table.addCell("HRA").setBorder(Border.NO_BORDER);
                    table.addCell("0.00").setBorder(Border.NO_BORDER);
                    table.addCell("Professional Tax").setBorder(Border.NO_BORDER);
                    table.addCell(professionalTax).setBorder(Border.NO_BORDER);

                    table.addCell("Conveyance").setBorder(Border.NO_BORDER);
                    table.addCell("0.00").setBorder(Border.NO_BORDER);
                    table.addCell("Garments Purchased").setBorder(Border.NO_BORDER);
                    table.addCell(garmentsPurchased).setBorder(Border.NO_BORDER);

                    table.addCell("Other Earnings").setBorder(Border.NO_BORDER);
                    table.addCell(otherEarnings).setBorder(Border.NO_BORDER);
                    table.addCell("ESI").setBorder(Border.NO_BORDER);
                    table.addCell(esi).setBorder(Border.NO_BORDER);

                    table.addCell("Gross Total");
                    table.addCell(grossSalary);
                    table.addCell("Deduction Total");
                    table.addCell(totalDeductions);


                    document.add(table);

// Add Net Pay and Additional Details
                    document.add(new Paragraph("\nNet Pay : "+netPay)
                            .setTextAlignment(TextAlignment.LEFT));

                    long longValue = (long) Double.parseDouble(netPay);
                    String words = convertNumberToWords(longValue);

                    document.add(new Paragraph(words).setTextAlignment(TextAlignment.LEFT));

                    Table table2 = new Table(2); // Two columns
                    table2.setWidth(UnitValue.createPercentValue(100)); // Set table to full width


                    table2.addCell(new Cell().add(new Paragraph("\nSick Leave : 0.00").setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

                    table2.addCell(new Cell().add(new Paragraph("Earned Leave : 0.00").setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

                    document.add(table2);
                    // Add Footer
                    document.add(new Paragraph("\n\"This is a computer-generated payslip. Hence, signature is not required.\"")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(10));


                    document.close();
                    outputStream.close();

                    Toast.makeText(this, "PDF saved in Downloads folder", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to create file in Downloads folder", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static String convertNumberToWords(long number) {
        if (number == 0) {
            return "ZERO";
        }

        String[] units = {
                "", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE",
                "TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN",
                "SEVENTEEN", "EIGHTEEN", "NINETEEN"
        };
        String[] tens = {
                "", "", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY"
        };

        StringBuilder words = new StringBuilder();

        if (number >= 1_000_000) {
            words.append(convertNumberToWords(number / 1_000_000)).append(" MILLION ");
            number %= 1_000_000;
        }
        if (number >= 1_00_000) {
            words.append(convertNumberToWords(number / 1_00_000)).append(" LAKH ");
            number %= 1_00_000;
        }
        if (number >= 1_000) {
            words.append(convertNumberToWords(number / 1_000)).append(" THOUSAND ");
            number %= 1_000;
        }
        if (number >= 100) {
            words.append(convertNumberToWords(number / 100)).append(" HUNDRED ");
            number %= 100;
        }
        if (number >= 20) {
            words.append(tens[(int) (number / 10)]).append(" ");
            number %= 10;
        }
        if (number > 0) {
            words.append(units[(int) number]).append(" ");
        }

        return words.toString().trim();
    }
    private void savePDFFile(byte[] pdfData, String fileName) throws IOException {
        // Path to save the PDF file
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadsDir, fileName);

        // Write the byte array to the file
        FileOutputStream fos = new FileOutputStream(pdfFile);
        fos.write(pdfData);
        fos.close();

        Toast.makeText(this, "File saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }


    private List<String> getLastThreeMonths() {
        List<String> months = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Start with the previous month
        calendar.add(Calendar.MONTH, -1);

        for (int i = 0; i < 3; i++) {
            // Format the month name as needed
            String monthName = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime());
            months.add(monthName);

            // Move to the previous month
            calendar.add(Calendar.MONTH, -1);
        }

        return months;
    }

    private void parseEmployeeDetails() {
        try {
            JSONArray employeeDetails = new JSONArray(employeeDetailsJson);
            binding.employeeName.setText(getOrDefault(employeeDetails.getString(0)));
            binding.employeeMobile.setText(getOrDefault(employeeDetails.getString(3)));
            binding.employeeWhatsApp.setText(getOrDefault(employeeDetails.getString(4)));
            binding.employeeEmail.setText(getOrDefault(employeeDetails.getString(5)));
            binding.employeeDOB.setText(getOrDefault(employeeDetails.getString(6)));
            binding.employeeDOJ.setText(getOrDefault(employeeDetails.getString(7)));
            binding.employeeGender.setText(getOrDefault(employeeDetails.getString(8)));
            binding.employeeQualification.setText(getOrDefault(employeeDetails.getString(28)));
            binding.employeeAadhaar.setText(getOrDefault(employeeDetails.getString(9)));
            binding.employeeUAN.setText(getOrDefault(employeeDetails.getString(10)));
            binding.employeeESI.setText(getOrDefault(employeeDetails.getString(11)));
            binding.employeeStreet.setText(getOrDefault(employeeDetails.getString(12)));
            binding.employeeLocality.setText(getOrDefault(employeeDetails.getString(13)));
            binding.employeeCity.setText(getOrDefault(employeeDetails.getString(14)));
            binding.employeeState.setText(getOrDefault(employeeDetails.getString(15)));
            binding.employeePincode.setText(getOrDefault(employeeDetails.getString(16)));
            binding.employeeDesignation.setText(getOrDefault(employeeDetails.getString(17)));
            binding.employeeGrossPay.setText(getOrDefault(employeeDetails.getString(18)));
            binding.employeeNetPay.setText(getOrDefault(employeeDetails.getString(19)));
            binding.employeeCTC.setText(getOrDefault(employeeDetails.getString(20)));
            binding.employeeAccNo.setText(getOrDefault(employeeDetails.getString(24)));
            binding.employeeIFSC.setText(getOrDefault(employeeDetails.getString(25)));
            binding.employeeBname.setText(getOrDefault(employeeDetails.getString(26)));
            employeeCode=employeeDetails.getString(2);
            getempimage(employeeCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getempimage(String empcode) {
// API URL
        String url = retrofit.baseUrl() +"getEmployeeImage?empcode=" + empcode;

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JSON request
        // Make the JSON Object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the JSON response
                            String base64Image = response.getString("image_data");

                            // Decode the Base64 image string
                            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);


                            // Load image using Glide
                            Glide.with(lfo_Employee_Details.this)
                                    .asBitmap()
                                    .load(decodedBytes)
                                    .circleCrop()
                                    .into(binding.employeePhoto);

//                            // Convert the byte array to a Bitmap
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//                            // Create a circular bitmap
//                            Bitmap circularBitmap = getCircularBitmap(bitmap);
//                            // Set the Bitmap to the ImageView
//                            binding.employeePhoto.setImageBitmap(circularBitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(lfo_Employee_Details.this, "Failed to parse image data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            // Extract the error message from the VolleyError
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorJson = new JSONObject(responseBody);
                            String errorMessage = errorJson.getString("error");

                            // Display the error message
                            Toast.makeText(lfo_Employee_Details.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(lfo_Employee_Details.this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Draw a circle
        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        // Use the BitmapShader to display the image inside the circle
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawCircle(radius, radius, radius, paint);

        return output;
    }
    private int getMonthNumber(String monthName) {
        // Use a mapping of month names to numbers
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("January", 1);
        monthMap.put("February", 2);
        monthMap.put("March", 3);
        monthMap.put("April", 4);
        monthMap.put("May", 5);
        monthMap.put("June", 6);
        monthMap.put("July", 7);
        monthMap.put("August", 8);
        monthMap.put("September", 9);
        monthMap.put("October", 10);
        monthMap.put("November", 11);
        monthMap.put("December", 12);

        // Get the month number from the map
        return monthMap.getOrDefault(monthName, -1); // Returns -1 if not found
    }


    private String getOrDefault(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }
    private void initializeIntentData() {
         employeeDetailsJson = getIntent().getStringExtra("employeeDetails");
    }

        private void norecordfound() {
        new SweetAlertDialog(lfo_Employee_Details.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("No Slip Available For This Month")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();

    }
}