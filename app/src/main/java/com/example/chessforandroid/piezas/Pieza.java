package com.example.chessforandroid.piezas;

public abstract class Pieza {
    protected boolean blancas;
    protected int fila;
    protected int columna;
    protected int drawable;
    protected String tag = "";

    public boolean isBlancas() {
        return blancas;
    }


    public void setBlancas(boolean blancas) {
        this.blancas = blancas;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
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
