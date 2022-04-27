package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Rey extends Pieza {

    public boolean haMovido;
    public boolean enJaque;

    public Rey(boolean blancas) {
        this.blancas = blancas;
        this.tag = "REY";
        this.haMovido = false;
        this.enJaque = false;
        if (blancas) {
            this.drawable = R.drawable.brey;
        } else {
            this.drawable = R.drawable.nrey;
        }
    }
}