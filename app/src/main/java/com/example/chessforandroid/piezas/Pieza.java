package com.example.chessforandroid.piezas;

public abstract class Pieza {
    protected boolean blancas;
    protected int drawable;
    protected String tag = "";

    public boolean isBlancas() {
        return blancas;
    }


    public void setBlancas(boolean blancas) {
        this.blancas = blancas;
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
