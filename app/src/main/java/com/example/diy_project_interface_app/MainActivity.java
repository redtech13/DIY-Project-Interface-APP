package com.example.diy_project_interface_app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.diy_project_interface_app.Communication.Bluetooth.BluetoothConnectionService;
import com.example.diy_project_interface_app.Communication.Bluetooth.BluetoothDeviceActivity;
import com.example.diy_project_interface_app.Inner.Builder;
import com.example.diy_project_interface_app.Inner.CommunicationProtocol;
import com.example.diy_project_interface_app.Modules.Module;
import com.example.diy_project_interface_app.Modules.Modules;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/***
 * Author: David Wilke
 * Matrikelnummer: 18301
 * Studiengang: ETB8
 * Class is launched on startup and overviews all traffic and main grid
 */
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
    boolean isConnected = false;
    BluetoothConnectionService bt_connection;
    SharedPreferences preferences;

    public enum COMM_GOAL {NONE, HANDSHAKE, INIT, BUILDINFO}

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Device is now connected
                isConnected = true;
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
                isConnected = false;
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
                isConnected = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.actionbar));

        preferences = getSharedPreferences(getString(R.string.pref_shared_id), MODE_PRIVATE);

        grid = (FrameLayout) findViewById(R.id.grid);
        commprot = new CommunicationProtocol(this);
        modules = new ArrayList<Module>();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        if(bt_connection==null)
            openDevices();

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

    /**
     * Executed when device is connected, initiates handshake
     */
    public void onConnected(){
        CommHandler handler = new CommHandler(COMM_GOAL.HANDSHAKE,MainActivity.this);
        handler.start();
    }

    /**
     * Executed when handshake has been confirmed
     * @param _version of the library on end device
     */
    private void successHandshake(int _version){
        CommHandler com = new CommHandler(COMM_GOAL.INIT,this);
        com.start();
        toastIt("Found Library");
    }

    /**
     * Executed when End Device has been activated
     */
    private void successInit(){
        CommHandler com = new CommHandler(COMM_GOAL.BUILDINFO,this);
        com.start();
        //toastIt("Initialized");
    }

    /**
     * Executed when buildinfo is received
     * @param _buildInfo the received buildinfo
     */
    private void successBuildInfo(String _buildInfo){
        //toastIt("Got Buildinfo");
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

    /**
     * Request data and wait for answer on another thread
     */
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
                    for (int i = 0; i < 100; i++) {
                        SystemClock.sleep(10);
                        try {
                            String input = bt_connection.getmInput();
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
                    for (int i = 0; i < 100; i++) {
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
                    for (int i = 0; i < 100; i++) {
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

    /**
     * Builder for the GUI overall control
     * @param _buildInfo build data
     */
    private void buildLayout(String _buildInfo) {
        try {
            buildReset();
            builder = new Builder(grid.getWidth(), grid.getHeight(), commprot.getColumns(_buildInfo), _buildInfo); //Instantiate Builder
            ArrayList<ArrayList<String>> moduleinfos = commprot.createModuleInfos(builder.getBuildInfo()); //Get Module Infos (a list of a list of module parameters
            //At this point errors might have been avoided, so we can do new build confirmed stuff here

            moduleinfos.remove(0);
            for (ArrayList<String> module : moduleinfos) {
                modules.add(buildModule(module));
            }
            isBuild = true;
            ModuleUpdater updateGetter = new ModuleUpdater(preferences.getInt(getString(R.string.pref_id_upInt), getResources().getInteger(R.integer.pref_upInt_def)), this);
            updateGetter.start();
        } catch (IllegalArgumentException e) {
            toastIt(e.getMessage());
        }
    }

    /**
     * Places a single module in the gui as well instantiate module itself
     * @param moduleInfo cleaned information for a single module
     * @return finished and ready module
     */
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

    /**
     * Old path to request buildinfo, replaced with thread worker, now used for testing
     * @return buildinfo
     */
    private String buildRequest() {
        //return "_-_´5;`´0;4;1`___"; //Testing
        return "_-_´5;`´5;3;1;Lampe1;0`´3;3;1;Slider1;20`´1;1;2;Module;Text`´2;2;1;Speed;Einheit;20`´4;4;1;Broadcast;ModuleTitle`___"; //Testing
        //return "_-_´5;`´0;4;1`´3;1;3`´0;3;2`´3;5;1`´0;1;2`´0;1;3`´0;1;3`´0;2;2`´0;1;3`´0;2;2`´0;3;1`´0;2;1`___"; //Testing
    }

    /**
     * Clean GUI and memory of previously placed modules
     */
    private void buildReset() {
        //Clear Grid
        grid.removeAllViews();
        modules = new ArrayList<Module>();
        isBuild = false;
    }

    /**
     * Create a short toast
     * @param _message to be toasted
     */
    private void toastIt(String _message) {
        Toast toast = Toast.makeText(this, _message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Regularly called to update modules with new information
     * @param _updateInfo similar to buildinfo, but contains new values
     */
    private void updateModules(String _updateInfo) {
        if (isBuild) {
                for (ArrayList<String> moduleinfo : commprot.createModuleInfos(_updateInfo)) {
                    try {
                        modules.get(Integer.parseInt(moduleinfo.remove(0))).updateInformation(moduleinfo);
                    }catch (IndexOutOfBoundsException e){}

                }

        }
    }

    /**
     * Class to regularly keep communication between end device and app
     */
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
                        String message = bt_connection.getmInput();
                        if(commprot.checkBuildInfo(message))
                            updateModules(message);
                    }
                });

                SystemClock.sleep(Math.max(interval - System.currentTimeMillis() + mark,0));
            }
        }
    }

    /**
     * Gets new information from modules on app and send it to end device
     */
    private void getModuleUpdates() {
        byte[] out = commprot.modulesToBuildInfo(modules).getBytes(StandardCharsets.UTF_8);
        //if(device.isConnected())
        if(out.length>0)
            bt_connection.write(out);
    }

    /**
     * Switch to preference activity
     */
    private void openPreferences() {
        Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
        activityLauncher.launch(intent);
    }

    /**
     * Switch to devices activity
     */
    private void openDevices() {
        Intent intent = new Intent(MainActivity.this, BluetoothDeviceActivity.class);
        activityLauncher.launch(intent);
    }
}