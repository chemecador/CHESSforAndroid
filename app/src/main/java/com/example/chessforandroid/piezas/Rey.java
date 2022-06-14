package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

/**
 * Clase Rey.
 */
public class Rey extends Pieza {

    /**
     * Si se ha movido el rey, ya no puede enrocar.
     */
    public boolean haMovido;

    public Rey(boolean blancas, boolean local) {

        this.blancas = blancas;
        this.tag = "REY";
        this.haMovido = false;
        this.local = local;

        if (blancas) {
            this.drawable = R.drawable.brey;
        } else {
            if (local) {
                this.drawable = R.drawable.lnrey;
            } else {
                this.drawable = R.drawable.nrey;
            }
        }
    }
}