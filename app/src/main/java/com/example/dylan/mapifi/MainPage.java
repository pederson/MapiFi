package com.example.dylan.mapifi;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainPage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        update_status = false;
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        winfo = mainWifi.getConnectionInfo();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    protected void onUpdateButton(View view){
        // toggle status
        update_status = !update_status;

        // get run button text
        TextView t = (TextView)findViewById(R.id.run_button);

        updateStrength();
//        while(update_status) {
//            t.setText("Stop");
//            updateStrength();
//        }


        t.setText("Run");
    }

    protected void updateStrength(){

        // get the Wifi strength in dBm
        int str = getStrength();

        // if not connected to network...
        if (str > 100){
            TextView t = (TextView)findViewById(R.id.strength_text);
            t.setText("Wifi Unavailable");
            return;
        }

        // display the strength to user
        TextView t = (TextView)findViewById(R.id.strength_text);
        t.setText(String.valueOf(str));
    }

    /*
        returns Wifi Signal strength as an integer in dBm
        typical range is -inf to 0
        if the Wifi is disconnected, it returns 101
     */
    protected int getStrength(){
        // check the Wifi state
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

    protected LocationManager locationManager;
    protected WifiManager mainWifi;
    protected WifiInfo winfo;
    protected boolean update_status;
}
