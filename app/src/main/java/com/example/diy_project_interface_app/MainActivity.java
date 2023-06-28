package com.example.diy_project_interface_app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.diy_project_interface_app.Communication.Bluetooth.BluetoothConnectionService;
import com.example.diy_project_interface_app.Communication.Bluetooth.BluetoothDeviceActivity;
import com.example.diy_project_interface_app.Inner.CommunicationProtocol;
import com.example.diy_project_interface_app.Modules.Module;
import com.example.diy_project_interface_app.Modules.Modules;

import java.sql.Timestamp;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /**
     * Use this Launcher to start new activities and catch any results from returning
     */
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent intent = result.getData();
            switch (result.getResultCode()) {
                case 1: //Device connected
                    BluetoothDevice device = intent.getParcelableExtra("bt_device");
                    bt_connection = new BluetoothConnectionService(MainActivity.this);
                    if (device != null) {
                        bt_connection.startClient(device);
                    }

                    //TODO: extract bundle and active device class
                    //get device mac address and create communication class aka bluetooth
                    //or pass Communication Class Instance (better)
                    break;
                case 2: //Device not found / not connected
                    //try connecting to old device
                    break;
            }
        }
    });

    Builder builder;
    FrameLayout grid;
    CommunicationProtocol commprot;
    ArrayList<Module> modules;
    boolean isBuild = false;
    BluetoothConnectionService bt_connection;
    SharedPreferences preferences;

    public enum COMM_GOAL {NONE, HANDSHAKE, INIT, BUILDINFO}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.actionbar));

        preferences = getSharedPreferences(getString(R.string.pref_shared_id), MODE_PRIVATE);

        grid = (FrameLayout) findViewById(R.id.grid);
        commprot = new CommunicationProtocol(this);
        modules = new ArrayList<Module>();

        /*if(!device.isConnected){
            openDevices();
        }*/

    }

    /*
    App Bar at top (creation and callback)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_devices) {
            openDevices();
            return true;
        } else if (itemId == R.id.action_settings) {
            openPreferences();
            return true;
        } else if (itemId == R.id.action_test) {
            buildLayout(buildRequest());  //for testing only
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    public void onConnected(){
        CommHandler handler = new CommHandler(COMM_GOAL.HANDSHAKE,MainActivity.this);
        handler.start();
    }
    private void successHandshake(int _version){
        CommHandler com = new CommHandler(COMM_GOAL.INIT,this);
        com.start();
    }
    private void successInit(){
        CommHandler com = new CommHandler(COMM_GOAL.BUILDINFO,this);
        com.start();
    }
    private void successBuildInfo(String _buildInfo){
        buildLayout(_buildInfo);
    }

    /**
     * Initiates requesting and building of the modules
     */
    private void buildLayout() {
        String buildInfo = buildRequest(); //Get buildInfo

        CommHandler tcom = new CommHandler(COMM_GOAL.BUILDINFO, this);
        tcom.start();
    }

    public class CommHandler extends Thread {
        COMM_GOAL goal = COMM_GOAL.NONE;
        MainActivity ctx;

        public CommHandler(COMM_GOAL _goal, Context _ctx) {
            goal = _goal;
            ctx = (MainActivity) _ctx;
        }

        public void run() {
            boolean running = true;
            //if(bt_connection.isConnected)
            switch (goal) {
                case NONE:
                    break;
                case HANDSHAKE:
                    bt_connection.write(commprot.getHandshake().getBytes(StandardCharsets.UTF_8));
                    for (int i = 0; i < 150; i++) {
                        SystemClock.sleep(10);
                        try {
                            String input = bt_connection.getmInput();
                            Log.d("connection",input);
                            Log.d("connection","l:"+input.length());
                            int version = commprot.getHandshakeVersion(input);
                            if (version > 0) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       successHandshake(version);
                                    }
                                });
                                running = false;
                                break;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                    if (running)
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toastIt(getString(R.string.ERR_comm_handshake));
                            }
                        });
                    break;
                case INIT:
                    bt_connection.write(commprot.getInit().getBytes(StandardCharsets.UTF_8));
                    for (int i = 0; i < 150; i++) {
                        SystemClock.sleep(10);
                        try {
                            if (commprot.checkInit(bt_connection.getmInput())) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        successInit();
                                    }
                                });
                                running = false;
                                break;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                    if (running)
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toastIt(getString(R.string.ERR_comm_handshake));
                            }
                        });
                    break;
                case BUILDINFO:
                    bt_connection.write(commprot.getBuildInfoRequest().getBytes(StandardCharsets.UTF_8));
                    for (int i = 0; i < 150; i++) {
                        SystemClock.sleep(10);
                        try {
                            String input = bt_connection.getmInput();
                            if (commprot.checkBuildInfo(input)) {
                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        successBuildInfo(input);
                                    }
                                });
                                running = false;
                                break;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                    if (running){
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toastIt(getString(R.string.ERR_comm_buildinfo));
                            }
                        });
                    }else {

                    }

                    break;
            }
        }
    }

    private void buildLayout(String _buildInfo) {
        try {
            builder = new Builder(grid.getWidth(), grid.getHeight(), commprot.getColumns(_buildInfo), _buildInfo); //Instantiate Builder
            ArrayList<ArrayList<String>> moduleinfos = commprot.createModuleInfos(builder.buildInfo); //Get Module Infos (a list of a list of module parameters
            //At this point errors might have been avoided, so we can do new build confirmed stuff here

            moduleinfos.remove(0);
            for (ArrayList<String> module : moduleinfos) {
                //buildModule(module); //TODO: change to return type module
                modules.add(buildModule(module));
            }
            isBuild = true;
            ModuleUpdater updateGetter = new ModuleUpdater(preferences.getInt(getString(R.string.pref_id_upInt), getResources().getInteger(R.integer.pref_upInt_def)), this);
            //updateGetter.start();
        } catch (IllegalArgumentException e) {
            toastIt(e.getMessage());
        }
    }

    private Module buildModule(ArrayList<String> moduleInfo) {
        String[] aModuleInfo = new String[moduleInfo.size()];
        moduleInfo.toArray(aModuleInfo); //convert to array, to give to module
        int width = Integer.parseInt(aModuleInfo[1]);
        int height = Integer.parseInt(aModuleInfo[2]);
        Point pos = builder.getNextPosition(width,height);
        Module module = Modules.getModule(moduleInfo, pos);


        builder.addRectangle(pos,width,height);

        LayoutInflater inflater = LayoutInflater.from(this);
        int viewId = module.getLayout(this); //test module
        ViewGroup modView = (ViewGroup) inflater.inflate(viewId,grid);
        ConstraintLayout constraint = (ConstraintLayout) modView.getChildAt(modView.getChildCount()-1);
        module.setView(constraint);
      
//        ConstraintSet conSet = new ConstraintSet();
//        conSet.clone(grid);
//        conSet.connect(constraint.getId(),ConstraintSet.TOP,grid.getId(),ConstraintSet.TOP);
//        conSet.connect(constraint.getId(),ConstraintSet.LEFT,grid.getId(),ConstraintSet.LEFT);
//        conSet.applyTo(grid);

        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(builder.getWidthPx(width), builder.getHeightPx(height));
        lparams.setMargins(builder.getWidthPx(pos.x), builder.getHeightPx(pos.y), 0, 0);
        constraint.setLayoutParams(lparams);

        return module;

    }

    private String buildRequest() {
        //Todo: request build info from device
        //return "_-_´5;`´0;4;1`___"; //Testing
        return "_-_´5;`´3;3;1`´1;3;1;Test;`___"; //Testing
        //return "_-_´5;`´0;4;1`´3;1;3`´0;3;2`´3;5;1`´0;1;2`´0;1;3`´0;1;3`´0;2;2`´0;1;3`´0;2;2`´0;3;1`´0;2;1`___"; //Testing
    }

    private void buildReset() {
        //Clear Grid
        grid.removeAllViews();
        isBuild = false;
    }

    private void toastIt(String _message) {
        Toast toast = Toast.makeText(this, _message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updateModules(String _updateInfo) {
        if (isBuild) {
            for (ArrayList<String> moduleinfo : commprot.createModuleInfos(_updateInfo)) {
                //modules.get(Integer.parseInt(moduleinfo.remove(0))).updateInformation(moduleinfo);  //TODO: need updateInformation to take in ArrayList<String> instead of String
            }
        }
    }

    public class ModuleUpdater extends Thread {
        int interval = 50;
        MainActivity ctx;

        public ModuleUpdater(int _interval, Context _ctx) {
            interval = _interval;
            ctx = (MainActivity) _ctx;
        }

        public void run() {
            while (true) {
                long mark = System.currentTimeMillis();
                //System.out.println(new Timestamp((int)System.currentTimeMillis()).toString());
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getModuleUpdates();
                        String message = bt_connection.getmInput().toString();
                        if (!message.isEmpty())
                            updateModules(message);
                    }
                });

                SystemClock.sleep(interval - System.currentTimeMillis() + mark);
            }
        }


    }

    private void getModuleUpdates() {
        byte[] out = commprot.modulesToBuildInfo(modules).getBytes(StandardCharsets.UTF_8);
        //if(device.isConnected())
        bt_connection.write(out);
    }

    private void openPreferences() {
        Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
        activityLauncher.launch(intent);
    }

    private void openDevices() {
        Intent intent = new Intent(MainActivity.this, BluetoothDeviceActivity.class);
        activityLauncher.launch(intent);
    }

    //TODO: keep communication active
    //need callback function for receiving with flags of what is active (enum)
    //make build process async and build on callback


}