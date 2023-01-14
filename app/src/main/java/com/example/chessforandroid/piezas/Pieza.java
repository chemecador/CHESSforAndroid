package com.example.chessforandroid.piezas;

/**
 * Clase abstracta Pieza. Contiene los elementos propios de una pieza,
 * que seran heredados por cada pieza del tablero.
 */
public abstract class Pieza {
    protected boolean blancas;
    protected boolean invertida;
    protected int drawable;
    protected String tag = "";

    public boolean isBlancas() {
        return blancas;
    }

    public void setBlancas(boolean blancas) {
        this.blancas = blancas;
    }

    public boolean isInvertida() {
        return invertida;
    }

    public void setInvertida(boolean invertida) {
        this.invertida = invertida;
        setNewDrawable();
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

    public abstract void setNewDrawable();



}
