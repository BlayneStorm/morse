package com.example.morse;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SmsActivity extends AppCompatActivity {

    final static int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    final static int PICK_CONTACT_REQUEST_CODE = 2;

    Button sendMessageButton;
    EditText inputMessage, inputPhoneNumber;
    TextView sendStatusTextView, deliveryStatusTextView;

    BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        sendMessageButton = findViewById(R.id.send_message);
        inputMessage = findViewById(R.id.message);
        inputPhoneNumber = findViewById(R.id.phone_number);
        sendStatusTextView = findViewById(R.id.message_status);
        deliveryStatusTextView = findViewById(R.id.delivery_status);

        sendMessageButton.setEnabled(false);

        if (checkPermission(Manifest.permission.SEND_SMS)) {
            sendMessageButton.setEnabled(true);

            Intent intent = getIntent();
            String str = intent.getStringExtra("encodedMessage");

            inputMessage.setText(str);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public void sendMessage(View v) {
        String message = inputMessage.getText().toString();
        String phoneNumber = inputPhoneNumber.getText().toString();

        message = "#morse " + message;

        if(!TextUtils.isEmpty(message) && !TextUtils.isEmpty(phoneNumber)) {
            if(checkPermission(Manifest.permission.SEND_SMS)) { //EU ZIC CA E REDUNDANT ASTA (mereu o sa fie granted in functie)
                SmsManager smsManager = SmsManager.getDefault();

                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);

                smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, deliveredIntent);

                //Toast.makeText(SmsActivity.this, "Message sent.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SmsActivity.this, "Permission denied.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SmsActivity.this, "Fields are empty. Can't send SMS.", Toast.LENGTH_LONG).show();
        }
    }

    public void pickContact(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE);
    }

    public void onResume() {
        super.onResume();
        sentStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }
                sendStatusTextView.setText(s);

            }
        };
        deliveredStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Message Not Delivered";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                deliveryStatusTextView.setText(s);
                inputPhoneNumber.setText("");
                inputMessage.setText("");
            }
        };
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }


    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendMessageButton.setEnabled(true);
                } else {
                    Toast.makeText(SmsActivity.this, "Permission denied for SMS send.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_CONTACT_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();

                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    inputPhoneNumber.setText(number);
                }
                break;
        }
    }
}
