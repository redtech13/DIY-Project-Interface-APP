package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.TextView;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

public class ModuleTitle extends Module {
    protected String title;
    protected TextView vTitle;

    public ModuleTitle(ArrayList<String> _Param, Point _Position) {
        super(_Param, _Position);
        if(_Param.size() >= 5)
        {
            try {
                this.title = _Param.get(4);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setView(View _view){
        relatedView = _view;
        vTitle = _view.findViewById(R.id.largeText);
    }

    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_title", "layout", context.getPackageName());
    }

    @Override
    public void updateInformation(ArrayList<String> receivedStrings) {
        if (!receivedStrings.isEmpty()) {
            try {
                vTitle.setText(receivedStrings.get(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
