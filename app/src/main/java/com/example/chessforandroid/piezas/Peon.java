package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Peon.
 */
public class Peon extends Pieza {

    /**
     * Booleano que almacena si el peon puede recibir la captura al paso
     */
    public boolean pasable;

    public Peon(boolean blancas) {
        this.blancas = blancas;
        this.tag = "PEON";
        this.pasable = false;
        if (blancas) {
            this.drawable = R.drawable.bpeon;
        } else {
            this.drawable = R.drawable.npeon;
        }
    }
}
