package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Alfil.
 */
public class Alfil extends Pieza {

    public Alfil(boolean blancas, boolean local) {
        this.blancas = blancas;
        this.tag = "ALFIL";
        this.local = local;

        if (blancas) {
            this.drawable = R.drawable.balfil;
        } else {
            if (local) {
                this.drawable = R.drawable.lnalfil;
            } else {
                this.drawable = R.drawable.nalfil;
            }
        }
    }
}