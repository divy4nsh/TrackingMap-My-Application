package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean LOGIN_STATUS=false;
    boolean SIGNUP_STATUS=false;
    Button buttonregister,buttonlogin, buttonforgetpassword;
    EditText emailet, passwordet,emailRegisterEditText,passwordRegisterEditText,displaynameet;
    private FirebaseAuth mAuth;
    DatabaseReference mRef= FirebaseDatabase.getInstance("https://testproject-9e3d2.firebaseio.com/").getReference();
    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    public void callPermissions() {
        Permissions.check(this, permissions, "Need Permissions to get Location", null, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermissions();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false)
        {
            Toast.makeText(this, "ERROR, ENABLE LOCATION SERVICES FIRST", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        callPermissions();
        Firebase.setAndroidContext(this);
        final Intent myIntent=new Intent(MainActivity.this, Main2Activity.class);
        displaynameet=findViewById(R.id.displaynameet);
        buttonforgetpassword=findViewById(R.id.buttonforgetpassword);
        mAuth=FirebaseAuth.getInstance();
        emailRegisterEditText=findViewById(R.id.emailRegisterEditText);
        passwordRegisterEditText=findViewById(R.id.passwordRegisterEditText);
        buttonlogin=findViewById(R.id.buttonlogin);
        buttonregister=findViewById(R.id.buttonregister);
        emailet=findViewById(R.id.emailEditText);
        passwordet =findViewById(R.id.passwordEditText);
        buttonforgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailet.getText().toString()!=null)
                {
                    mAuth.sendPasswordResetEmail(emailet.getText().toString());
                    Toast.makeText(MainActivity.this, "Password Reset Success", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
                if(LOGIN_STATUS)
                {
                    startActivity(myIntent);
                }
            }
        });
    }

    private void updateDisplayName(String uid) {
        Log.i("status", String.valueOf(SIGNUP_STATUS));
        if(SIGNUP_STATUS)
        {
            String displayname= displaynameet.getText().toString();
            mRef.child("userData").child(uid).setValue(displayname);
            Toast.makeText(this, "SIGNUP SUCCESS", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRegistration()
    {
        String email, password;
        email=emailRegisterEditText.getText().toString();
        password=passwordRegisterEditText.getText().toString();
        emailRegisterEditText.setText("Waiting for Response");
        passwordRegisterEditText.setText("");
        if(email.equals("")||password.equals(""))
        {
            Log.i("details", email+"     "+password);
            Toast.makeText(this, "Enter Details Properly", Toast.LENGTH_SHORT).show();
            emailRegisterEditText.setText("Enter Details Properly");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                SIGNUP_STATUS=true;
                updateDisplayName(mAuth.getCurrentUser().getUid());
            }
        });

    }
    private void startSignIn() {
        String email, password;
        email=emailet.getText().toString();
        password=passwordet.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Unable to Login", Toast.LENGTH_SHORT).show();
            }
        });
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(this, new OnSuccessListener<AuthResult>()
        {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
    }
}