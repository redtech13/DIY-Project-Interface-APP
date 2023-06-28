package com.example.diy_project_interface_app.Modules;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.view.View;

import androidx.annotation.LayoutRes;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;

/***
 * Author: Jonas Sbiegay
 * Studiengang: SMSB
 * Created: 18.06.2023
 * ModuleClass is the base for the specific modules in the GUI
 */

public class Module {
    protected int moduleTypeId;
    protected String name;
    protected int width, height;
    protected View relatedView;
    protected int positionX, positionY;
    protected boolean isEnabled;
    protected String description;
    protected boolean informationChanged;
    protected ArrayList<String> information;

    public Module(ArrayList<String> _Param, Point _Position) {
        this.moduleTypeId = Integer.parseInt(_Param.get(0));
        this.width = Integer.parseInt(_Param.get(1));
        this.height = Integer.parseInt(_Param.get(2));
        if(_Param.size() >= 4)
        {
            try {
                this.name = String.valueOf(_Param.get(3));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        this.positionX = _Position.x;
        this.positionY = _Position.y;
        this.isEnabled = false;
        this.description = "";
        this.informationChanged = false;
        this.information = new ArrayList<String>();
    }

    /**
     * Hier wird nach der Dokumentation im Wiki der String entgegengenommen, um das
     * initialisierte Modul zu aktualisieren
     * https://github.com/redtech13/DIY-Project-Interface-APP/wiki/Modules
     * @param receivedStrings
     */
    public void updateInformation(ArrayList<String> receivedStrings) {
        for (String str : receivedStrings) {
            //Code dann hier
        }
    }

    public ArrayList<String> getInformation() {
        if (informationChanged) {
            informationChanged = false;
            return information;
        }
        return null;
    }

    /**
     * setzt die view sodass die Verbindung zwischen Instanz und GUI hergestellt werden kann
     * sofern vorhanden werden auch Parameter aus der Initialisierung direkt gesetzt
     * @param _view
     */
    public void setView(View _view) {
        relatedView = _view;
    }

    public int getLayout(Context context) {
        return context.getResources().getIdentifier("module_example", "layout", context.getPackageName());
    }


    // relevante Getter und Setter
    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Die toString ist für ein Übersichtliches Debugging gedacht, sodass man sich das gesamte
     * Modul ausgeben lassen kann
     * @return
     */
    @Override
    public String toString() {
        return "ModulTypID => " + String.valueOf(this.moduleTypeId) + "\n"+
                "Name => " + this.name +"\n"+
                "Width, Height => " + String.valueOf(this.width) + ", " + String.valueOf(this.height) +"\n"+
                "posX, posY => " + String.valueOf(this.positionX) + ", " + String.valueOf(this.positionY) +"\n"+
                "informationChanged => " + String.valueOf(this.informationChanged) +"\n"+
                "information" + information.toString() + "\n"
                ;
    }
}
