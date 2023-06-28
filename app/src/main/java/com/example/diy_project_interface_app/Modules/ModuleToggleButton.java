package com.example.diy_project_interface_app.Modules;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

public class ModuleToggleButton extends Module {
    protected Boolean active;
    protected ToggleButton vToggleButton;

    public ModuleToggleButton(ArrayList<String> _Param, Point _Position) {
        super(_Param, _Position);
        if(_Param.size() >= 5)
        {
            try {
                if(_Param.get(4).equals("1")) {
                    this.active = true;
                }
                else{
                    this.active = false;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * setzt die view sodass die Verbindung zwischen Instanz und GUI hergestellt werden kann
     * sofern vorhanden werden auch Parameter aus der Initialisierung direkt gesetzt
     * @param _view
     */
    @Override
    public void setView(View _view){
        relatedView = _view;
        vToggleButton = _view.findViewById(R.id.toggleButton);
        if(name != null){
            vToggleButton.setTextOff(name + "_OFF");
            vToggleButton.setTextOn(name);
        }
        vToggleButton.setChecked(active);
        initToggleButton();
    }

    private void initToggleButton() {
        vToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vToggleButton.setBackgroundColor(ContextCompat.getColor(vToggleButton.getContext(), R.color.dark_green));
                    active = true;
                } else {
                    vToggleButton.setBackgroundColor(ContextCompat.getColor(vToggleButton.getContext(), R.color.light_gray));
                    active = false;
                }
                informationChanged = true;
                if (information.isEmpty()) {
                    if(active){
                        information.add("1");
                    }else {
                        information.add("0");
                    }
                } else {
                    if(active){
                        information.set(0, "1");
                    }else {
                        information.set(0, "0");
                    }
                }
            }
        });
    }

    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_togglebutton", "layout", context.getPackageName());
    }

    /**
     * Hier wird nach der Dokumentation im Wiki der String entgegengenommen, um das
     * initialisierte Modul zu aktualisieren
     * https://github.com/redtech13/DIY-Project-Interface-APP/wiki/Modules
     * @param receivedStrings
     */
    @Override
    public void updateInformation(ArrayList<String> receivedStrings) {
        if (!receivedStrings.isEmpty()) {
            try {
                boolean buffer;
                if(receivedStrings.get(0).equals("1")) {
                    buffer = true;
                }
                else{
                    buffer = false;
                }
                if(buffer != this.active){
                    this.active = buffer;
                    vToggleButton.setChecked(this.active);
                    if (this.active) {
                        vToggleButton.setBackgroundColor(ContextCompat.getColor(vToggleButton.getContext(), R.color.dark_green));
                    } else {
                        vToggleButton.setBackgroundColor(ContextCompat.getColor(vToggleButton.getContext(), R.color.light_gray));
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Die toString ist für ein Übersichtliches Debugging gedacht, sodass man sich das gesamte
     * Modul ausgeben lassen kann
     * @return
     */
    @Override
    public String toString() {
        return super.toString() +
                "Button|" + name + "| => " + active.toString()
                ;
    }
}
