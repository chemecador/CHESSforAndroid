package com.example.chessforandroid;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.example.chessforandroid.Piezas.Pieza;

public class Casilla extends androidx.appcompat.widget.AppCompatImageButton {
    private int fila;
    private int columna;
    private Pieza pieza;

    public Casilla(@NonNull Context context, int x, int y) {
        super(context);
        this.fila = x;
        this.columna= y;
        this.pieza = null;
    }

    public int getFila() {
        return this.fila;
    }

    public void setX(int x) {
        this.fila = x;
    }
    
    public int getColumna() {
        return this.columna;
    }

    public void setY(int y) {
        this.columna= y;
    }

    public Pieza getPieza() {
        return pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }
}
