package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Caballo.
 */
public class Caballo extends Pieza {
    public Caballo(boolean blancas) {
        this.blancas = blancas;
        this.tag = "CABALLO";
        if (blancas) {
            this.drawable = R.drawable.bcaballo;
        } else {
            this.drawable = R.drawable.ncaballo;
        }
    }
}