package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Rey extends Pieza {
    public Rey(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        this.tag = "REY";
        if (blancas) {
            this.drawable = R.drawable.brey;
        } else {
            this.drawable = R.drawable.nrey;
        }
    }
}