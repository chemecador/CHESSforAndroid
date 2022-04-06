package com.example.chessforandroid.Piezas;

import com.example.chessforandroid.R;

public class Torre extends Pieza {
    public Torre(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        this.tag = "TORRE";
        if (blancas) {
            this.drawable = R.drawable.btorre;
        } else {
            this.drawable = R.drawable.ntorre;
        }
    }
}