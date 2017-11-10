package com.cellumed.healthcare.microfit.Home;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.Bluetooth.Act_Device_Connect;
import com.cellumed.healthcare.microfit.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microfit.Bluetooth.ContextUtil;
import com.cellumed.healthcare.microfit.Bluetooth.PreferenceUtil;
import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

public class SplashActivity extends BTConnectActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    ImageView imageView;
    TextView tv_version;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.act_splash);

        tv_version = (TextView)findViewById(R.id.version);

        try {
            PackageInfo pi = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            Log.i("pain0928", "Name: " + pi.packageName + " povider:" + pi.providers);
            tv_version.setText("App:" + pi.versionName + "\n" + "Firmware:" + BudUtil.getInstance().FWVersion );
        } catch(PackageManager.NameNotFoundException e) { }

        ContextUtil.CONTEXT = this;
        getSupportActionBar().hide();
        //imageView = (ImageView) findViewById(R.id.splashscreen);
        //imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.splash_screen));

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