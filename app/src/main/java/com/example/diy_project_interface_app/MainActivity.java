package com.example.diy_project_interface_app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.diy_project_interface_app.Communication.Bluetooth.BluetoothDeviceActivity;
import com.example.diy_project_interface_app.Inner.CommunicationProtocol;
import com.example.diy_project_interface_app.Modules.Module;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /**
     * Use this Launcher to start new activities and catch any results from returning
     */
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent intent = result.getData();
            switch (result.getResultCode()){
                case 1: //Device connected
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
    //Communication device;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.actionbar));

        preferences = getSharedPreferences(getString(R.string.pref_shared_id),MODE_PRIVATE);

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
        getMenuInflater().inflate(R.menu.actionbar,menu);
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
        } else if(itemId == R.id.action_test){
            buildLayout();  //for testing only
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initiates requesting and building of the modules
     */
    private void buildLayout(){
        String buildInfo = buildRequest(); //Get buildInfo

        try {
            builder = new Builder(grid.getWidth(),grid.getHeight(), commprot.getColumns(buildInfo),buildInfo); //Instantiate Builder
            ArrayList<ArrayList<String>> moduleinfos = commprot.createModuleInfos(builder.buildInfo); //Get Module Infos (a list of a list of module parameters
            //At this point errors might have been avoided, so we can do new build confirmed stuff here

            moduleinfos.remove(0);
            for(ArrayList<String> module: moduleinfos){
                //buildModule(module); //TODO: change to return type module
                modules.add(buildModule(module));
            }
            isBuild = true;
            ModuleUpdateGetter updateGetter = new ModuleUpdateGetter();
            updateGetter.onPostExecute(preferences.getInt(getString(R.string.pref_id_upInt),getResources().getInteger(R.integer.pref_upInt_def)));;
        }catch (IllegalArgumentException e){
            toastIt(e.getMessage());
        }

    }

    private Module buildModule(ArrayList<String> moduleInfo){
        String[] aModuleInfo = new String[moduleInfo.size()];
        moduleInfo.toArray(aModuleInfo); //convert to array, to give to module
        int type = Integer.parseInt(aModuleInfo[0]);
        int width = Integer.parseInt(aModuleInfo[1]);
        int height = Integer.parseInt(aModuleInfo[2]);
        Point pos = builder.getNextPosition(width,height);
        Module module = new Module(0,"",0,0,new View(this)); //Todo: change to  //Modules.getModule(type, pos.x,pos.y,width,height etc...)

        builder.addRectangle(pos,width,height);
        //int viewid = module.getView();
        int viewId = R.layout.module_example; //test module

        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup modView = (ViewGroup) inflater.inflate(viewId,grid);
        ConstraintLayout constraint = (ConstraintLayout) modView.getChildAt(modView.getChildCount()-1);
        int vid = View.generateViewId();
        constraint.setId(vid+54812);
//        ConstraintSet conSet = new ConstraintSet();
//        conSet.clone(grid);
//        conSet.connect(constraint.getId(),ConstraintSet.TOP,grid.getId(),ConstraintSet.TOP);
//        conSet.connect(constraint.getId(),ConstraintSet.LEFT,grid.getId(),ConstraintSet.LEFT);
//        conSet.applyTo(grid);

        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(builder.getWidthPx(width),builder.getHeightPx(height));
        lparams.setMargins(builder.getWidthPx(pos.x),builder.getHeightPx(pos.y),0,0);
        constraint.setLayoutParams(lparams);

        return module;

    }

    private String buildRequest(){
        //Todo: request build info from device
        //return "_-_´5;`´0;4;1`___"; //Testing
        return "_-_´5;`´0;4;1`´0;1;3`´0;3;2`´0;5;1`´0;1;2`´0;1;3`´0;1;3`´0;2;2`´0;1;3`´0;2;2`´0;3;1`´0;2;1`___"; //Testing
    }

    private void buildReset(){
        //Clear Grid
        grid.removeAllViews();
        isBuild = false;
    }

    private void toastIt(String _message){
        Toast toast = new Toast(this);
        toast.setText(_message);
        toast.show();
    }

    private void updateModules(String _updateInfo){
        if(isBuild){
            for(ArrayList<String> moduleinfo:commprot.createModuleInfos(_updateInfo)){
                //modules.get(Integer.parseInt(moduleinfo.remove(0))).updateInformation(moduleinfo);  //TODO: need updateInformation to take in ArrayList<String> instead of String
            }
        }
    }

    private class ModuleUpdateGetter extends AsyncTask<Integer, Void, String>{
        int interval = 50;

        protected void onPostExecute(int i) {
            interval = i;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    long mark = System.currentTimeMillis();
                    //System.out.println(new Timestamp((int)System.currentTimeMillis()).toString());
                    getModuleUpdates();
                    handler.postDelayed(this, interval - System.currentTimeMillis() + mark);

                }
            },100);
        }


        @Override
        protected String doInBackground(Integer... integers) {
            interval = integers[0];
            return null;
        }
    }
    private void getModuleUpdates(){
        StringBuilder cmd = new StringBuilder("");
        for(Module module: modules){
            cmd.append(module.getInformation());
        }
        //if(device.isConnected())
        //  device.send(cmd);
    }

    private void openPreferences(){
        Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
        activityLauncher.launch(intent);
    }

    private void openDevices(){
        Intent intent = new Intent(MainActivity.this, BluetoothDeviceActivity.class);
        activityLauncher.launch(intent);
    }

    //TODO: keep communication active
    //need callback function for receiving with flags of what is active (enum)
    //make build process async and build on callback


}