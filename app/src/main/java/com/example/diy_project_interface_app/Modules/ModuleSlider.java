package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.view.View;
import android.widget.SeekBar;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

public class ModuleSlider extends Module {

    protected float sliderValue;
    protected SeekBar slider;


    public ModuleSlider(ArrayList<String> _Param, Point _Position) {
        super(_Param, _Position);
        if(_Param.size() >= 5)
        {
            try {
                this.sliderValue = Integer.parseInt(_Param.get(4));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setView(View _view){
        relatedView = _view;
        slider = _view.findViewById(R.id.slider);
        initSlider();
    }

    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_slider", "layout", context.getPackageName());
    }


    @Override
    public void updateInformation(ArrayList<String> receivedStrings) {
        for (String str : receivedStrings) {
            if (!receivedStrings.isEmpty()) {
                try {
                    sliderValue = Integer.parseInt(receivedStrings.get(0));
                } catch (NumberFormatException e) {
                    System.out.println("Fehler beim Parsen des Strings zu int: " + e.getMessage());
                }

            }
        }
    }

    private void initSlider(){
        //auf der Seekbar will ich unseren Slider aufbauen morgen
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Hier wird der ausgewählte Wert aktualisiert
                // progress enthält dann den aktuellen Wert des Sliders
                informationChanged = true;
                if (information.isEmpty()) {
                    information.add(String.valueOf(progress));
                } else {
                    information.set(0, String.valueOf(progress));
                }
            }

            // Wird aufgerufen, wenn Slider berührt wird
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // Wird aufgerufen, wenn der Slider released wird
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



}
