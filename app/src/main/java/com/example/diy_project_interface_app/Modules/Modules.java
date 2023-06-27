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

    /**
     * 1 => SliderModule
     * 2 => TitleModule
     * 3 => Number/Text Module
     * @param type
     * @param moduleTypeId
     * @param name
     * @param width
     * @param height
     * @return
     */
    public Module getModule(int type, int moduleTypeId, String name, int width, int height, View relativeView) {
        switch (type) {
            case 1: // Slider
                return new ModuleSlider(moduleTypeId, name, width, height, relativeView);

            case 2: // Title, big Text
                return new Module(moduleTypeId, name, width, height, relativeView);

            case 3: // Number/Text Module, can be switched
                return new Module(moduleTypeId, name, width, height, relativeView);

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
    public Module getModule(int type, int moduleTypeId, String name, int width, int height, float sliderValue, View relativeView) {
        if (type == 1) {
            return new ModuleSlider(moduleTypeId, name, width, height, relativeView);
        } else {
            // wrong Parameter for this Module type
            return null;
        }
    }

}
