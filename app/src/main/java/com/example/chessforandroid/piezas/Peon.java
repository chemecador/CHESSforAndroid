package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Peon extends Pieza {

    public Peon(boolean blancas) {
        this.blancas = blancas;
        this.tag = "PEON";
        if (blancas) {
            this.drawable = R.drawable.bpeon;
        } else {
            this.drawable = R.drawable.npeon;
        }
    }
}
