package com.example.diy_project_interface_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    Button deviceRefreshBtn;

    static final String EXTRA_ADDRESS = "Device_address";
    Set<BluetoothDevice> deviceList;
    BluetoothManager bluetoothManager;
    BluetoothAdapter mBlueAdapter;

    BluetoothLeScanner leScanner;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        deviceRefreshBtn = findViewById(R.id.deviceRefreshBtn);

        deviceList = new HashSet<>();

        //adapter
        bluetoothManager = getSystemService(BluetoothManager.class);
        mBlueAdapter = bluetoothManager.getAdapter();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        if (!mBlueAdapter.isEnabled()) {
            //intent to on bluetooth
            mBlueAdapter.enable();
            mBlueAdapter.startDiscovery();
            showToast("Turning On Bluetooth...");
        } else {
            showToast("Bluetooth is already on");
            pairedDeviceList();
        }
        leScanner = mBlueAdapter.getBluetoothLeScanner();

        //get paired devices btn click
        deviceRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDeviceList();
            }
        });

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        mBlueAdapter.cancelDiscovery();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //bluetooth is on
                    showToast("Bluetooth is on");
                    pairedDeviceList();
                } else {
                    //user denied to turn bluetooth on
                    showToast("Could not turn on bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pairedDeviceList() {
        //filter Devices (TAG in name)
        //Send byte array on connect
        Set<BluetoothDevice> m_pairedDevices = mBlueAdapter.getBondedDevices();

        if (!m_pairedDevices.isEmpty()) {
            deviceList.addAll(m_pairedDevices);
        }

        // Create a BroadcastReceiver for ACTION_FOUND.

        if(!mBlueAdapter.isDiscovering()) {
            mBlueAdapter.startDiscovery();
        }

        RecyclerView recyclerView = findViewById(R.id.bt_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this, deviceList, new BluetoothDeviceAdapter.OnItemClickListener(){
            @Override public void onItemClick(BluetoothDevice item) {
                //TODO establish connection
                Intent intent = new Intent(BluetoothDeviceActivity.this, BluetoothConnectActivity.class);
                intent.putExtra(EXTRA_ADDRESS, item.getAddress());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    //toast message function
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}