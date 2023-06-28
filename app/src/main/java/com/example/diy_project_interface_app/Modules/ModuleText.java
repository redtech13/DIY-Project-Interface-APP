package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

/***
 * Author: Jonas Sbiegay
 * Studiengang: SMSB
 * ModuleText is a Module with a Title and changable Text
 */

public class ModuleText extends Module {

    protected String text;
    protected TextView vText, vTitle;

    /**
     * Dynamischer Konstruktor, der nach der Dokumentation im Wiki initialisiert
     * https://github.com/redtech13/DIY-Project-Interface-APP/wiki/Modules
     * @param _Param
     * @param _Position
     */
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

    /**
     * setzt die view sodass die Verbindung zwischen Instanz und GUI hergestellt werden kann
     * sofern vorhanden werden auch Parameter aus der Initialisierung direkt gesetzt
     * @param _view
     */
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

    /**
     * Gibt das Layout nach außen, da jede Modul ein anderes Layout nutzt
     * @param context
     * @return
     */
    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_text", "layout", context.getPackageName());
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
                vText.setText(receivedStrings.get(0));
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
                "Title, Text => " + name + ", " + text
                ;
    }
}
