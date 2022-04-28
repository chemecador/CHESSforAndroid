package com.example.chessforandroid;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chessforandroid.piezas.Alfil;
import com.example.chessforandroid.piezas.Caballo;
import com.example.chessforandroid.piezas.Dama;
import com.example.chessforandroid.piezas.Peon;
import com.example.chessforandroid.piezas.Pieza;
import com.example.chessforandroid.piezas.Rey;
import com.example.chessforandroid.piezas.Torre;

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

    public Casilla clonarCasilla(){
        Casilla clon = new Casilla(getContext(),fila,columna);
        if (this.getPieza() != null) {
            switch (this.getPieza().getTag()) {
                case "REY":
                    clon.setPieza(new Rey(this.getPieza().isBlancas()));
                    break;
                case "DAMA":
                    clon.setPieza(new Dama(this.getPieza().isBlancas()));
                    break;
                case "ALFIL":
                    clon.setPieza(new Alfil(this.getPieza().isBlancas()));
                    break;
                case "CABALLO":
                    clon.setPieza(new Caballo(this.getPieza().isBlancas()));
                    break;
                case "TORRE":
                    clon.setPieza(new Torre(this.getPieza().isBlancas()));
                    break;
                case "PEON":
                    clon.setPieza(new Peon(this.getPieza().isBlancas()));
                    break;
            }
        }
        return clon;
    }

    public int getFila() {
        return this.fila;
    }

    public void setFila(int x) {
        this.fila = x;
    }

    public int getColumna() {
        return this.columna;
    }

    public void setColumna(int y) {
        this.columna= y;
    }

    public Pieza getPieza() {
        return pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }
}
