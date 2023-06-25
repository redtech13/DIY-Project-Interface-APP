package com.example.diy_project_interface_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private void loadPreferences(){
        updateInterval.setText(Integer.toString(preferences.getInt(getString(R.string.pref_id_upInt),getResources().getInteger(R.integer.pref_upInt_def))));
    }

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

    private void savePreferences(){
        //update Interval
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.pref_id_upInt),Integer.parseInt(updateInterval.getText().toString()));
        editor.apply();
    }

    public void btnSave(View _view){
        if(checkPreferences()){
            savePreferences();
            finish();
        }else{
            toastIt(getString(R.string.ERR_pref_wrongArgument));
        }
    }

    public void btnCancel(View _view){
        finish();
    }

    private void toastIt(String _message){
        Toast toast = Toast.makeText(this,_message,Toast.LENGTH_LONG);
        toast.show();
    }
}
