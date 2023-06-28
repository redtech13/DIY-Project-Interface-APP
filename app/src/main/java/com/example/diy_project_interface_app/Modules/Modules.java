package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

public class Modules {
    Context ctx;

    Modules(Context _ctx){
        this.ctx = _ctx;
    }


    public Module getModule(ArrayList<String> _Param , Point _Position) {
        if (!_Param.isEmpty()) {
            switch (_Param.get(0)) {
                case "1":
                    //return new ModuleText(_Param , _Position);

                case "2":
                    //return new ModuleNumber(_Param , _Position);

                case "3":
                    return new ModuleSlider(_Param , _Position);

                case "4":
                    //return new ModuleTitle(_Param , _Position);

                default:
                    return null;
            }
        return null;
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
