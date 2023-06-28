package com.example.diy_project_interface_app.Modules;

import android.graphics.Point;
import android.view.View;

import androidx.annotation.LayoutRes;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

/***
 * Author: Jonas Sbiegay
 * Studiengang: SMSB
 * Created: 18.06.2023
 * ModuleClass is the base for the specific modules in the GUI
 */

public class Module {
    protected int moduleTypeId;
    protected String name;
    protected int width, height;
    protected View relatedView;
    protected String title;
    protected boolean isEnabled;
    protected String description;
    protected boolean informationChanged;
    protected ArrayList<String> information;

    public Module(ArrayList<String> _Param, Point _Position) {
        this.moduleTypeId = Integer.parseInt(_Param.get(0));
        this.name = _Param.get(1);
        this.width = _Position.x;
        this.height = _Position.y;
        this.isEnabled = false;
        this.description = "";
        this.informationChanged = false;
        this.information = new ArrayList<String>();
    }

    public void updateInformation(ArrayList<String> receivedStrings) {
        for (String str : receivedStrings) {
            //Code dann hier
        }
    }

    public ArrayList<String> getInformation() {
        if (informationChanged) {
            informationChanged = false;
            return information;
        }
        return null;
    }

    public void setView(View _view){
        relatedView = _view;
    }

    public @LayoutRes int getLayout(){
        return R.layout.module_example;
    }

    // relevante Getter und Setter
    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getDescription() {
        return description;
    }
}
