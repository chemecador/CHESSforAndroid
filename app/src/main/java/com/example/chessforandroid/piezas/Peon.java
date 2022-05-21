package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Peon.
 */
public class Peon extends Pieza {

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
