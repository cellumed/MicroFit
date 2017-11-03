package com.cellumed.healthcare.microfit.Bluetooth;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;

import com.cellumed.healthcare.microfit.Util.BudUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;

@TargetApi(18)
public class BluetoothConnectService extends Service implements IMP_CMD {

    private final static String TAG = BluetoothConnectService.class.getSimpleName();
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_CONNECTED =
            "com.ikoob.ems_ui.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED =
            "com.ikoob.ems_ui.ACTION_DISCONNECTED";
    public final static String ACTION_WRONG_ID =
            "com.ikoob.ems_ui.ACTION_WRONG_ID";
    public final static String ACTION_DATA_AVAILABLE =
            "com.ikoob.ems_ui.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.ikoob.ems_ui.EXTRA_DATA";

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String userId ="29"; // bluetooth default id no
    //private static String userId ="41"; // bluetooth default id no
    private static boolean isInit = false;

    private BluetoothAdapter mAdapter;


    private ConnectionThread thread;

    private byte[] last_unacked_bytes;

    public class LocalBinder extends Binder {
        BluetoothConnectService getService() {
            return BluetoothConnectService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBinder;
    }

    /**
     * 주소로 연결하기
     *
     * @param address mac address
     */
    public void connect(String address) {
        BluetoothDevice device = mAdapter.getRemoteDevice(address);
        connect(device);
    }


    public void connect(BluetoothDevice device) {
        Log.e("TAG", device == null ? "null" : "not null, address : " + device.getAddress());
        //jun@ 이미 접속할 디바이스를 발견했으니 discover하지않음
        mAdapter.cancelDiscovery();
        mConnectionState = STATE_CONNECTING;
        ConnectThread thread = new ConnectThread(device);
        thread.start();
    }


