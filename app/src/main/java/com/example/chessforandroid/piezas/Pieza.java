package com.example.chessforandroid.piezas;

/**
 * Clase abstracta Pieza. Contiene los elementos propios de una pieza,
 * que seran heredados por cada pieza del tablero.
 */
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

}
