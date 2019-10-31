package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.client.Firebase;;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

//    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
//    public void callPermissions() {
//        Permissions.check(this, permissions, "Need Permissions to get Location", null, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//            }
//            @Override
//            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
//                super.onDenied(context, deniedPermissions);
//                callPermissions();
//            }
//        });
//    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main);
//        callPermissions();
        Intent myServiceIntent = new Intent(getApplicationContext(),myService.class);
        startForegroundService(myServiceIntent);
        Firebase.setAndroidContext(this);
    }
}
