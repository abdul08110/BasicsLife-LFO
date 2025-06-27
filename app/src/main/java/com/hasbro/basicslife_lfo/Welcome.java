package com.hasbro.basicslife_lfo;

import static com.hasbro.basicslife_lfo.freshLoginPage.PREFS_NAME;
import static com.hasbro.basicslife_lfo.geturl.retrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;


public class Welcome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //variable declaration
    static final float END_SCALE = 0.7f;

    private android.os.Handler handler;
    private android.os.Handler gpshandler;

    String str_id="",str_name="",bill_no="",bill_headerid="",qty="",bill_date="";
    String  complaint_no="", created_date="", original_mrp="", stage_id="";
    int numRowsChanged = 0 , numRowscomp = 0, wlletrow = 0,numRowstopsale=0,numRowstrackcmp=0;
    private Runnable r;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    final Context contextt = this;
    TextView  storename,name,strcode;
    String store_name = "", wallet = "",lfo_storecode="",group_name="";
    String Cstr_id="", Cstr_name="", Cbill_no="", Cbill_headerid="", Cqty="", Cbill_date="";
    String itemcode_T="",countt="";

    boolean progressflag = false;
    customProgressBar progressDialog = new customProgressBar();
    ImageView toolbar;

    LinearLayout contentView;

    String mpin="",phoneno="",tmcode="", user_name="", email="", custid="", dob="", city="", status="", type="", strname="";

    JSONArray storeGpsData =new JSONArray();

    static JSONArray storeNotiData =new JSONArray();
    static String messagestring="";
    static String urlstring="";
    LinearLayout Sales, stocks,tasks,Saledata,staffs,upCounter,sellthrough;
    private String selectedStoreName,selectedStoreCode,selectedGroupName,selectedStrId,selectedCarpetarea;
    SliderView sliderView;
    private int flag=0;
    HashMap storelatlngmap=new HashMap<>();
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    HashMap<String, List> map = new HashMap<String, List> ();
    String bio;
    String emp_code="",temp_empcode="",doj="",aadharno = "",street = "";
    Retrofit retrofit   = geturl.getClient();

    Dialog myDialog;

    private static final int REQUEST_LOCATION = 1;

    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Request Location Permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        // Session Timeout Handler
        handler = new android.os.Handler();
        r = this::handleSessionTimeout;
        startHandler();

        // Initialize Views
        initializeViews();

        // Set User Details
        setUserDetails();

        // Setup Navigation Drawer
        setupNavigationDrawer();

        // Setup Button Clicks
        setupButtonClicks();

        // Handle Back Press
        setupBackPressedHandler();

        // Check Pending Tasks
        myDialog = new Dialog(this);
        checkPendingTasks();

        new Handler().postDelayed(this::showNotification, 100000);
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        contentView = findViewById(R.id.content);
        stocks = findViewById(R.id.stocks);
        tasks = findViewById(R.id.tasks);
        name = findViewById(R.id.tmname);
        Sales = findViewById(R.id.Sales);
        sellthrough= findViewById(R.id.sellthrough);
        strcode = findViewById(R.id.strcode);
        Saledata = findViewById(R.id.Saledata);
        staffs = findViewById(R.id.staff);
        upCounter = findViewById(R.id.upCounter);

    }

    private void setUserDetails() {
        user_name = getIntent().getStringExtra("name");
        phoneno = getIntent().getStringExtra("mobile");
        bio = getIntent().getStringExtra("bio");
        tmcode = getIntent().getStringExtra("tmcode");
        name.setText("HI! " + user_name);
    }

    private void setupNavigationDrawer() {
        navigationView.setItemIconTintList(null);
        navigationDrawer();
    }

    private void setupButtonClicks() {
        Saledata.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                if (GlobalVars.isNetworkConnected) {
                    startActivity(new Intent(getApplicationContext(), india_sale_map.class));
                } else {
                    interneterror();
                }
            }
        });

        Sales.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(0);
            }
        });



        stocks.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(1);
            }
        });
        staffs.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(2);
            }
        });

        tasks.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                showTaskPopupWindow();
            }
        });

        upCounter.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(3);
            }
        });

        sellthrough.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) {
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(4);
            }
        });
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!progressflag && drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    logout();
                }
            }
        });
    }

    private void handleSessionTimeout() {
        Toast.makeText(this, "Session TimeOut", Toast.LENGTH_SHORT).show();
        Intent main = new Intent(this, MainActivity.class);
        main.putExtras(getIntent());
        startActivity(main);
    }




