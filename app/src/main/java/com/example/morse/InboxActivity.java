package com.example.morse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {
    ListView inboxList;

    final static int READ_SMS_PERMISSION_REQUEST_CODE = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        inboxList = findViewById(R.id.inbox_list);

        if (checkPermission(Manifest.permission.READ_SMS)) {
            if(fetchInbox() != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fetchInbox());
                inboxList.setAdapter(adapter);
            }

            inboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String morseSent = fetchInbox().get(position).split("\n")[1].split(" ",3)[2];
                    //Toast.makeText(InboxActivity.this, morseSent, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), ConvertActivity.class);
                    intent.putExtra("messageToDecode", morseSent);

                    startActivity(intent);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public ArrayList<String> fetchInbox() {
        ArrayList<String> sms = new ArrayList<String>();

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        //cursor.moveToFirst();
        while(cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));

            if(body.contains("#morse")) {
                sms.add("Sender: " + address + "\nMessage: " + body);
            }
        }

        return sms;
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_SMS_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if(fetchInbox() != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fetchInbox());
                        inboxList.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(InboxActivity.this, "Permission denied for SMS read.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
