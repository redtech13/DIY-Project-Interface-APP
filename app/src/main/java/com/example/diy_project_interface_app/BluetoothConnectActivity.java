package com.example.diy_project_interface_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothConnectActivity extends AppCompatActivity {

    Button bt_disconnect_btn;

    UUID m_myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket m_bluetoothSocket = null;
    BluetoothAdapter m_bluetoothAdapter;
    Boolean m_isConnected = false;
    String m_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);

        bt_disconnect_btn = findViewById(R.id.bt_disconnect_btn);

        Intent intent = getIntent();
        m_address = intent.getStringExtra(BluetoothDeviceActivity.EXTRA_ADDRESS);

        new ConnectToDevice(this).execute();
        //TODO set Listener for Module elements
        //control_led_on.setOnClickListener { sendCommand("a") }
        //control_led_off.setOnClickListener { sendCommand("b") }
        bt_disconnect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
    }

    private void sendCommand(String input) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket.close();
                m_bluetoothSocket = null;
                m_isConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    private class ConnectToDevice extends AsyncTask<Void, Void, String> {

        Context context;
        Boolean connectSuccess = true;

        public ConnectToDevice(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = m_bluetoothAdapter.getRemoteDevice(m_address);
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    m_bluetoothSocket.connect();
                }
            } catch (IOException e) {
                connectSuccess = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!connectSuccess) {
                Toast.makeText(context, "couldn't connect to device", Toast.LENGTH_SHORT).show();
            }
            else {
                m_isConnected = true;
            }
        }
    }
}