package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Alfil extends Pieza {
    public Alfil(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        this.tag = "ALFIL";
        if (blancas) {
            this.drawable = R.drawable.balfil;
        } else {
            this.drawable = R.drawable.nalfil;
        }
    }
}