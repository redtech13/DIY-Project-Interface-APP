package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

public class ModuleText extends Module {

    protected String text;
    protected TextView vText, vTitle;

    public ModuleText(ArrayList<String> _Param, Point _Position) {
        super(_Param, _Position);
        if(_Param.size() >= 5)
        {
            try {
                this.text = _Param.get(4);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setView(View _view){
        relatedView = _view;
        vText = _view.findViewById(R.id.contentText);
        vTitle = _view.findViewById(R.id.titleText);
        vTitle.setText(name);
        if(text != null){
            vText.setText(text);
        }
    }

    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_text", "layout", context.getPackageName());
    }

    @Override
    public void updateInformation(ArrayList<String> receivedStrings) {
        if (!receivedStrings.isEmpty()) {
            try {
                vText.setText(receivedStrings.get(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
