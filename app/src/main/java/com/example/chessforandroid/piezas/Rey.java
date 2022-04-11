package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Rey extends Pieza {

    private boolean posInicial;

    public Rey(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        this.tag = "REY";
        this.posInicial = true;
        if (blancas) {
            this.drawable = R.drawable.brey;
        } else {
            this.drawable = R.drawable.nrey;
        }
    }
}