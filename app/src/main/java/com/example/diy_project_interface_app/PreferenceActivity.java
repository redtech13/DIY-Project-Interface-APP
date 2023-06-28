package com.example.diy_project_interface_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
/***
 * Author: David Wilke
 * Matrikelnummer: 18301
 * Studiengang: ETB8
 * created: 26.06.2023
 * Class to manage preferences and edit shared preferences
 */
public class PreferenceActivity extends AppCompatActivity {

    SharedPreferences preferences;
    EditText updateInterval;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        setSupportActionBar(findViewById(R.id.actionbar_pref));
        getSupportActionBar().setTitle(getString(R.string.pref_title));

        preferences = getSharedPreferences(getString(R.string.pref_shared_id),MODE_PRIVATE);

        updateInterval = (EditText) findViewById(R.id.pref_upInt);

        loadPreferences();
    }

    /**
     * Load preferences into view with current values
     */
    private void loadPreferences(){
        updateInterval.setText(Integer.toString(preferences.getInt(getString(R.string.pref_id_upInt),getResources().getInteger(R.integer.pref_upInt_def))));
    }

    /**
     * Check if all preferences are allowed, marks faulty preferences
     * @return true if ok
     */
    private boolean checkPreferences(){
        boolean noerror = true;
        //update Interval
        try {
            int val = Integer.parseInt(updateInterval.getText().toString());
            if(val <= 0)
                throw new NumberFormatException();
        }catch (NumberFormatException e){
            ((TableRow)updateInterval.getParent()).setBackground(getDrawable(R.drawable.pref_background_error));
            noerror = false;
        }

        return noerror;
    }

    /**
     * saves preferences
     */
    private void savePreferences(){
        //update Interval
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.pref_id_upInt),Integer.parseInt(updateInterval.getText().toString()));
        editor.apply();
    }

    /**
     * On save button clicked, check if saveable and saves
     * @param _view
     */
    public void btnSave(View _view){
        if(checkPreferences()){
            savePreferences();
            finish();
        }else{
            toastIt(getString(R.string.ERR_pref_wrongArgument));
        }
    }

    /**
     * closes activity without saving
     * @param _view
     */
    public void btnCancel(View _view){
        finish();
    }

    /**
     * Method to toast messages
     * @param _message to be toasted
     */
    private void toastIt(String _message){
        Toast toast = Toast.makeText(this,_message,Toast.LENGTH_LONG);
        toast.show();
    }
}
