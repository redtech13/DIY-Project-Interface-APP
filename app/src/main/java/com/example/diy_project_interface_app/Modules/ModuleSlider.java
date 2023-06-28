package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

/***
 * Author: Jonas Sbiegay
 * Studiengang: SMSB
 * ModuleSlider is a Module that has a slider that can be changed from controller and that
 * can controll the controller
 */

public class ModuleSlider extends Module {

    protected int sliderValue;
    protected SeekBar slider;

    /**
     * Dynamischer Konstruktor, der nach der Dokumentation im Wiki initialisiert
     * https://github.com/redtech13/DIY-Project-Interface-APP/wiki/Modules
     * @param _Param
     * @param _Position
     */
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
        else
        {
            sliderValue = 50;
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
        slider = _view.findViewById(R.id.slider);
        initSlider();
        if(sliderValue != 50){
            slider.setProgress(sliderValue, true);
        }
    }

    /**
     * Gibt das Layout nach außen, da jede Modul ein anderes Layout nutzt
     * @param context
     * @return
     */
    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_slider", "layout", context.getPackageName());
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
                sliderValue = Integer.parseInt(receivedStrings.get(0));
                slider.setProgress(sliderValue, true);
            } catch (NumberFormatException e) {
                System.out.println("Fehler beim Parsen des Strings zu int: " + e.getMessage());
            }
        }
    }

    /**
     * Setzt die Changelistener, sodass wenn im GUI etwas geändert wird, das Backend
     * entsprechend darauf reagiert
     */
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

    /**
     * Die toString ist für ein Übersichtliches Debugging gedacht, sodass man sich das gesamte
     * Modul ausgeben lassen kann
     * @return
     */
    @Override
    public String toString() {
        return super.toString() +
                "Slider|" + name + "| => " + String.valueOf(sliderValue)
                ;
    }

}
