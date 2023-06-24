package com.example.diy_project_interface_app;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.diy_project_interface_app.Communication.Bluetooth.BluetoothDeviceActivity;
import com.example.diy_project_interface_app.Inner.CommunicationProtocol;

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
                case 1: //Devices
                    //TODO: extract bundle and active device class
                    break;
            }
        }
    });

    Builder builder;
    FrameLayout grid;
    CommunicationProtocol commprot;
    //ArrayList<Module> modules;
    boolean isBuild = false;
    //Communication device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.actionbar));

        grid = (FrameLayout) findViewById(R.id.grid);
        commprot = new CommunicationProtocol(this);
        //modules = new ArrayList<Module>();

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
            //TODO: start device activity and return
            Intent intent = new Intent(MainActivity.this, BluetoothDeviceActivity.class);
            activityLauncher.launch(intent);
            return true;
        } else if (itemId == R.id.action_settings) {
            //TODO: start settings activity
            //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            //activityLauncher.launch(intent);
            buildLayout();
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
            ArrayList<ArrayList<String>> modules = commprot.createModuleInfos(builder.buildInfo); //Get Module Infos (a list of a list of module parameters
            //At this point errors might have been avoided, so we can do new build confirmed stuff here

            modules.remove(0);
            for(ArrayList<String> module: modules){
                buildModule(module); //TODO: change to return type module
                //modules.add(buildModule(module));
            }
            isBuild = true;
        }catch (IllegalArgumentException e){
            toastIt(e.getMessage());
        }

    }

    private void buildModule(ArrayList<String> moduleInfo){
        String[] aModuleInfo = new String[moduleInfo.size()];
        moduleInfo.toArray(aModuleInfo); //convert to array, to give to module
        int type = Integer.parseInt(aModuleInfo[0]);
        int width = Integer.parseInt(aModuleInfo[1]);
        int height = Integer.parseInt(aModuleInfo[2]);
        Point pos = builder.getNextPosition(width,height);
        //Module module = Modules.getModule(type, pos.x,pos.y,width,height etc...)

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

        //return module;

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
                //modules[moduleinfo.remove(0)].update(moduleinfo);
            }
        }
    }

    private void getModuleUpdates(){
        StringBuilder cmd = new StringBuilder("");
        /*for(Module module: modules){
            cmd.append(module.getCommand());
        }*/
        //if(device.isConnected())
        //  device.send(cmd);
    }

    //TODO: keep communication active
    //need callback function for receiving with flags of what is active (enum)
    //make build process async and build on callback

}