package com.example.diy_project_interface_app.Inner;

import android.content.Context;
import android.widget.Toast;

import com.example.diy_project_interface_app.Modules.Module;
import com.example.diy_project_interface_app.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: David Wilke
 * Studiengang: ETB8
 * Created: 13.06.2023
 * This Class is used to act as a layer between the communication to the microcontroller and the modules in the UI.
 * To transform information into handleable formats.
 */

enum PROT_STATE {EMPTY, VERSION, INIT}
public class CommunicationProtocol{
    Context ctx;
    PROT_STATE state = PROT_STATE.EMPTY;
    int version = 0;
    public CommunicationProtocol(Context _ctx){
        ctx = _ctx;
    }

    /**
     * Takes any module and uses a general interface to retrieve needed data
     * Then uses necessary formatting rules and applies them
     * @return The String of a module to be send to the uC
     */
    public String moduleToString(Module _module){
        StringBuilder builder = new StringBuilder();
        builder.append( ctx.getString(R.string.PROT_mod_prefix));
        //builder.append(module.getId());

        //if parameters aka commands for the uC are empty, no need to send
        //if(module.getParameters().size()== 0)
        //  return "";

        //For each parameter
        /*
        for(String param:module.getParameters()){
        builder.append(App.getContext().getString(R.string.PROT_mod_parameter));
        builder.append(param);
        }
         */

        builder.append( ctx.getString(R.string.PROT_mod_suffix));

        return builder.toString();
    }

    public String modulesToBuildInfo(ArrayList<Module> _modules){
    StringBuilder builder = new StringBuilder(ctx.getString(R.string.PROT_cmd_buildStart));
        for (int i = 0; i < _modules.size(); i++) {
            builder.append(ctx.getString(R.string.PROT_mod_prefix));
            builder.append(Integer.toString(i));
            builder.append(ctx.getString(R.string.PROT_mod_parameter));
            builder.append(moduleToString(_modules.get(i)));
            builder.append(ctx.getString(R.string.PROT_mod_suffix));
        }
    builder.append(ctx.getString(R.string.PROT_cmd_buildEnd));
        return builder.toString();
    }


    /**
     * Takes a long string containing the build info received from the uC
     * Splits the string into chunks of each module by protocol rules
     * @param _buildinfo The Build info send by the uC
     * @return A List of modules, the first should contain general layout information
     */
    public ArrayList<String> buildInfoToModuleStrings(String _buildinfo) throws IllegalArgumentException{
        ArrayList<String> modules = new ArrayList<String>();
        //if buildinfo contains start and end flags, work on it
        if(_buildinfo.startsWith(ctx.getString(R.string.PROT_cmd_buildStart)) && _buildinfo.endsWith(ctx.getString(R.string.PROT_cmd_buildEnd))){
            while(true){
                //Get module info start and end positions
                int start = _buildinfo.indexOf(ctx.getString(R.string.PROT_mod_prefix)) + 1;
                int end = _buildinfo.indexOf(ctx.getString(R.string.PROT_mod_suffix));
                //if no found then stop
                if(start < 1 || end < 0){
                    break;
                }
                //cut substring
                modules.add(_buildinfo.substring(start,end));
                //remove this part from buildinfo string so it cant be found again
                _buildinfo = _buildinfo.substring(end+1,_buildinfo.length());
            }

        }
        if(modules.size()==0)
            throw new IllegalArgumentException(ctx.getString(R.string.ERR_buildInfo_incomplete));
        return modules;
    }

    /**
     * Convert a buildinfo string into a list of module info each being a list
     * @param _buildinfo The build info string send by uC
     * @return List of Modules Infos of type list
     */
    public ArrayList<ArrayList<String>> createModuleInfos(String _buildinfo) throws IllegalArgumentException{
        ArrayList<ArrayList<String>> modules = new ArrayList<ArrayList<String>>();
        ArrayList<String> moduleinfos = buildInfoToModuleStrings(_buildinfo);
        for(int i = 0; i< moduleinfos.size() ;i++){
            modules.add( new ArrayList<String> (Arrays.asList(moduleinfos.get(i).split(ctx.getString(R.string.PROT_mod_parameter)))));
        }
        return modules;
    }

    /**
     * Retrieves the amount of columns information from the buildstring
     * @param _buildInfo Build Info send by uC
     * @return Amount of columns requested
     */
    public int getColumns(String _buildInfo) throws IllegalArgumentException{
        ArrayList<String> data = buildInfoToModuleStrings(_buildInfo);
        String first = data.get(0);
        String[] second = first.split(";");
        String third = second[0];
        return Integer.parseInt(third);
    }


    public String getHandshake(){
        return ctx.getString(R.string.PROT_cmd_versionRequest);
    }

    public int getHandshakeVersion(String _input) throws NumberFormatException{
        return Integer.parseInt(_input.replace(ctx.getString(R.string.PROT_cmd_versionRequest),""));
    }

    public String getBuildInfoRequest(){
        return ctx.getString(R.string.PROT_cmd_buildRequest);
    }

    public boolean checkBuildInfo(String _input){
        return _input.startsWith(ctx.getString(R.string.PROT_cmd_buildStart)) && _input.endsWith(ctx.getString(R.string.PROT_cmd_buildEnd));
    }

    public String getInit(){
        return ctx.getString(R.string.PROT_cmd_modeActive);
    }

    public boolean checkInit(String _input){
        return _input.equals(ctx.getString(R.string.PROT_mod_confirm));
    }
}
