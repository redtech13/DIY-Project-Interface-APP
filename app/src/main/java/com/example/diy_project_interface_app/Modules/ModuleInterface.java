package com.example.diy_project_interface_app.Modules;

/***
 * Author: Jonas Sbiegay
 * Studiengang: SMSB
 * Created: 18.06.2023
 * ModuleClass is the base for the specific modules in the GUI
 */

public interface ModuleInterface
{
    void updateInformation(String receivedString);
    String getInformation();
}
