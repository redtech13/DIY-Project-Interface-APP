package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.diy_project_interface_app.R;

public class Modules {
    Context ctx;

    Modules(Context _ctx){
        this.ctx = _ctx;
    }

    public Module getModule(int type, int moduleTypeId, String name, int width, int height) {
        switch (type) {
            case 1: // Slider
                View sliderLayout = LayoutInflater.from(ctx).inflate(R.layout.module_slider, null);
                return new ModuleSlider(moduleTypeId, name, width, height, sliderLayout);

            case 2:
                View exampleLayout = LayoutInflater.from(ctx).inflate(R.layout.module_example, null);
                return new Module(moduleTypeId, name, width, height, exampleLayout);

            default:
                return null;
        }
    }

    /**
     * Überladung der Funktion aufgrund von unterschiedlichen Parametern für den Slider
     * @param type
     * @param moduleTypeId
     * @param name
     * @param width
     * @param height
     * @param sliderValue
     * @return
     */
    public Module getModule(int type, int moduleTypeId, String name, int width, int height, float sliderValue) {
        if (type == 1) {
            View sliderLayout = LayoutInflater.from(ctx).inflate(R.layout.module_slider, null);
            return new ModuleSlider(moduleTypeId, name, width, height, sliderLayout, sliderValue);
        } else {
            // wron Parameter for this Module type
            return null;
        }
    }

}
