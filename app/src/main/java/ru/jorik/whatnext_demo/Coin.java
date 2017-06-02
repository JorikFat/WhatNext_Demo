package ru.jorik.whatnext_demo;

import android.graphics.Color;

/**
 * Created by 111 on 23.08.2016.
 */
public class Coin {
    public static int radius;
    private boolean selected;
    private int color = Color.BLACK;
    private Coord2 coords;
    private Coord2 position;

    public Coin(int x, int y){
        this.coords = new Coord2(x, y);
        this.position = new Coord2();
        selected = false;
    }



    public int getRadius() {
        return radius;
    }

/*
    public void setRadius(int radius) {
        this.radius = radius;
    }
*/

    public int getColor() {
        return color;
    }

    public int getX() {
        return coords.x;
    }

    public void setX(int x) {
        this.coords.x = x;
    }

    public int getY() {
        return coords.y;
    }

    public void setY(int y) {
        this.coords.y = y;
    }

    public boolean isSelected(){
//        this.color = Color.BLUE;
        return selected;
    }

    public void select() {
        if (!selected){
            selected = true;
            this.color = Color.BLUE;
        }
    }

    public void select(boolean s){
            selected = s;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Coord2 getCoords(){
        return coords;
    }

    public void setPosition(int x, int y){
        position.x = x;
        position.y = y;
    }

    public Coord2 getPosition() {
        return position;
    }
}
