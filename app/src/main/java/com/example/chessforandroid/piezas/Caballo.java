package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Caballo.
 */
public class Caballo extends Pieza {

    public Caballo(boolean blancas, boolean local) {

        this.blancas = blancas;
        this.tag = "CABALLO";
        this.local = local;

        if (blancas) {
            this.drawable = R.drawable.bcaballo;
        } else {
            if (local) {
                this.drawable = R.drawable.lncaballo;
            } else {
                this.drawable = R.drawable.ncaballo;
            }
        }
    }
}