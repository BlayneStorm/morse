package com.example.morse;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ToggleButton toggleButton;
    Camera camera;
    Camera.Parameters parameters;

    static final int CAMERA_REQUEST = 50;
    boolean flashLighStatus = false;

    EditText inputText;
    TextView result;
    Button enableButton;

    public static String[] TEXT = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "!", ",", "?",
            ".", "'" };
    public static String[] MORSE = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..",
            "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
            "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----", "-.-.--", "--..--",
            "..--..", ".-.-.-", ".----." };

    public static HashMap<String, String> TEXT_TO_MORSE = new HashMap<>();
    public static HashMap<String, String> MORSE_TO_TEXT = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.phrase_input);
        result = findViewById(R.id.result);
        enableButton = findViewById(R.id.enable_button);

        boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        enableButton.setEnabled(!isEnabled);
        enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
            }
        });

//        toggleButton = (ToggleButton) findViewById(R.id.onOffFlashlight);
//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                camera = Camera.open();
//                Camera.Parameters parameters = camera.getParameters();
//
//                if (checked) {
//                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                    camera.setParameters(parameters);
//                    camera.startPreview();
//
//                } else {
//                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                    camera.setParameters(parameters);
//                    camera.stopPreview();
//                    camera.release();
//                }
//            }
//        });

        for (int i = 0; i < TEXT.length && i < MORSE.length; i++) {
            TEXT_TO_MORSE.put(TEXT[i], MORSE[i]);
            MORSE_TO_TEXT.put(MORSE[i], TEXT[i]);
        }
    }

    public void turnFlash(View v) {
        final boolean hasCameraFlash = getApplicationContext().
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (hasCameraFlash) {
            if (flashLighStatus)
                flashLightOff();
            else
                flashLightOn();
        } else {
            Toast.makeText(MainActivity.this, "No flash available on this device!", Toast.LENGTH_LONG).show();
        }
    }

    private void flashLightOn() {
        //Get camera service
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //Get front camera Id
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLighStatus = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void flashLightOff() {
        //Get camera service
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        //Get front camera Id
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLighStatus = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Check if user has granted the camera permission
        switch(requestCode) {
            case CAMERA_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableButton.setEnabled(false);
                    enableButton.setText("Camera is enabled!");
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied for camera", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void textToMorse(View v) throws InterruptedException{
        String textString = inputText.getText().toString();

        StringBuilder builder = new StringBuilder();
        String[] words = textString.trim().split(" ");

        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                String morseCharResult = TEXT_TO_MORSE.get(word.substring(i, i + 1).toLowerCase());
                builder.append(morseCharResult).append(" ");

                for (char c: morseCharResult.toCharArray()) {
//                    camera = Camera.open();
//                    parameters = camera.getParameters();
//                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                    camera.setParameters(parameters);
//                    camera.startPreview();
                    flashLightOn();

                    if(c == '.') {
                        Thread.sleep((long)(0.3 * 1 * 1000));
                    }
                    if(c == '-') {
                        Thread.sleep((long)(0.3 * 3 * 1000));
                    }

//                    camera = Camera.open();
//                    parameters = camera.getParameters();
//                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                    camera.setParameters(parameters);
//                    camera.stopPreview();
//                    camera.release();
                    flashLightOff();

                    Thread.sleep((long)(0.3 * 1 * 1000));
                }
            }

            builder.append("  ");
        }

        result.setText(builder.toString());
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
//            case R.id.convert_menu:
//                Intent intent3 = new Intent(this, ConvertActivity.class);
//                startActivity(intent3);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}