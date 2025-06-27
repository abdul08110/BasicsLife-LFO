package com.hasbro.basicslife_lfo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
public class CustomGalleryActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();// list of file paths
    File[] listFile;
    private final String folderName = "MyPhotoDir";
    // Creating object of ViewPager
    ViewPager mViewPager;
    // Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
CheckNetworkConnection network = new CheckNetworkConnection(getApplicationContext());
network.registerDefaultNetworkCallback();
        setContentView(R.layout.activity_gallery);
        getFromSdcard();
        // Initializing the ViewPager Object
        mViewPager = findViewById(R.id.viewPagerMain);
        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(CustomGalleryActivity.this, f);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

    }



    public void getFromSdcard() {
        File file = new File(getExternalFilesDir(folderName), "/");
        if (file.isDirectory()) {
            listFile = file.listFiles();
                if (listFile != null) {
                    for (File value : listFile) {
                        f.add(value.getAbsolutePath());
                    }
                }
        }
    }
}
