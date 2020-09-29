package com.bogdanlonchuk.minimalisticmoney.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.bogdanlonchuk.minimalisticmoney.R;
import com.kobakei.ratethisapp.RateThisApp;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Show RateThisApp dialog
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
        //Set Configs for RateThisApp dialog
        RateThisApp.Config config = new RateThisApp.Config(1, 2);
        RateThisApp.init(config);

        //Check if user have Internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            Intent startApp = new Intent(StartActivity.this, MainActivity.class);
            startActivity(startApp);
        } else {
            Intent noConnection = new Intent(StartActivity.this, NoConnection.class);
            startActivity(noConnection);
        }
    }
}
