package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Alfil.
 */
public class Alfil extends Pieza {
    public Alfil(boolean blancas) {
        this.blancas = blancas;
        this.tag = "ALFIL";
        if (blancas) {
            this.drawable = R.drawable.balfil;
        } else {
            this.drawable = R.drawable.nalfil;
        }
    }
}