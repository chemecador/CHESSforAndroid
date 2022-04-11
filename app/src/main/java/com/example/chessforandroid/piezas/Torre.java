package com.example.chessforandroid.piezas;

import com.example.chessforandroid.R;

public class Torre extends Pieza {
    
    private boolean posInicial;

    public Torre(int x, int y, boolean blancas) {
        this.blancas = blancas;
        this.x = x;
        this.y = y;
        this.tag = "TORRE";
        this.posInicial = true;
        if (blancas) {
            this.drawable = R.drawable.btorre;
        } else {
            this.drawable = R.drawable.ntorre;
        }
    }
}