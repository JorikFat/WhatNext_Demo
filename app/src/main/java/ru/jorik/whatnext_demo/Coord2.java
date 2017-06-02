package ru.jorik.whatnext_demo;

/**
 * Created by 111 on 30.05.2017.
 */

public class Coord2 {
    int x;
    int y;

    Coord2(int x, int y){
        this.x = x;
        this.y = y;
    }

    Coord2(){}


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        else {
            Coord2 temp = (Coord2) obj;
            if (this.x == temp.x && this.y == temp.y)return true;
            else return false;
        }
    }
}