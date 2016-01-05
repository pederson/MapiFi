package com.example.dylan.mapifi;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;


public class MainPage extends ActionBarActivity {
    protected WifiManager mainWifi;
    protected WifiInfo winfo;
    protected TextView strength_textview;
    protected LocationManager locationManager;
    protected boolean update_status;
    private android.os.Handler strengthHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        strength_textview = (TextView)findViewById(R.id.strength_text);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        winfo = mainWifi.getConnectionInfo();
        update_status = false;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        strengthHandler = new Handler();
    }

    protected Runnable mStrengthChecker = new Runnable() {
        @Override
        public void run() {
            updateStrength();
            strengthHandler.postDelayed(this, 100);
        }
    };

    protected void startWifiUpdates() {
        mStrengthChecker.run();
    }

    protected void stopWifiUpdates() {
        strengthHandler.removeCallbacks(mStrengthChecker);
    }

    protected void startLocationUpdates() {

    }

    protected void stopLocationUpdates() {

    }


    protected void onRunButton(View view){
        // toggle status
        update_status = !update_status;

        // get run button text
        TextView t = (TextView)findViewById(R.id.run_button);

        if (update_status){
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
            return;
        }

        // display the strength to user
        strength_textview.setText(String.valueOf(str));
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

}

