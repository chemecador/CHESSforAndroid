package com.example.chessforandroid.piezas;

public abstract class Pieza {
    protected boolean blancas;
    protected int x;
    protected int y;
    protected int drawable;
    protected String tag = "";


    public void mover(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
