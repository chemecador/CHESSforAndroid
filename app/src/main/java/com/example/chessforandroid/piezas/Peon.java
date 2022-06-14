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

    public Peon(boolean blancas, boolean local) {

        this.blancas = blancas;
        this.tag = "PEON";
        this.pasable = false;
        this.local = local;
        if (blancas) {
            this.drawable = R.drawable.bpeon;
        } else {
            if (local) {
                this.drawable = R.drawable.lnpeon;
            } else {
                this.drawable = R.drawable.npeon;
            }
        }
    }
}
