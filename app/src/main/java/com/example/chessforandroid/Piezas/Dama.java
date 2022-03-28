package com.example.chessforandroid.Piezas;

import com.example.chessforandroid.R;

public class Dama extends Pieza {
    public Dama(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        if (blancas) {
            this.drawable = R.drawable.bdama;
        } else {
            this.drawable = R.drawable.ndama;
        }
    }
}
