package com.example.diy_project_interface_app.Modules;

import android.view.View;
import android.widget.SeekBar;

public class ModuleSlider extends Module {

    protected float sliderValue;

    public ModuleSlider(int moduleTypeId, String name, int width, int height, View relatedView, float sliderValue) {
        super(moduleTypeId, name, width, height, relatedView);
        this.sliderValue = sliderValue;

        initSlider();
    }

    private void initSlider(){
        SeekBar slider = (SeekBar) relatedView;

        //auf der Seekbar will ich unseren Slider aufbauen morgen
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Hier wird der ausgewählte Wert aktualisiert
                // progress enthält dann den aktuellen Wert des Sliders
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
