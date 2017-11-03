package com.cellumed.healthcare.microfit.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.cellumed.healthcare.microfit.Bluetooth.Act_Device_Connect;
import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.Bluetooth.ContextUtil;
import com.cellumed.healthcare.microfit.Bluetooth.PreferenceUtil;
import com.cellumed.healthcare.microfit.R;

public class SplashActivity extends BTConnectActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    ImageView imageView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.act_splash);
        ContextUtil.CONTEXT = this;
        getSupportActionBar().hide();
        imageView = (ImageView) findViewById(R.id.splashscreen);
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.splash_screen));

        startService();
        if (PreferenceUtil.lastConnectedDeviceAddress() != null) {
            mBluetoothConnectService.connect(PreferenceUtil.lastConnectedDeviceAddress());
            connectedDevice();
        } else {
            startHandler();
        }
    }

    private void startHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, Act_Device_Connect.class);
                //Intent mainIntent = new Intent(SplashActivity.this, Act_Home.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void connectedDevice() {
        Intent mainIntent = new Intent(SplashActivity.this, Act_Home.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void disconnectedDevice(int reason) {
        startHandler();
    }

    @Override
    protected void dataAvailableCheck(String data) {

    }
}