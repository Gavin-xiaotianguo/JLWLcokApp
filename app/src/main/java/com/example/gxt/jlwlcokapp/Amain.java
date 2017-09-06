package com.example.gxt.jlwlcokapp;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class Amain extends Activity {
    private final static String TAG = "JlwLock";
    private BluetoothAdapter mBluetoothAdapter; // = bluetoothManager.getAdapter();
    private Handler mHandler = new Handler();
    private boolean mScanning = false;
    private static final long SCAN_PERIOD = 1000000;
    //  private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = 0;
    private int mServiceState;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic1;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic2;
    private boolean mRescan;
    private Dialog Mydialog;
    String doornumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amain);
        Intent intent = getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle = intent.getExtras();//.getExtras()得到intent所附带的额外数据
        doornumber = bundle.getString("classname");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "ble_not_supported");
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        Log.i(TAG, "ble_ok_1");
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Adapter null", Toast.LENGTH_SHORT).show();
        }
        mBluetoothAdapter.enable();
        Log.i(TAG, "mBluetoothAdapter.enable");

        Log.i(TAG, "ble_ok_2");

        View button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                if (mConnectionState != BluetoothProfile.STATE_CONNECTED) {
                    message("Not connected: " + mConnectionState);
                    return;
                }
                if (mBluetoothGattService == null) {
                    message("Service not found");
                    return;
                }
                if (mBluetoothGattCharacteristic1 == null) {
                    message("Characteristic not found");
                    return;
                }


                byte[] buffer1 = {(byte) 0x00, 0x00, 0x00, 0x00, 0x00};
                mBluetoothGattCharacteristic1.setValue(buffer1);
                if (mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic1)) {
                    message("Write OK");
                    finish();
                }
                else {
                    message("Write fail");
                    finish();
                }
            }
        });
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索
            message("Start scanning");
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
            message("Stop scanning");
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            final String dstname = doornumber;
            if (!(dstname.equals(device.getName())))
                return;
            mBluetoothDeviceAddress = device.getAddress();
            scanLeDevice(false);
            connect(mBluetoothDeviceAddress);
        }
    };

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        message("Start Connect");
        // Previously connected device. Try to reconnect. (先前连接的设备。 尝试重新连接)
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = BluetoothProfile.STATE_CONNECTING;
                message("Reconnect");
                return true;
            }
            else {
                message("Rescan");
                scanLeDevice(true);
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            message("Device not found");
            Log.w(TAG, "Device not found.  Unable to connect.");
            scanLeDevice(true);
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback); //该函数才是真正的去进行连接
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = BluetoothProfile.STATE_CONNECTING;
        if (mBluetoothGatt == null) {
            message("Connecting fail");
            scanLeDevice(true);
            return false;
        }
        message("Connecting done");
        return true;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override  //当连接上设备或者失去连接时会回调该函数
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            mConnectionState = newState;
            message("ConnectionStateChange");

            if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
                message("Connected");
                mBluetoothGatt.discoverServices(); //连接成功后就去找出该设备中的服务 private BluetoothGatt mBluetoothGatt;
            }
            else if (newState == BluetoothProfile.STATE_CONNECTING) {  //连接失败
                message("Connecting");
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {  //连接失败
                message("Disconnected - Rescan");
                if (mRescan) scanLeDevice(true);
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTING) {  //连接失败
                message("Disconnecting");
            }
        }

        @Override  //当设备是否找到服务时，会回调该函数
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {   //找到服务了
                //在这里可以对服务进行解析，寻找到你需要的服务
                message("ServicesDiscovered");
                final List<BluetoothGattService> service_list = mBluetoothGatt.getServices();
                final String service_uuid = new String("fff0");
                final String characteristic1_uuid = new String("fff3");
                final String characteristic2_uuid = new String("fff1");
                for (BluetoothGattService service : service_list) {
                    message(service.getUuid().toString());
                    if (service.getUuid().toString().substring(4, 8).equals(service_uuid)) {
                        mBluetoothGattService = service;
                        message("Service Found");
                        break;
                    }
                }

                final List<BluetoothGattCharacteristic> characteristic_list = mBluetoothGattService.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristic_list) {
                    if (characteristic.getUuid().toString().substring(4, 8).equals(characteristic1_uuid)) {
                        mBluetoothGattCharacteristic1 = characteristic;
                        message("Characteristic1 found");
                    }
                    if (characteristic.getUuid().toString().substring(4, 8).equals(characteristic2_uuid)) {
                        mBluetoothGattCharacteristic2 = characteristic;
                        message("Characteristic2 found");
                        setCharacteristicNotification(mBluetoothGattCharacteristic2, true);
                        message("Set notify enable");
                    }
                }
            }
            else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override  //当读取设备时会回调该函数
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            System.out.println("onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //读取到的数据存在characteristic当中，可以通过characteristic.getValue();函数取出。然后再进行解析操作。
                //int charaProp = characteristic.getProperties();if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)表示可发出通知。  判断该Characteristic属性
                message("Char read: " + characteristic.getUuid());
                if (characteristic == mBluetoothGattCharacteristic1) {
                    message("Char AAC1: " + char2hex(characteristic.getValue()));
                }
                if (characteristic == mBluetoothGattCharacteristic2) {
                    message("Char AAC2: " + char2hex(characteristic.getValue()));
                }
            }
        }

        @Override //当向设备Descriptor中写数据时，会回调该函数
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            System.out.println("onDescriptorWriteonDescriptorWrite = " + status + ", descriptor =" + descriptor.getUuid().toString());
            message("Descriptor Write");
        }

        @Override //设备发出通知时会调用到该接口
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            message("Characteristic Changed: " + characteristic.getUuid().toString());
            if (characteristic == mBluetoothGattCharacteristic2) {
                message("Char AAC2: " + char2hex(characteristic.getValue()));
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        }

        @Override //当向Characteristic写数据时会回调该函数
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            message("Characteristic Write");
        }
    };

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
                .fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (descriptor != null) {
            System.out.println("write descriptor");
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void message(final String msg) {
        final TextView textView1 = (TextView) findViewById(R.id.textView1);
        if (textView1 != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CharSequence text = textView1.getText();
                    text = msg + "\r\n" + text;
                    textView1.setText(text);
                }
            });
        }
    }

    public String char2hex(byte[] buffer) {
        char[] num = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        String ret = "";
        for (int i = 0; i < (char) buffer.length; i++) {
            byte x = buffer[i];
            ret = ret + num[(x >> 4) & 0x0f] + num[x & 0x0f] + " ";
        }

        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBluetoothGatt != null) {
            mRescan = false;
            mBluetoothGatt.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mRescan = true;
        if (mConnectionState != BluetoothProfile.STATE_CONNECTED)
            scanLeDevice(true);
    }

}
