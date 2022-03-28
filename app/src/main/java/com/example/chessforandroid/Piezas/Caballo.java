package com.example.chessforandroid.Piezas;

import com.example.chessforandroid.R;

public class Caballo extends Pieza {
    public Caballo(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        if (blancas) {
            this.drawable = R.drawable.bcaballo;
        } else {
            this.drawable = R.drawable.ncaballo;
        }
    }
}