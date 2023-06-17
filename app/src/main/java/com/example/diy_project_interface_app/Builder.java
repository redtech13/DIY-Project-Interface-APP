package com.example.diy_project_interface_app;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Vector;

/***
 * Author: David Wilke
 * Studiengang: ETB8
 * Created: 17.06.2023
 * Builder class holds information and methods to create grid layout in main
 */
public class Builder {
    String buildInfo = "";
    int screenwidth;
    int screenheight;
    int columns;
    int cellwidth;
    int cellheight;
    ArrayList<boolean[]> used;

    public Builder(int _screenwidth, int _screenheight, int _columns, String _buildInfo){
        buildInfo = _buildInfo;
        screenwidth=_screenwidth;
        screenheight=_screenheight;
        columns = _columns;
        cellwidth = screenwidth / columns;
        cellheight = cellwidth;
        used = new ArrayList<boolean[]>();
    }

    /**
     * Calculates given the current used array the coordinates, where the rectangular object can be placed
     * @param _width of rectangle in columns
     * @param _height of rectangle in rows
     * @return the placement coordinates
     */
    public Point getNextPosition(int _width, int _height){
        boolean cont = true;
        int count = 0;
        Point out = new Point(0,0);
        while(cont){ //continues until space was found, should sooner or later find one
            if(count>=used.size()) { // in case all rows are full (or no space found) create at new line
                out = new Point(0, count);
                break;
            }
            int space = 0;
            for(int i = 0;i<columns;i++){
                if(used.get(count)[i]){  //check each column of row and count free succeeding spaces
                    space = 0;
                }else {
                    space++;
                }
                if(space >= _width)  //if one fits check further
                    if(checkNextLines(new Point(i-_width+1,count),_width,_height)) {
                        out = new Point(i-_width+1, count);
                        cont = false;
                        break;
                    }
            }
            count++;
        }
        return out;
    }

    /**
     * Check if the same space as found is available beneath for the needed height
     * @param _pos x,y position of first fitting space
     * @param _width rectangle width
     * @param _height rectangle height
     * @return true if fits
     */
    private boolean checkNextLines(Point _pos, int _width, int _height){
        boolean out = true;
        for(int i = 1;i<_height;i++){ //check lines after first fit for given height
            if(_pos.y + i >= used.size()) //if reached end of rows, will fit if before was no issue
                break;
            for(int k = 0;k<_width;k++){ //goes through each row from give positions for the needed width
                if(used.get(_pos.y+i)[_pos.x+k]){
                    out = false;
                    break;
                }
            }
        }
        return out;
    }

    /**
     * Marks the given rectangle as used in the array, may add more rows to array
     * @param _pos x,y
     * @param _width width
     * @param _height height
     */
    public void addRectangle(Point _pos, int _width, int _height){
        int rep = _pos.y+_height-used.size();
        for(int i = 0;i<rep;i++){ //Add needed rows
            used.add(new boolean[columns]);
        }

        for(int i = 0;i<_height;i++){ //used state to true for given area
            for(int k = 0;k<_width;k++){
                used.get(_pos.y+i)[_pos.x+k]=true;
            }
        }
    }


    public int getWidthPx(int _width){
        return _width * cellwidth;
    }

    public int getHeightPx(int _height){
        return _height * cellheight;
    }
}
