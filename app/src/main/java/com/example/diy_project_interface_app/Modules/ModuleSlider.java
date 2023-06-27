package com.example.diy_project_interface_app.Modules;

import android.view.View;
import android.widget.SeekBar;

import java.util.ArrayList;

public class ModuleSlider extends Module {

    protected float sliderValue;
    protected SeekBar slider;


    public ModuleSlider(int moduleTypeId, String name, int width, int height, View relatedView, float sliderValue) {
        super(moduleTypeId, name, width, height, relatedView);
        this.sliderValue = sliderValue;
        slider = (SeekBar) relatedView;
        initSlider();
    }

    public ModuleSlider(int moduleTypeId, String name, int width, int height, View relatedView) {
        super(moduleTypeId, name, width, height, relatedView);
        this.sliderValue = 0;
        slider = (SeekBar) relatedView;
        initSlider();
    }

    @Override
    public void updateInformation(ArrayList<String> receivedStrings) {
        for (String str : receivedStrings) {
            // was für Daten bekomme ich hier?
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
                information = ""; //welches Format kommt hier rein
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
