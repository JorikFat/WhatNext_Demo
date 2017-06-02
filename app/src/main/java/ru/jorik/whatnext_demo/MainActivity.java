package ru.jorik.whatnext_demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //todo переписать методы под interface
    enum Result {Win, Lose}
    enum State {Playing, Finish}

    SharedPreferences dimenPrefs, statsPrefs;
    int win, lose, gameStarts;

    CanvasViewInterface canvasI;
    CanvasView canvas;
    Coin[][] coins;

    int numActiveCoins;
    Coord2[] order;
    int selectIndex = 0;
    ShowOrderCoins showOrderCoins = new ShowOrderCoins();
    boolean orderIsShowing = false;
    State currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dimenPrefs = getSharedPreferences("main_settings", MODE_PRIVATE);
        statsPrefs = getSharedPreferences("statistic", MODE_PRIVATE);

        initWidthAndHeight();
        calculateRadiusCoin();
        initStatistic();
        initCoins();
        initOrder();

        showLog(); //todo Убрать Log


        canvas = new CanvasView(this, null);
        canvasI = new CanvasView(this, null);
        setContentView(canvas); //// TODO: 01.06.2017 заменить на интерфейс
    }


    @Override
    protected void onStop() {
        writeStatistic();
        super.onStop();
    }

    //Utils:
    public void touch(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();


        if (currentState == State.Playing) {
            mark:
            for (int i = 0; i < Setting.colums; i++) {
                for (int j = 0; j < Setting.rows; j++) {
                    if (inCircle(coins[i][j], x, y)) {
                        if (!coins[i][j].isSelected()) {
                            coins[i][j].select();

                            if (order[selectIndex].equals(coins[i][j].getPosition())) { // правильная последовательность
                                coins[i][j].setColor(Color.GREEN);
                                if (selectIndex == order.length - 1) {
                                    gameOver(Result.Win);
                                }
                                selectIndex++;

                            } else {
                                coins[i][j].setColor(Color.RED);
                                gameOver(Result.Lose);

                            }

                            break mark;
                        }
                    }
                }
            }
        } else if (currentState == State.Finish){
            showGameOverDialog();
        }
    }

    void gameOver(Result result) {
        currentState = State.Finish;
        switch (result){
            case Win:
                canvas.showMessage("Победа"); //// TODO: 01.06.2017 заменить на интерфейс
                win++;
                break;
            case Lose:
                canvas.showMessage("Проигрыш"); //// TODO: 01.06.2017 заменить на интерфейс
                lose++;
                break;
            default:
        }
        setAllCoins(true);
        writeStatistic();



        showGameOverDialog();
    }



    private void initStatistic() {
        win = statsPrefs.getInt("win", 0);
        lose = statsPrefs.getInt("lose", 0);
        gameStarts = statsPrefs.getInt("gameStarts", 0);
    }

    private void writeStatistic(){
        SharedPreferences.Editor editor = statsPrefs.edit();
        editor.putInt("win", win);
        editor.putInt("lose", lose);
        editor.putInt("gameStarts", gameStarts);
        editor.apply();
    }

    private void initOrder(){
        Random tempRandom = new Random();
        numActiveCoins = tempRandom.nextInt(7) + 3;

        order = new Coord2[numActiveCoins];

        for (int i=0; i<numActiveCoins; i++){
            order[i] = getIndividualNumber();
        }
    }

    private boolean inCircle(Coin coin, int tx, int ty){
        int u1 = (tx-coin.getX())*(tx-coin.getX()) + (ty-coin.getY())*(ty-coin.getY());
        int u2 = Setting.coinRadius * Setting.coinRadius;
        return u1 <= u2;
    }

    private void initCoins(){
        coins = new Coin[Setting.colums][Setting.rows];
        int r = Setting.coinRadius;
        int col = Setting.colums;
        int row = Setting.rows;

        int centerScreenHorizontal = Setting.widthScreen / 2;
        int centerScreenVertical = Setting.heightScreen / 2;


        int posX1 = (int) (centerScreenHorizontal - 2*r - 2*r*0.1);
        int posY1 = (int) (centerScreenVertical - 3*r - 3*r*0.1);


        for (int i=0; i<row; i++){
            for (int ii=0; ii<col; ii++){
                int x = (int) (posX1 + ii*r*2 + ii*r*0.1);
                int y = (int) (posY1 + i*r*2 + i*r*0.1);
                coins[ii][i] = new Coin(x, y);
                coins[ii][i].setPosition(ii, i);
            }
        }
    }

    public void drawAllCoins(){
        for (Coin[] cc : coins)
            for (Coin c : cc){
                canvas.drawCircle(c); //// TODO: 01.06.2017 заменить на интерфейс
            }

    }

    private void initWidthAndHeight(){
        if (readScreenParam()){
            return;
        }
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Setting.heightScreen = point.y;
        Setting.widthScreen = point.x;
        writeParamsScreen();
    }

    private void calculateRadiusCoin(){
        if (readRadiusFromPreferences()){
            return;
        }
        int width = Setting.widthScreen;
        int height = Setting.heightScreen;

        int r1 = (int) (width/6.6)/2; //6.6 = 6R + 0.6R = 2R + 4R + 6S = 2R + 4R + 3R * 0.2
        int r2 = (int) (height/5.4)/2; //5.4 = 5R + 0.4R = 2R + 3R + 4S = 2R + 3R + 2R * 0.2
        if (r1 >= r2) Setting.coinRadius = r2;
        else Setting.coinRadius = r1;


        writeRadiusCoin();


    }

    private void showLog() { //todo Убрать Log
        for (Coord2 s : order){
            Log.i("order", "" + s.x + " " + s.y);
        }
    }

    //subUtils:
    private void showGameOverDialog() {
        String message = "Игр начато: " + gameStarts + "\n"
                + "Выйграно: " + win + "\n"
                + "Проиграно: " + lose;
        new AlertDialog.Builder(this)
                .setTitle("Игра завершена!")
                .setMessage(message)
                .setPositiveButton("Заново", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        orderIsShowing = false;
                        selectIndex = 0;
                        setAllCoinsColor(Color.BLACK);
                        initOrder();
                        showLog();//todo убрать Log
                        dialogInterface.cancel();
                        canvas.invalidate(); //// TODO: 01.06.2017 заменить на интерфейс
                    }
                })
                .setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                })
                .create().show();

    }

    private void setAllCoinsColor (int color){
        for (Coin[] cc : coins)
            for (Coin c : cc)
                c.setColor(color);
    }

    private void setAllCoins(boolean s){
        for (Coin[] cc : coins)
            for (Coin c : cc)
                c.select(s);
    }

    private Coord2 getIndividualNumber(){
        Coord2 item = randomItem();
        if (orderHasItem(item)){
            item = getIndividualNumber();
        }
        return item;
    }

    private Coord2 randomItem(){
        Random random = new Random();
        int a1 = random.nextInt(Setting.colums);
        int a2 = random.nextInt(Setting.rows);
        return new Coord2(a1, a2);
    }

    private boolean orderHasItem(Coord2 item){
        for (Coord2 i : order){
            if (item.equals(i)){
                return true;
            }
        }
        return false;
    }

    private boolean readScreenParam(){//заглушка
        if (dimenPrefs.contains("heightScreen") && dimenPrefs.contains("widthScreen")){
            Setting.widthScreen = dimenPrefs.getInt("widthScreen", 10);
            Setting.heightScreen = dimenPrefs.getInt("heightScreen", 10);
            return true;
        }
        return false;
    }

    private void writeParamsScreen(){
        SharedPreferences.Editor editor = dimenPrefs.edit();
        editor.putInt("widthScreen", Setting.widthScreen);
        editor.putInt("heightScreen", Setting.heightScreen);
        editor.apply();
    }

    private boolean readRadiusFromPreferences(){
        if (dimenPrefs.contains("coinRadius")){
            Setting.coinRadius = dimenPrefs.getInt("coinRadius", 30);
            return true;
        }
        return false;
    }

    private void writeRadiusCoin(){
        SharedPreferences.Editor editor = dimenPrefs.edit();
        editor.putInt("coinRadius", Setting.coinRadius);
        editor.apply();
    }

    class ShowOrderCoins extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            setAllCoins(true);
            gameStarts++;
            currentState = State.Playing;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            waitMilliseconds(Setting.milisecondsWait);
            for (Coord2 s : order){
                int y = s.y;
                int x = s.x;
                coins[x][y].setColor(Color.BLUE);
                publishProgress();
                waitMilliseconds(Setting.milisecondsWait);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            canvas.drawCircle(coins[0][0]); //// TODO: 01.06.2017 заменить на интерфейс
            canvas.invalidate(); //// TODO: 01.06.2017 заменить на интерфейс
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setAllCoinsColor(Color.BLACK);
            setAllCoins(false);
            canvas.invalidate(); //// TODO: 01.06.2017 заменить на интерфейс
            //Костыль: добавляет возможность повторного вызова AsynkTask
            showOrderCoins = new ShowOrderCoins();
            super.onPostExecute(aVoid);
        }

        private void waitMilliseconds(long l){
            try {
                TimeUnit.MILLISECONDS.sleep(l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}