    private void manageConnectedSocket(BluetoothSocket socket) {
        PreferenceUtil.putLastRequestDeviceAddress(socket.getRemoteDevice().getAddress());
        //mAdapter.cancelDiscovery();   // already canceled  @ run() in ConnectThread
        thread = new ConnectionThread(socket);
        thread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        @RequiresPermission(Manifest.permission.BLUETOOTH)
        public ConnectThread(BluetoothDevice device) {
            Method tmp_m = null;
            BluetoothSocket bs=null;
            try {
                // SPP UUID
                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                //bs = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                bs = device.createRfcommSocketToServiceRecord(MY_UUID);
                //tmp_m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
                //bs=(BluetoothSocket) tmp_m.invoke(device,1);
            } catch (IOException e)
            {

            } /*
            catch (NoSuchMethodException e) {
                e.printStackTrace();
                Log.d(TAG, "Unable to create insecure connection", e);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
*/
            mmSocket = bs;
        }


        public void run() {
            mAdapter.cancelDiscovery();
            if(mmSocket == null) {
                Log.e(TAG, "no socket to connect");
                return;
            }

            Log.d(TAG,"ConnectThread try connect");
            try {
                mmSocket.connect(); // blocking method. after 12 secs, timeout exception
            } catch (IOException e1) {
                e1.printStackTrace();
                try {
                    Log.e(TAG,"BT connect failed");
                    if (mmSocket.isConnected())
                     mmSocket.close();
                    broadcastUpdate(ACTION_DISCONNECTED);
                    mConnectionState = STATE_DISCONNECTED;

                } catch (IOException e2) {
                    e2.printStackTrace();
                    broadcastUpdate(ACTION_DISCONNECTED);
                    mConnectionState = STATE_DISCONNECTED;

                    Log.e(TAG,"unable to close socket");
                }
                //mConnectionState = STATE_DISCONNECTED;
                //mAdapter.startDiscovery();
                return;
            }
            Log.d(TAG,"ConnectThread connect end");

            mConnectionState = STATE_CONNECTED;
            manageConnectedSocket(mmSocket);
            broadcastUpdate(ACTION_CONNECTED);  /// act_device_connect에서 act_home 열때만 사용됨.

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_DISCONNECTED);
            }
        }
    }

    private class ConnectionThread extends Thread {
        private BluetoothSocket mmSocket;
        //private BluetoothDevice device;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectionThread(BluetoothSocket socket) {
            mmSocket = socket;
            //device = socket.getRemoteDevice();
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                //jun@. already connected @ connectThread.run
                //mmSocket.connect();
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"socket not created?",e);
                //cancel();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            mConnectionState = STATE_CONNECTED;
            Log.d(TAG,"in=" +tmpIn +" out=" + tmpOut);
        }


        public void run() {
            byte[] old_buffer = new byte[100];
            int old_len=0;
            boolean skip_packet;
            byte[] buffer = new byte[1024];
            int k;
            int bytes, byteAvailable;
            String out;

            Log.d(TAG,"connection run()");
            while (mConnectionState == STATE_CONNECTED) {
                try {
/*                    byteAvailable = mmInStream.available();
                    if(byteAvailable > 0)
                    {
                        Log.d(TAG," inputstream avialabe = " + byteAvailable);
                        bytes = mmInStream.read(buffer);

                        Log.d(TAG," inp = "+ new String(buffer,0,bytes));

                        broadcastUpdate(ACTION_DATA_AVAILABLE, bytes2String(buffer, bytes));
                    }
                    Thread.sleep(1);
*/
                    bytes = mmInStream.read(buffer);
                    out=bytes2String(buffer, bytes);
                    Log.d(TAG," inp = "+ out);

                    skip_packet = false;
                    if(old_len==0) {
                        if (buffer[0] != 0x21) {
                            // drop
                            skip_packet = true;
                            Log.e("BT", " no STX packet with no previous data. len=" + bytes);
                        }
                        if (buffer[0] == 0x21 && bytes >= 2 && buffer[1] > bytes) {
                            old_len = bytes;
                            System.arraycopy(buffer, 0, old_buffer, 0, bytes);
                            Log.e("BT", " new packet but short. len in pkt=" + Byte.toString(buffer[1]) + "rcv len=" + bytes);

                            skip_packet = true;
                        }
                        if(buffer[0]==0x21 && bytes==1)
                        {
                            old_len=1;
                            old_buffer[0]=buffer[0];

                            Log.e("BT", " only 1 byte recved");
                            skip_packet=true;
                        }
                        else
                        {
                            Log.e("BT", " rcv=" + bytes + "old_len="+ old_len + " buf[0]=" + Byte.toString(buffer[0]));
                        }
                    }
                    else if(old_len!=0)
                    {
                        System.arraycopy(buffer,0,old_buffer,old_len,bytes);
                        old_len+=bytes;
                        if(old_len<3)
                        {
                            skip_packet=true;
                            Log.e("BT", " pkt merged but too short. drop");

                        }
                        else if(old_len != old_buffer[1])
                        {
                            skip_packet=true;
                            Log.e("BT", " pkt merged but length is not match. rcvd=" + old_len + " pktlen="+ Byte.toString(old_buffer[1]));

                        }
                        else
                        {
                            bytes=old_len;
                            System.arraycopy(old_buffer,0,buffer,0,old_len);
                            out=bytes2String(buffer, bytes);
                            Log.d(TAG," resemble = "+ out);

                        }

                        old_len=0;
                    }

                    if(!skip_packet && bytes>= 3) {
                        // update version if incoming packet is STATUS_RSP
                        if (buffer[2] == 0x11) {

                            // STATUS = Ready (0x0B)
                            String[] sp = out.split(" ");
                            if (sp[4].length() == 1) sp[4] = "0" + sp[4];
                            if (sp[5].length() == 1) sp[5] = "0" + sp[5];
                            if (sp[6].length() == 1) sp[6] = "0" + sp[6];
                            if (sp[7].length() == 1) sp[7] = "0" + sp[7];


                            BudUtil.getInstance().FWVersion = sp[4] + "." + sp[5];
                            BudUtil.getInstance().HWVersion = sp[6] + "." + sp[7];

                            Log.d("recv STATUS_RSP", "FW " + BudUtil.getInstance().FWVersion);
                            Log.d("recv STATUS_RSP", "HW " + BudUtil.getInstance().HWVersion);
                        }

                        broadcastUpdate(ACTION_DATA_AVAILABLE, bytes2String(buffer, bytes));
                    }
                    else
                    {
                        Log.d("pktrcv", "skip=" + skip_packet + "len=" + bytes);

                    }

                } catch (IOException e) {
                    Log.e(TAG,"disconnected",e);

                    // disconnected goto first page
                    mConnectionState = STATE_DISCONNECTED;
                    broadcastUpdate(ACTION_DISCONNECTED);
/*
                    try {
                        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        mmSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                        mmSocket.connect();
                        mmInStream = mmSocket.getInputStream();
                        mmOutStream = mmSocket.getOutputStream();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        mConnectionState = STATE_DISCONNECTED;
                        broadcastUpdate(ACTION_DISCONNECTED);

                        break;
                    }
*/
                    e.printStackTrace();
                } /*catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

            }
        }

        public void write(byte[] bytes) {
            if(mConnectionState != STATE_CONNECTED)
            {
                Log.e("svcWrite", "write / not connected ");
            }
            try {

//                if (!this.isAlive())
//                    this.run();
                Log.e("SvcWrite", bytes2String(bytes, bytes.length));
                mmOutStream.write(bytes);
                last_unacked_bytes = bytes;
            } catch (IOException e) {
                e.printStackTrace();
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_DISCONNECTED);

            }
        }

        private String bytes2String(byte[] b, int count) {
            ArrayList<String> result = new ArrayList<String>();
            for (int i = 0; i < count; i++) {
                String myInt = Integer.toHexString((int) (b[i] & 0xFF));
                result.add(myInt);
            }
            return TextUtils.join(" ", result);
        }

        public void cancel() {
            Log.d(TAG,"connection cancel");
            try {
                mmSocket.close();
            } catch (IOException e) {
            } finally {
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_DISCONNECTED);
            }
        }
    }

    public ConnectionThread getThread() {
        return thread;
    }

    public void send(String cmd, String data) {
        try {
            thread.write(hexStringToByteArray(makeData(cmd, data)));
        } catch(Exception e)
        {
            // 중지되거나 해서 정상적으로 thread 생성을 안 한 경우 thread null exception.
            // 재연결로 이동함
            mConnectionState = STATE_DISCONNECTED;
            broadcastUpdate(ACTION_DISCONNECTED);
        }
    }

    public void send_raw(byte[] b) {
        try {
            thread.write(b);
        } catch(Exception e)
        {
            // 중지되거나 해서 정상적으로 thread 생성을 안 한 경우 thread null exception.
            // 재연결로 이동함
            mConnectionState = STATE_DISCONNECTED;
            broadcastUpdate(ACTION_DISCONNECTED);
        }
    }

    public void threadCancel() {
        Log.d("TAG", "thread cancel");
        if(thread !=null )
            try
            {
                thread.cancel();
            }
            catch(Exception e)
            {
                Log.e("BCS","exception. thread. cancel");
            }

    }

    private void broadcastUpdate(final String action) {
        Log.e("TAG", "Send broadcast");
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String data) {
        String cmd =data.split(" ")[2] ;
        final Intent intent = new Intent(action);

        if(checksumOK(data)==true)
        {
            if (cmd.equals(CMD_ID_INFO)) {
                if( data.split(" ")[3] != "41")
                {
                    broadcastUpdate(ACTION_WRONG_ID);
                    send(CMD_NAK, ERR_INVALID_ID);
                }
                else send(CMD_ACK, "");
            }
            else if(cmd.equals(CMD_NAK))
            {
                // 현재 timeout인 경우는 없음.
                if(last_unacked_bytes !=null) send_raw(last_unacked_bytes);
                return;
            }
            else if(cmd.equals(CMD_ACK))
            {
                last_unacked_bytes=null;
            }


            intent.putExtra(EXTRA_DATA, data);
            sendBroadcast(intent);

        }
        else {
            send(CMD_NAK, ERR_INVALID_CHECKSUM);
        }

    }

    //jun@ copy from PacketUtil.java
    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len - 1; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
    }

    public boolean isDisconnected() {
        return mConnectionState == STATE_DISCONNECTED;
    }

    private boolean checksumOK(String data) {
        String[] split_data=data.split(" ");

        int length = (split_data.length);
        int checkSum = 0;

        // except stx, etx, checksum
        for (int i = 1; i < length -2; i ++) {
            //tmp = Integer.parseInt(data.substring(i, i + 2), 16);
            checkSum += Integer.parseInt(split_data[i], 16);
        }

        if (checkSum > 255)
            checkSum = checkSum & 0x00ff;

        return (checkSum == Integer.parseInt(split_data[length-2],16));
    }

    private String makeData(String cmd, String data) {
        String header = "21";
        String footer = "75";
        int length = 5 + (data.length() / 2);
        int checkSum = Integer.parseInt(cmd, 16) + length;
        
        for (int i = 0; i < data.length() ; i += 2) {
            //tmp = Integer.parseInt(data.substring(i, i + 2), 16);
            checkSum += Integer.parseInt(data.substring(i, i + 2), 16);

        }

        if (checkSum > 255)
            checkSum = checkSum & 0x00ff;
        return header + String.format("%02X", length) + cmd + data + String.format("%02X", checkSum) + footer;
    }

    private String byteToString(byte a) {
        char[] hexChars = new char[2];
        int v = a & 0xFF;
        hexChars[0] = hexArray[v >>> 4];
        hexChars[1] = hexArray[v & 0x0F];

        return new String(hexChars);
    }

    private String byteArrayToString(byte[] a) {
        char[] hexChars = new char[a.length * 3];
        for (int j = 0; j < a.length; j++) {
            int v = a[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = new String(" ").charAt(0);
        }

        return new String(hexChars);
    }

    private byte[] intToByteArray(final int integer) {
        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.putInt(integer);
        buff.order(ByteOrder.BIG_ENDIAN);
        return buff.array();
    }

    public String getUserId() {
        if (userId == null)
            return "41";
        else
            return userId;
    }

    public boolean setUserId(String id) {
        if (id.length() != 2) return false;
        int k=-1;
        try {
            k = Integer.parseInt(id);
        } catch (NumberFormatException ex)
        {
            return false;

        }

        userId = id;
        return true;
    }

}
