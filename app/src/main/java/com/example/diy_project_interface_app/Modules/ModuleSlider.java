package com.example.diy_project_interface_app.Modules;

import android.view.View;

public class ModuleSlider extends Module {

    protected float sliderValue;

    public ModuleSlider(int moduleTypeId, String name, int width, int height, View relatedView, float sliderValue) {
        super(moduleTypeId, name, width, height, relatedView);
        this.sliderValue = sliderValue;
    }

}
