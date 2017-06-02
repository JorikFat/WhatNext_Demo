package ru.jorik.whatnext_demo;

/**
 * Created by 111 on 10.05.2017.
 */

public class Setting {

/*
    public static class Coord2 {
        int x;
        int y;

        Coord2(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
*/
    public static int startGames;
    public static int winGames;
    public static int looseGames;
    public static int notFinishedGames;

    public static int rows = 4;
    public static int colums = 3;

    public static int coinRadius; //сохранено в SharedPreferences
    public static int widthScreen; //сохранено в SharedPreferences
    public static int heightScreen; //сохранено в SharedPreferences

    public static int milisecondsWait = 1000;
}
