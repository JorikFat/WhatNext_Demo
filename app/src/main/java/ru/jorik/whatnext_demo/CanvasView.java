package ru.jorik.whatnext_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class CanvasView extends View implements CanvasViewInterface{
    private MainActivity mainActivity;
    private Paint paint;
    private Canvas canvas;

    // конструктор
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mainActivity = (MainActivity) context;


        initPaint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            mainActivity.touch(event);
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;

        mainActivity.drawAllCoins();

        if (!mainActivity.orderIsShowing){
            mainActivity.showOrderCoins.execute();
            mainActivity.orderIsShowing = true;
        }
        super.onDraw(canvas);
    }

    public void showMessage(String message){
        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
    }

    public void showMessage(int w, int l){
        String message = "Побед: " + w + '\n' +
                "Проигрышей: " + l;
        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
    }


    @Override
    public void drawCircle(Coin coin) {
        paint.setColor(coin.getColor());
        canvas.drawCircle(coin.getX(), coin.getY(), Setting.coinRadius, paint);
    }

    //Utils
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setCanvas(Canvas canvas){
        this.canvas = canvas;
    }


}