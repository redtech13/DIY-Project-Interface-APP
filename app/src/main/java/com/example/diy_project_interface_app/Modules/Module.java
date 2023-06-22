package com.example.diy_project_interface_app.Modules;

import android.view.View;

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
    protected int xPosition, yPosition;
    protected View relatedView;
    protected int creationId;
    protected String title;
    protected boolean isEnabled;
    protected String description;
    protected boolean informationChanged;
    protected String information;

    public Module(int moduleTypeId, String name, int width, int height, View relatedView) {
        this.moduleTypeId = moduleTypeId;
        this.name = name;
        this.width = width;
        this.height = height;
        this.relatedView = relatedView;
        this.isEnabled = false;
        this.description = "";
        this.informationChanged = false;
        this.information = "";
    }

    public void updateInformation(String receivedString) {

    }

    public String getInformation() {
        if (informationChanged) {
            informationChanged = false;
            return information;
        }
        return "";
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

    public int getXPosition() {
        return xPosition;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getCreationId() {
        return creationId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getDescription() {
        return description;
    }
}
