package com.example.dylan.mapifi;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class MainPage extends ActionBarActivity {
    protected WifiManager mainWifi;
    protected WifiInfo winfo;
    protected TextView strength_textview;
    protected TextView location_textview;
    protected TextView accuracy_textview;
    protected TextView ip_textview;
    protected LocationManager locationManager;
    protected FallbackLocationTracker locTracker;
    protected boolean update_status;

    private android.os.Handler strengthHandler;
    private android.os.Handler locationHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        strength_textview = (TextView)findViewById(R.id.strength_text);
        location_textview = (TextView)findViewById(R.id.gps_text);
        accuracy_textview = (TextView)findViewById(R.id.accuracy_text);
        ip_textview = (TextView)findViewById(R.id.ip_text);


        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //winfo = mainWifi.getConnectionInfo();

        locTracker = new FallbackLocationTracker(this, ProviderLocationTracker.ProviderType.GPS);
        strengthHandler = new Handler();
        locationHandler = new Handler();

        update_status = false;
    }

    protected Runnable mStrengthChecker = new Runnable() {
        @Override
        public void run() {
            updateStrength();
            strengthHandler.postDelayed(this, 500);
        }
    };

    protected Runnable mLocationChecker = new Runnable() {
        @Override
        public void run() {
            updateLocation();
            locationHandler.postDelayed(this, 500);
        }
    };

    protected void startWifiUpdates() {
        mStrengthChecker.run();
    }

    protected void stopWifiUpdates() {
        strengthHandler.removeCallbacks(mStrengthChecker);
    }

    protected void startLocationUpdates() {
        locTracker.start();
        mLocationChecker.run();
    }

    protected void stopLocationUpdates() {
        locationHandler.removeCallbacks(mLocationChecker);
        locTracker.stop();
    }


    protected void onRunButton(View view){
        // toggle status
        update_status = !update_status;

        Log.d("hit_button", "button was pressed");

        // get run button text
        TextView t = (TextView)findViewById(R.id.run_button);

        if (update_status){
            Toast.makeText(this, "Capturing Data", Toast.LENGTH_LONG).show();
            t.setText("Stop");
            startWifiUpdates();
            startLocationUpdates();
        }
        else{
            t.setText("Run");
            stopWifiUpdates();
            stopLocationUpdates();
        }

    }

    protected void updateStrength(){

        // get the Wifi strength in dBm
        int str = getStrength();

        // if not connected to network...
        if (str > 100){

            strength_textview.setText("Wifi Unavailable");
            ip_textview.setText("---------------");
            return;
        }

        // display the strength to user
        strength_textview.setText(String.valueOf(str));

        // update the IP
        int IP = winfo.getIpAddress();
        String sIP = String.valueOf(IP);
        sIP = sIP.substring(0, 3)+"."+sIP.substring(3,5)+"."+sIP.substring(5, 8)+"."+sIP.substring(8);
        //ip_textview.setText(String.valueOf(winfo.getIpAddress()));
        ip_textview.setText(sIP);

    }

    /*
        returns Wifi Signal strength as an integer in dBm
        typical range is -inf to 0
        if the Wifi is disconnected, it returns 101
     */
    protected int getStrength(){
        // check the Wifi state
        //return 100;
        int state = mainWifi.getWifiState();
        if (state != WifiManager.WIFI_STATE_ENABLED){
            return 101;
        }

        // get the signal strength
        winfo = mainWifi.getConnectionInfo();
        int str = winfo.getRssi();
        return str;
        //return WifiManager.calculateSignalLevel(str, 100);

    }

    protected void updateLocation(){
        Location loc = locTracker.getLocation();
        double lat, lon;
        float acc;

        if (loc == null){
            location_textview.setText("GPS Unavailable");
            accuracy_textview.setText("---------------");
            return;
        }
        else {
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            acc = loc.getAccuracy();

            location_textview.setText(lat+", "+lon);
            accuracy_textview.setText(String.valueOf(acc));
        }



    }

}

