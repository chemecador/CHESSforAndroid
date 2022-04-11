package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Dama extends Pieza {
    public Dama(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        this.tag = "DAMA";
        if (blancas) {
            this.drawable = R.drawable.bdama;
        } else {
            this.drawable = R.drawable.ndama;
        }
    }
}