package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Torre extends Pieza {

    /**
     * Si se ha movido esa torre, ya no se puede enrocar hacia ese lado.
     */
    public boolean haMovido;

    public Torre(boolean blancas) {
        this.blancas = blancas;
        this.tag = "TORRE";
        this.haMovido = false;
        if (blancas) {
            this.drawable = R.drawable.btorre;
        } else {
            this.drawable = R.drawable.ntorre;
        }
    }
}