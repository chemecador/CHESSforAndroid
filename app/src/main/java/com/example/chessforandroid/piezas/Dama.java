package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Dama.
 */
public class Dama extends Pieza {

    public Dama(boolean blancas, boolean local) {

        this.blancas = blancas;
        this.tag = "DAMA";
        this.local = local;
        if (blancas) {
            this.drawable = R.drawable.bdama;
        } else {
            if (local) {
                this.drawable = R.drawable.lndama;
            } else {
                this.drawable = R.drawable.ndama;
            }
        }
    }
}
