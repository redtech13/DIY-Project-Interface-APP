package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.TextView;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

public class ModuleNumber extends Module {
    protected String Unit;
    protected TextView vNumber, vUnit;

    public ModuleNumber(ArrayList<String> _Param, Point _Position) {
        super(_Param, _Position);
        if(_Param.size() >= 5)
        {
            try {
                this.Unit = _Param.get(4);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setView(View _view){
        relatedView = _view;
        vNumber = _view.findViewById(R.id.valueText);
        vUnit = _view.findViewById(R.id.unitText);
        vUnit.setText(Unit);
    }

    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_number", "layout", context.getPackageName());
    }

    @Override
    public void updateInformation(ArrayList<String> receivedStrings) {
        if (!receivedStrings.isEmpty()) {
            try {
                vNumber.setText(receivedStrings.get(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
