package com.bluetooth.xu.bluetoothstudy;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_UUID;

public class BlueToothActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_blue;
    MyBtReceiver myBtReceiver;
    int REQUEST_ENABLE_BT = 1;
    private String TAG = "BLUE_TOOTH_LOG";
    /**
     * BluetoothAdapter 拥有基本的蓝牙操作，例如开启蓝牙扫描，使用已知的 MAC 地址 （BluetoothAdapter#getRemoteDevice）实例化一个 BluetoothDevice 用于连接蓝牙设备的操作等等。
     */
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean mScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        btn_blue = findViewById(R.id.btn_open_blue_tooth);
        btn_blue.setOnClickListener(this);
        initBlueTooth();

    }

    /**
     * 检查蓝牙
     */
    private void initBlueTooth() {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
            Log.e(TAG, mBluetoothAdapter.getAddress() + "---" + mBluetoothAdapter.getName());
        }
        //设备不支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "该手机不支持蓝牙", Toast.LENGTH_LONG);
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


    }

    /**
     * 开启 扫描
     * 指定的 boolean startLeScan(UUID[] serviceUuids, BluetoothAdapter.LeScanCallback callback)
     *
     * @param v
     */
    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        /*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//  已经有匹配过得
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothDeviceArrayList.add(device);
            }
        }*/
        initBlueTooth();
        //TODO  检察蓝牙状态
        scanLeDevice(true);
    }

    private LinkedList<BluetoothDevice> bluetoothDeviceArrayList = new LinkedList<>();
    final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            bluetoothDeviceArrayList.add(device);
            Log.d(TAG, "run: scanning...");
        }

    };

    //停止 扫描
    @SuppressLint("NewApi")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // 预先定义停止蓝牙扫描的时间（因为蓝牙扫描需要消耗较多的电量）
           /* mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(callback);
                    mBluetoothAdapter.cancelDiscovery();
                    Log.e(TAG, "停止扫描");
                }
            }, 120000);*/

//            mScanning=mBluetoothAdapter.startLeScan(callback);
            mScanning = mBluetoothAdapter.startDiscovery();
            if (mScanning) {
                myBtReceiver = new MyBtReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_FOUND);
                filter.addAction(ACTION_UUID);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(myBtReceiver, filter);

            }
        } else {
            mScanning = false;
//            mBluetoothAdapter.stopLeScan(callback);
            mBluetoothAdapter.cancelDiscovery();
            Log.e(TAG, "停止扫描");
        }
    }


    //广播接收器
    private class MyBtReceiver extends BroadcastReceiver {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(getBaseContext(), "开始搜索 ...", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getBaseContext(), "搜索over ...", Toast.LENGTH_SHORT).show();
            } else if (ACTION_FOUND.equals(action)) {
                //获得发现的设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getAddress().equals("80:00:1A:7E:57:54")) {
                    device.connectGatt(getBaseContext(), true, gattCallback);
                }

            } else if (ACTION_UUID.equals(action)) {
                ParcelUuid par = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);
                Log.e(TAG,par.getUuid()+"------------------");
            }
        }
    }

    android.bluetooth.BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("NewApi")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {//连接上了
                    gatt.discoverServices();
                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.close();
                }
            } else {
                gatt.close();
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> servers = gatt.getServices();
                for (BluetoothGattService server : servers) {
                    for (BluetoothGattCharacteristic characteristics : server.getCharacteristics()) {
                        Log.e(TAG, characteristics.getUuid() + "");
                        gatt.readCharacteristic(characteristics);
                    }

                }
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                gatt.close();
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic characteristic,
                                         final int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Log.d(TAG, "read value: " + characteristic.getValue());
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                gatt.close();
            }

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK) {
            Toast.makeText(this, "蓝牙连接成功", Toast.LENGTH_LONG).show();
        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText(this, "取消蓝牙连接", Toast.LENGTH_LONG).show();
        }

    }

    public void clean() {
        bluetoothDeviceArrayList.remove();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBtReceiver);
    }
}
