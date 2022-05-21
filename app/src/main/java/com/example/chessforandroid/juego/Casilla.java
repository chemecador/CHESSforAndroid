package com.example.chessforandroid.juego;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.chessforandroid.piezas.*;

/**
 * Clase Casilla. Contiene los elementos propios de una casilla.
 */
public class Casilla extends androidx.appcompat.widget.AppCompatImageButton {

    private final int fila;
    private final int columna;
    private Pieza pieza;

    public Casilla(@NonNull Context context, int x, int y) {
        super(context);
        this.fila = x;
        this.columna= y;
        this.pieza = null;
    }

    /**
     * Crea y devuelve una copia de esta casilla.
     * @return Casilla
     */
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

    public int getColumna() {
        return this.columna;
    }


    public Pieza getPieza() {
        return pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }
}
