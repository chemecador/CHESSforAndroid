package com.example.chessforandroid.Piezas;

public abstract class Pieza {
    protected boolean blancas;
    protected int x;
    protected int y;
    protected int drawable;


    public boolean isBlancas() {
        return blancas;
    }

    public void setBlancas(boolean blancas) {
        this.blancas = blancas;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
