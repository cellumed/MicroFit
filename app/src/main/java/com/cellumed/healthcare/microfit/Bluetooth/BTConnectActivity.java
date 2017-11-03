package com.cellumed.healthcare.microfit.Bluetooth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.cellumed.healthcare.microfit.R;
import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.lang.reflect.Field;
import java.util.Locale;

public abstract class BTConnectActivity extends AppCompatActivity {

    public Typeface typeface;
    boolean force_service_disconnect=false;
    void setDefaultFont()
    {
        if(typeface == null) {
            typeface = Typeface.createFromAsset(this.getAssets(), "NotoSansKR-Regular-Hestia.otf");
        }
        Field f= null;
        try {
            f = Typeface.class.getDeclaredField("DEFAULT");
           // f = Typeface.class.getDeclaredField("monospace");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(null, typeface);
            Log.e("PAIN0928", "change Font !!!!!!!!!!!!!!");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    protected static boolean mConnected = false;

    protected BluetoothConnectService mBluetoothConnectService = null;

    protected static String deviceAddress;

    private static boolean pauseState;
    private static boolean disconnectedState;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.e("TAG", "Service Connected");
            mBluetoothConnectService = ((BluetoothConnectService.LocalBinder) service).getService();
            mConnected = true;

            if(force_service_disconnect) {
                force_service_disconnect = false;
                mBluetoothConnectService.threadCancel();

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothConnectService = null;
            mConnected = false;
            Log.e("TAG", "Service Disconnected");
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data;
            final String action = intent.getAction();
            Log.e("TAG", action);
            if (BluetoothConnectService.ACTION_CONNECTED.equals(action)) {
                connectedDevice();
            } else if (BluetoothConnectService.ACTION_DISCONNECTED.equals(action)) {
                disconnectedDevice(0);
            } else if (BluetoothConnectService.ACTION_WRONG_ID.equals(action)) {
                disconnectedDevice(1);
            } else if (BluetoothConnectService.ACTION_DATA_AVAILABLE.equals(action)) {

                data=intent.getStringExtra(BluetoothConnectService.EXTRA_DATA);
                Log.d("onBroadcastRcv",data);

                dataAvailableCheck(intent.getStringExtra(BluetoothConnectService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultFont();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startService();

        //checkLang();
    }

    protected void startService() {
        if (mConnected) {
            Intent gattServiceIntent = new Intent(this, BluetoothConnectService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            Log.e("TAG", "bindService");
        } else {
            Intent gattServiceIntent = new Intent(this, BluetoothConnectService.class);
            gattServiceIntent.putExtra("address", deviceAddress);
            startService(gattServiceIntent);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            Log.e("TAG", "startService");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.e("BTL","onRes");
        checkLang();
        if (disconnectedState) {
            finishAllActivityAndStartConnectActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(mServiceConnection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void checkLang()
    {
        // 국자정보를 가져와서 비교. 바뀌었으면 새로 세팅
        String sfName="EMS_LANG";   // 언어
        SharedPreferences sf = this.getSharedPreferences(sfName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();

        Locale systemLocale = getApplicationContext().getResources().getConfiguration().locale;
        //String strCountry = systemLocale.getCountry(); // KR
        String strLanguage = systemLocale.getLanguage(); // ko


        String lang=sf.getString("Lang","");
        Log.e("info","cur lang="+strLanguage + " old lang=" + lang);
        if(!lang.equals(strLanguage) && lang.length() > 0)
        {
            new  com.cellumed.healthcare.microfit.DataBase.DataBases(this).reset();
            Log.e("info","language changed. db reset!" );

            // reset
            editor.putString("Lang", strLanguage);
            editor.commit();

            //Toast toast=null;
            //toast = Toast.makeText(this,"Language is changed. Restart App"/*WrongIdReconnect*/, Toast.LENGTH_SHORT);
            //if(toast!=null) toast.show();

            // 현 시점에 service가 connect되었다는 보장이 없음
            force_service_disconnect= true;
            finishAllActivityAndStartConnectActivity();
        }
        if(lang.length()==0)
        {
            editor.putString("Lang", strLanguage);
            editor.commit();
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothConnectService.ACTION_CONNECTED);
        intentFilter.addAction(BluetoothConnectService.ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothConnectService.ACTION_WRONG_ID);
        intentFilter.addAction(BluetoothConnectService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    protected abstract void connectedDevice();

    protected void disconnectedDevice(int reason) {

        Toast toast=null;
		if(reason==0)
		{
            String r=getResources().getString(R.string.MoveToReconnect);
			toast = Toast.makeText(this,
                    r /*)*/, Toast.LENGTH_SHORT);
		}
		else if(reason==1)
		{
            String r=getResources().getString(R.string.WrongIdReconnect);
			toast = Toast.makeText(this,
                    r /*WrongIdReconnect*/, Toast.LENGTH_SHORT);
		}

        if(toast!=null) toast.show();
        finishAllActivityAndStartConnectActivity();

    }

    protected abstract void dataAvailableCheck(String data);

    private void finishAllActivityAndStartConnectActivity() {
        disconnectedState = false;
        for (int i = 0; i < BudUtil.actList.size(); i++) {
            BudUtil.actList.get(i).finish();
        }
        finish();
        Intent intent = new Intent(this, Act_Device_Connect.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("fin","act Dev connect intent");
        startActivity(intent);
    }

}
