package com.example.diy_project_interface_app;

import android.content.res.Resources;

/**
 * Author: David Wilke
 * Studiengang: ETB8
 * Created: 13.06.2023
 * This class contains general methods that could be used or needed anywhere
 */
public class Helper {
    /**
     * Gets the string from resources
     * @param _ResId Id at resource
     * @return String from resource
     */
    public static String getResString(int _ResId){
        return Resources.getSystem().getString(_ResId);
    }
}
