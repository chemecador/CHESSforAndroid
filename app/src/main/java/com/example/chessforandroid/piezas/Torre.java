package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Torre extends Pieza {

    /**
     * Si se ha movido esa torre, ya no se puede enrocar hacia ese lado.
     */
    public boolean haMovido;

    public Torre(boolean blancas, boolean local) {

        this.blancas = blancas;
        this.tag = "TORRE";
        this.haMovido = false;
        this.local = local;

        if (blancas) {
            this.drawable = R.drawable.btorre;
        } else {
            if (local) {
                this.drawable = R.drawable.lntorre;
            } else {
                this.drawable = R.drawable.ntorre;
            }
        }
    }
}