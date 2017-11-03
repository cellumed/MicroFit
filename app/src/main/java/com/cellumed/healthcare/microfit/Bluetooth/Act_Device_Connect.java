package com.cellumed.healthcare.microfit.Bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cellumed.healthcare.microfit.Home.Act_Home;
import com.cellumed.healthcare.microfit.Home.BackPressCloseHandler;
import com.cellumed.healthcare.microfit.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Act_Device_Connect extends BTConnectActivity implements IMP_CMD {

    private Context mContext;

    private int mInitStatus = 0;  // init. for send status req. 0: none. 1: sent status req. 2: recv status rsp.
    private int mInitReties =0;
    private TimerTask mTask;
    private Timer mRetryTimer;


    private DeviceListAdapter mDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;

    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 2;
    private static final int PERMISSION_REQUEST_GET_ACCOUNT = 3;

    private ProgressDialog dialog=null;
    private BackPressCloseHandler backPressCloseHandler;

    Handler SearchTimeoutHandler = new Handler();
    Handler ConnectTimeoutHandler = new Handler();

    ImageButton startbutton;
    ImageButton connectbutton;
    ListView deviceList;
    View updated;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = device.getName();
                if (deviceName != null && deviceName.length() > 0) {
                    // filtering by bluetooth name.
                    if (device.getName().contains("CMF-1000W")) {
                        if(dialog!=null) dialog.dismiss();
                        SearchTimeoutHandler.removeCallbacksAndMessages(null);
                        mDeviceListAdapter.addDevice(device);
                        mDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.act__device__connect);
        setTitle("");
        assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fF6669")));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //startService();

        backPressCloseHandler = new BackPressCloseHandler(this);

        startbutton = (ImageButton) findViewById(R.id.search_device);
        connectbutton = (ImageButton) findViewById(R.id.connect_device);
        deviceList = (ListView) findViewById(R.id.device_list);
        deviceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        deviceList.setOnItemClickListener(tempDevice);
        startbutton.setOnClickListener(startAction);
        connectbutton.setOnClickListener(connectDevice);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mHandler = new Handler();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    Button.OnClickListener startAction = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mScanning == false) {

                dialog = new ProgressDialog(mContext,R.style.AppCompatAlertDialogStyle);
                dialog.setTitle( mContext.getResources().getString(R.string.ScanTitle));
                dialog.setMessage( mContext.getResources().getString(R.string.Scanning));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();

                mScanning = true;
                mDeviceListAdapter.clear();
                mDeviceListAdapter.notifyDataSetChanged();

                scanDevice(true);

                SearchTimeoutHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            mScanning = false;
                            scanDevice(false);
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.CannotScan), Toast.LENGTH_SHORT).show();

                        }
                    }
                }, 20000);


            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    SearchTimeoutHandler.removeCallbacksAndMessages(null);
                }
                mScanning = false;
                scanDevice(false);
            }
        }
    };

    ListView.OnItemClickListener tempDevice = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
            if (device == null) {
                return;
            }

            if (updated != null)
                updated.setBackgroundColor(Color.TRANSPARENT);

            updated = view;
            deviceAddress = device.getAddress();
            Log.e("TAG", device.getAddress());
            view.setBackgroundColor(Color.GRAY);
        }
    };

    Button.OnClickListener connectDevice = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (deviceAddress == null) {
                Log.e("TAG", "Null address");
                return;
            }

            if (mScanning) {
                mScanning = false;
            }


            dialog = new ProgressDialog(mContext,R.style.AppCompatAlertDialogStyle);
            dialog.setTitle( mContext.getResources().getString(R.string.ConnectTitle));
            dialog.setMessage(mContext.getResources().getString(R.string.Connecting));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

            ConnectTimeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.ConnectFailed), Toast.LENGTH_SHORT).show();

                    }
                }
            }, 20000);

            mBluetoothAdapter.cancelDiscovery();
            mHandler.removeCallbacksAndMessages(null);

            mBluetoothConnectService.connect(deviceAddress);

            Log.e("TAG", "connect start " + deviceAddress);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        mDeviceListAdapter = new DeviceListAdapter();
        deviceList.setAdapter(mDeviceListAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Log.d("TAG","Act Device Connect. onPause");
        super.onPause();
        this.unregisterReceiver(mReceiver);
        if(mBluetoothConnectService!=null && mBluetoothConnectService.isDisconnected()) scanDevice(false);    // connecting이나 Conneted일때는 이미 정지되어있음
        mDeviceListAdapter.clear();

        // force to cancel if scan handler working
        mScanning = false;
        mHandler.removeCallbacksAndMessages(null);

        if (dialog!=null && dialog.isShowing()) dialog.dismiss();
    }

    @Override
    protected void connectedDevice() {

        // 처음 연결되었을때는 초기화 시퀀스를 보낸다.
        if (mInitStatus ==0) {

            mInitReties =0;
            if(mBluetoothConnectService!=null)
                mBluetoothConnectService.send(CMD_STATUS_REQ, "");

            mInitStatus = 1;

            mTask = new TimerTask() {

                @Override
                public void run() {

                    Log.d("TAG","Retry. sending status request again");
                    mInitReties++;
                    if(mInitReties > 3)
                    {
                        Toast.makeText(mContext, mContext.getResources().getString( R.string.ConnectFailed), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (mBluetoothConnectService != null) {
                            mBluetoothConnectService.send(CMD_STATUS_REQ, "");


                            if (mRetryTimer != null) mRetryTimer.schedule(mTask, 1000);
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.DisconnectWhileConnect), Toast.LENGTH_SHORT).show();
                        }
                    }
                } // end run
            };

            mRetryTimer = new Timer();
            mRetryTimer.schedule(mTask,1000);


        }

    }

    @Override
    protected void dataAvailableCheck(String data) {

        if (data.split(" ")[2].equals( CMD_STATUS_RSP) && mInitStatus == 1) {
            Log.d("TAG","Recv STATUS_RSP");

            if(mRetryTimer!=null) mRetryTimer.cancel();

            mInitStatus=2;
            Intent intent = new Intent(this, Act_Home.class);
            startActivity(intent);

            ConnectTimeoutHandler.removeCallbacksAndMessages(null);
			if(dialog!=null) dialog.dismiss();
            finish();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISSION_REQUEST_COARSE_LOCATION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if(requestCode==PERMISSION_REQUEST_EXTERNAL_STORAGE) {

        }
    }

    private void scanDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(() -> {
                mScanning = false;
                mBluetoothAdapter.startDiscovery();
                invalidateOptionsMenu();
            }, SCAN_PERIOD);

            mBluetoothAdapter.startDiscovery();
        } else {
            if(mBluetoothAdapter!=null) mBluetoothAdapter.cancelDiscovery();
            mHandler.removeCallbacksAndMessages(null);
            SearchTimeoutHandler.removeCallbacksAndMessages(null);
        }
        invalidateOptionsMenu();

    }

    private class DeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflater;

        public DeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflater = Act_Device_Connect.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BluetoothDevice device = mLeDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("Unknown");

            return convertView;
        }

    }

    static class ViewHolder {
        TextView deviceName;
    }

}