//    private void setupImageSlider(HashMap<String, List> map) {
//        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
//        for (int i = 1; i <= map.size(); i++) {
//            ArrayList<Object> tablerow = new ArrayList<>();
//            tablerow.add(map.get("imgurl"));
//            for (Object row : tablerow) {
//                ArrayList finallist = (ArrayList) row;
//                for (Object url : finallist) {
//                    sliderDataArrayList.add(new SliderData(url.toString()));
//                }
//            }
//        }
//        SliderAdapterExample adapter = new SliderAdapterExample(this, sliderDataArrayList);
//        sliderView.setSliderAdapter(adapter);
//        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
//        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//        sliderView.setIndicatorSelectedColor(Color.WHITE);
//        sliderView.setIndicatorUnselectedColor(Color.GRAY);
//        sliderView.setScrollTimeInSec(4);
//        sliderView.startAutoCycle();
//    }

    public void getLocation(int getval){
        gpsTracker = new GpsTracker(Welcome.this);

        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            String URL = ""+retrofit.baseUrl()+"getgpsdata?latitude=" + latitude + "&longitude=" + longitude + "&tmcode=" + tmcode + " ";
            //RequestQueue initialized

            mRequestQueue = Volley.newRequestQueue(this);
            //String Request initialized
            mStringRequest = new StringRequest(Request.Method.GET, URL, response -> {
                try {
                    JSONObject obj = new JSONObject(response);
                    System.out.println("welcome gps data"+obj.length());

                    System.out.println("latitude "+latitude);
                    System.out.println("longitude "+longitude);
                    if(obj.length() > 0){
                        storeGpsData = obj.getJSONArray("storegpsdata");
                        //String chk = String.valueOf(obj.get("storegpsdata"));

                        if (storeGpsData.length()>0) {
                            ArrayList<String> storeNames = new ArrayList<>();
                            for (int i = 0; i < storeGpsData.length(); i++) {
                                JSONArray store = storeGpsData.getJSONArray(i);
                                storeNames.add(store.getString(1)); // Store name at index 1
                            }

                                showStoreSelectionDialog(storeGpsData, storeNames, getval);
                            progressDialog.dismiss();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    nogpsrecordfound();
                                    progressDialog.dismiss();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }
                            });
                        }
                    }else{
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                nogpsrecordfound();
                                progressDialog.dismiss();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            }
                        });
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    throw new RuntimeException(e);
                }


            }, error -> System.out.println("Error :" + error.toString()));

            mRequestQueue.add(mStringRequest);


        }else{
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            gpsTracker.showSettingsAlert();
        }
    }
    private void showStoreSelectionDialog(JSONArray storeGpsData, ArrayList<String> storeNames, int getvalue) {
        // Convert to String array for AlertDialog
        String[] storeNameArray = storeNames.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Store")
                .setItems(storeNameArray, (dialog, which) -> {
                    try {
                        // Get the selected store data

                        JSONArray selectedStore = storeGpsData.getJSONArray(which);
                         selectedStoreName = selectedStore.getString(1); // Store name
                         selectedStoreCode = selectedStore.getString(0); // Store code
                         selectedGroupName= selectedStore.getString(2);
                         selectedStrId= selectedStore.getString(6);
                         selectedCarpetarea= selectedStore.getString(7);
                        // Set the selected store name in the TextView
                        if (getvalue==0){
                            // Set the selected store name in the TextView
                            // storename.setText(selectedStoreName);
                            strcode.setText(selectedStoreCode);
                            Intent intent = new Intent(getApplicationContext(), lfo_sales_view.class);
                            Bundle bundle = new Bundle();
                            intent.putExtra("strname", selectedStoreName);
                            intent.putExtra("strcode", selectedStoreCode);
                            intent.putExtra("grname", selectedGroupName);
                            intent.putExtra("strid", selectedStrId);
                            intent.putExtra("carea", selectedCarpetarea);
                            intent.putExtra("tmcode", tmcode);
                            intent.putExtra("mobile",phoneno);
                            intent.putExtra("name",user_name);
                            intent.putExtra("bio",bio);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            // Optionally, show a Toast
                            Toast.makeText(this, "Selected Store: " + selectedStoreName, Toast.LENGTH_SHORT).show();
                        }
                        else if(getvalue==1) {
                           // storename.setText(selectedStoreName);
                            strcode.setText(selectedStoreCode);
                            Intent intent = new Intent(getApplicationContext(), lfo_stocks_view.class);
                            Bundle bundle = new Bundle();
                            intent.putExtra("strname", selectedStoreName);
                            intent.putExtra("strcode", selectedStoreCode);
                            intent.putExtra("grname", selectedGroupName);
                            intent.putExtra("strid", selectedStrId);
                            intent.putExtra("carea", selectedCarpetarea);
                            intent.putExtra("tmcode", tmcode);
                            intent.putExtra("mobile",phoneno);
                            intent.putExtra("name",user_name);
                            intent.putExtra("bio",bio);

                            intent.putExtras(bundle);
                            startActivity(intent);
                            // Optionally, show a Toast
                            Toast.makeText(this, "Selected Store: " + selectedStoreName, Toast.LENGTH_SHORT).show();
                        }
                        else if(getvalue==2) {
                            // storename.setText(selectedStoreName);
                            strcode.setText(selectedStoreCode);
                            Intent intent = new Intent(getApplicationContext(), lfo_staff_view.class);
                            Bundle bundle = new Bundle();
                            intent.putExtra("strname", selectedStoreName);
                            intent.putExtra("strcode", selectedStoreCode);
                            intent.putExtra("grname", selectedGroupName);
                            intent.putExtra("strid", selectedStrId);
                            intent.putExtra("carea", selectedCarpetarea);
                            intent.putExtra("tmcode", tmcode);
                            intent.putExtra("mobile",phoneno);
                            intent.putExtra("name",user_name);
                            intent.putExtra("bio",bio);

                            intent.putExtras(bundle);
                            startActivity(intent);
                            // Optionally, show a Toast
                            Toast.makeText(this, "Selected Store: " + selectedStoreName, Toast.LENGTH_SHORT).show();
                        }else if(getvalue==3) {
                            // storename.setText(selectedStoreName);
                            strcode.setText(selectedStoreCode);
                            Intent intent = new Intent(getApplicationContext(), lfo_counter_photo.class);
                            Bundle bundle = new Bundle();
                            intent.putExtra("strname", selectedStoreName);
                            intent.putExtra("strcode", selectedStoreCode);
                            intent.putExtra("grname", selectedGroupName);
                            intent.putExtra("strid", selectedStrId);
                            intent.putExtra("carea", selectedCarpetarea);
                            intent.putExtra("tmcode", tmcode);
                            intent.putExtra("mobile",phoneno);
                            intent.putExtra("name",user_name);
                            intent.putExtra("bio",bio);

                            intent.putExtras(bundle);
                            startActivity(intent);
                            // Optionally, show a Toast
                            Toast.makeText(this, "Selected Store: " + selectedStoreName, Toast.LENGTH_SHORT).show();
                        }
                        else if(getvalue==4) {
                            // storename.setText(selectedStoreName);
                            strcode.setText(selectedStoreCode);
                            Intent intent = new Intent(getApplicationContext(), sellthrow_search.class);
                            Bundle bundle = new Bundle();
                            intent.putExtra("strname", selectedStoreName);
                            intent.putExtra("strcode", selectedStoreCode);
                            intent.putExtra("grname", selectedGroupName);
                            intent.putExtra("strid", selectedStrId);
                            intent.putExtra("carea", selectedCarpetarea);
                            intent.putExtra("tmcode", tmcode);
                            intent.putExtra("mobile",phoneno);
                            intent.putExtra("name",user_name);
                            intent.putExtra("bio",bio);

                            intent.putExtras(bundle);
                            startActivity(intent);
                            // Optionally, show a Toast
                            Toast.makeText(this, "Selected Store: " + selectedStoreName, Toast.LENGTH_SHORT).show();
                        }

                        // Optionally, show a Toast
                        Toast.makeText(this, "Selected Store: " + selectedStoreName, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void checkPendingTasks() {
        // Replace with your logic to check pending tasks
        showTaskPopupWindow();

    }

    private void showNotification() {
        // Replace with your logic to check pending tasks
        String apiUrl = ""+retrofit.baseUrl()+"getnotidata";
        mRequestQueue = Volley.newRequestQueue(this);
        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, apiUrl, response -> {
            try {
                JSONObject obj = new JSONObject(response);

                if(obj.length() > 0){
                    storeNotiData = obj.getJSONArray("strnotidata");

                    if (storeNotiData.length()>0) {
                        JSONArray message = storeNotiData.getJSONArray(0);
                        System.out.println("message abdul "+message);
                        messagestring = (String) message.get(0);
                        urlstring = (String) message.get(1);
                        ShownotificationWindow(messagestring,urlstring);

                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }, error -> System.out.println("Error :" + error.toString()));

        mRequestQueue.add(mStringRequest);
    }

    private void ShownotificationWindow(String messagestring, String urlstring) {
        TextView txtclose,notimsg;
        ImageView imageurl;

        myDialog.setContentView(R.layout.notification_popup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        notimsg = (TextView) myDialog.findViewById(R.id.notimsg);
        imageurl =(ImageView) myDialog.findViewById(R.id.notiurl);
        notimsg.setText(messagestring);
        Glide.with(Welcome.this).load(urlstring).fallback(R.drawable.empty).error(R.drawable.empty).into(imageurl);
        txtclose.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                myDialog.dismiss();

            }
        });

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void showTaskPopupWindow() {

        TextView txtclose,skip;
        Button doitnow;

        myDialog.setContentView(R.layout.pending_task_popup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        skip =(TextView) myDialog.findViewById(R.id.skip);
        txtclose.setText("X");
        doitnow = (Button) myDialog.findViewById(R.id.doitnow);
        doitnow.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {


                        try {
                            // This method call MySQL database perform sql query...
                            myDialog.dismiss();
                            //showpendingtaskwindow();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }



            }
        });
        txtclose.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {
                myDialog.dismiss();

            }
        });
        skip.setOnClickListener(new SafeClikcListener() {
            @Override
            public void performClick(View v) throws ParseException, SQLException {

                myDialog.dismiss();

            }
        });
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        String dataFromOtherAct = sp.getString("bio", bio);
        bio=dataFromOtherAct;
        System.out.println("dataFromOtherAct" +dataFromOtherAct);
    }



    public void stopHandler() {
        handler.removeCallbacks(r);
    }

    public void startHandler() {
        handler.postDelayed(r, 120000);
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();
        stopHandler();
        startHandler();
        checkinternet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }


    private void navigationDrawer() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);


        toolbar.setOnClickListener(v -> {

            if (drawerLayout.isDrawerVisible(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.openDrawer(GravityCompat.START);

        });
        animateNavigation();
    }

    private void animateNavigation() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaledOffSet = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffSet;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffSet / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        switch (menuitem.getItemId()) {
            case R.id.home:
                break;
            case R.id.sales:
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(0);
                break;
            case R.id.stocks:
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(1);
                break;
            case R.id.salesdata:
                if (GlobalVars.isNetworkConnected) {
                    new Thread(() -> {
                        try {
                            Intent intent = new Intent(getApplicationContext(), india_sale_map.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();

                } else {
                    interneterror();
                }
                break;
            case R.id.staff:
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(2);
                break;
            case R.id.ptask:
                showTaskPopupWindow();
                break;
            case R.id.upCounter:
                progressDialog.show(getSupportFragmentManager(), "tag");
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getLocation(3);
                break;
            case R.id.profile:
                if (GlobalVars.isNetworkConnected) {
                    new Thread(() -> {
                        try {

                        Intent pro = new Intent(Welcome.this, profile.class);
                        pro.putExtra("name", user_name);
                        pro.putExtra("mobile", phoneno);
                        pro.putExtra("email", email);
                        pro.putExtra("emp_code", emp_code);
                        pro.putExtra("dob", dob);
                        pro.putExtra("temp_empcode", temp_empcode);
                        pro.putExtra("doj", doj);
                        pro.putExtra("aadharno", aadharno);
                        pro.putExtra("street", street);
                        pro.putExtra("strname", strname);
                        startActivity(pro);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();

                } else {
                    interneterror();
                }
                break;
            case R.id.lgout:
                logout();
                break;
            case R.id.setting:
                Intent set = new Intent(Welcome.this, settings.class);
                set.putExtra("phoneno",phoneno);
                set.putExtra("mpin",mpin);
                set.putExtra("name",user_name);
                set.putExtra("bio",bio);
                startActivity(set);
                break;
            case R.id.support:
                Intent support = new Intent(Welcome.this, contactSupport.class);
                startActivity(support);
                break;
            case R.id.share:
                //interneterror();
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Basics Life LFO-Portal");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            case R.id.rateus:
                //interneterror();
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(R.id.home);
        return true;
    }

    private void checkinternet() {

        if (!GlobalVars.isNetworkConnected) {
            interneterror();
        }

    }


    private void logout() {

        new SweetAlertDialog(Welcome.this, SweetAlertDialog.WARNING_TYPE)
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

                        Intent it = new Intent(Welcome.this, MainActivity.class);
                        it.putExtra("mobile", phoneno);
                        it.putExtra("name", user_name);
                        it.putExtra("bio", bio);
                        startActivity(it);
                    }
                })
                .show();


    }
    private void norecordfound() {
        new SweetAlertDialog(Welcome.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("No Record Found")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();

    }
    private void nogpsrecordfound() {
        new SweetAlertDialog(Welcome.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Currently You Are Not In Store")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                }).show();

    }

    private void interneterror() {
        new SweetAlertDialog(Welcome.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Basics Life LFO Portal")
                .setContentText("Turn On Internet")
                .setConfirmText("Settings")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();

    }
}