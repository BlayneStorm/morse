package com.example.morse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class ConvertActivity extends AppCompatActivity {

    public static String[] TEXT = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "!", ",", "?",
            ".", "'" };
    public static String[] MORSE = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..",
            "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
            "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----", "-.-.--", "--..--",
            "..--..", ".-.-.-", ".----." };

    public static HashMap<String, String> TEXT_TO_MORSE = new HashMap<>();
    public static HashMap<String, String> MORSE_TO_TEXT = new HashMap<>();

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        for (int i = 0; i < TEXT.length && i < MORSE.length; i++) {
            TEXT_TO_MORSE.put(TEXT[i], MORSE[i]);
            MORSE_TO_TEXT.put(MORSE[i], TEXT[i]);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //EditText texty = findViewById(R.id.phrase_input_encode);

        //Intent intent = getIntent();
        //String str = intent.getStringExtra("messageToDecode");
        //texty.setText(str, TextView.BufferType.EDITABLE);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EncodeFragment(), "Encode Feature");
        adapter.addFragment(new DecodeFragment(), "Decode Feature");

        viewPager.setAdapter(adapter);

        Intent intent = getIntent();
        String str = intent.getStringExtra("messageToDecode");
        //Toast.makeText(ConvertActivity.this, "x"+str+"x", Toast.LENGTH_LONG).show();

        if(str != null) {
            viewPager.setCurrentItem(1); //daca ajung pe activitate din InboxActivity (primesc si morse pt decode)
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    public void flashLightOn() {
        //Get camera service
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //Get front camera Id
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void flashLightOff() {
        //Get camera service
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //Get front camera Id
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);

        // return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.send_sms_menu:
                Intent intent1 = new Intent(this, SmsActivity.class);
                startActivity(intent1);
                return true;
            case R.id.inbox_sms_menu:
                Intent intent2 = new Intent(this, InboxActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
