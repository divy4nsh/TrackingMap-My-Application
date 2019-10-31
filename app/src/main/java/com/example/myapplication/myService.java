package com.example.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.Date;


public class myService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    String myUser = "";
    DatabaseReference mRef=FirebaseDatabase.getInstance("https://testproject-9e3d2.firebaseio.com/").getReference();
    private static final String CHANNEL_ID="MyDataChannelID";
    private static final String CHANNEL_NAME="MyDataChannelName";
    private static final String CHANNEL_DESC="MyDataChannelDescription";
    private static final String TAG = myService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    NotificationManagerCompat mNotificationMgr;

    @Override
    public void onCreate() {
        Intent myServiceIntent = new Intent(getApplicationContext(), myService.class);
        startService(myServiceIntent);
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getUid()!=null)
        {
            myUser = mAuth.getCurrentUser().getUid();
        }
        else
        {
            Toast.makeText(this, "ERROR, RESTART", Toast.LENGTH_SHORT).show();
        }

        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;

        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void sendLocationToFirebase(Location location)
    {
        Date currentTime = Calendar.getInstance().getTime();
        mRef.child(myUser).child(currentTime.toString()).child("latitude").setValue(location.getLatitude());
        mRef.child(myUser).child(currentTime.toString()).child("longitude").setValue(location.getLongitude());
    }
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(mLocationClient.isConnected()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        sendLocationToFirebase(location);
        sendNotification(location);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }
    public void sendNotification(Location location) {
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Tracking Active")
                .setContentText("Latitude: " + location.getLatitude()+"\n"+"Longitude: " + location.getLongitude())
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mNotificationMgr = NotificationManagerCompat.from(this);
        mNotificationMgr.notify(1,mBuilder.build() );
    }
}