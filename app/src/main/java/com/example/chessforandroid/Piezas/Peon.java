package com.example.chessforandroid.Piezas;

import com.example.chessforandroid.R;

public class Peon extends Pieza {
    public Peon(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        if (blancas) {
            this.drawable = R.drawable.bpeon;
        } else {
            this.drawable = R.drawable.npeon;
        }

    }
}
