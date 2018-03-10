package com.bluetooth.xu.bluetoothstudy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.bluetooth.BluetoothDevice.ACTION_UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_blue;
    private BluetoothAdapter mBluetoothAdapter;
    int REQUEST_ENABLE_BT = 001;
    private String TAG = "BLUE_TOOTH_LOG";
    private int REQUEST_COARSE_LOCATION = 002;
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


    private void initBlueTooth() {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        //是否支持 低耗功能 BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "该手机不支持低耗蓝牙", Toast.LENGTH_LONG);
            finish();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {//必须是Android4.3 以上 (SDK_INT)API==18
                mBluetoothAdapter = bluetoothManager.getAdapter();
                if (!mBluetoothAdapter.isEnabled()) {//没打开蓝牙，则请求打开
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            } else {
                Toast.makeText(this, "该手机不支持蓝牙", Toast.LENGTH_LONG);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                return;
            }
        }


    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        mBluetoothAdapter.startLeScan(callback);
    }

    @SuppressLint("NewApi")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // 预先定义停止蓝牙扫描的时间（因为蓝牙扫描需要消耗较多的电量）
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(callback);

                }
            }, 120000);

            mScanning = mBluetoothAdapter.startLeScan(callback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(callback);
            Log.e(TAG, "停止扫描");
        }
    }

    final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord);
            if (ibeacon != null) Log.e(TAG, ibeacon.proximityUuid);
        }

    };

    @SuppressLint("NewApi")
    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothAdapter.stopLeScan(callback);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(callback);
    }
}
